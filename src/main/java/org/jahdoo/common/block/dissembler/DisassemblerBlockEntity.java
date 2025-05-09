package org.jahdoo.common.block.dissembler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.client.overlay.CustomHudOverlay;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.ComponentReg;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;

import static org.jahdoo.common.registers.ItemReg.ESSENCE_FRAGMENT;
import static software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache;


public class DisassemblerBlockEntity extends AbstractTankUser implements GeoBlockEntity {

    private static final int RECYCLING_COST = 6;
    private final AnimatableInstanceCache cache = createInstanceCache(this);
    private int maxProgress = 200;

    public DisassemblerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.INFUSER_BE.get(), pos, state, 5);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> DisassemblerBlockEntity.this.progress;
                    case 1 -> DisassemblerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> DisassemblerBlockEntity.this.progress = pValue;
                    case 1 -> DisassemblerBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public int setInputSlots() {
        return 1;
    }

    @Override
    public int setOutputSlots() {
        return 2;
    }

    @Override
    public int getMaxSlotSizeInput() {
        return 1;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 64;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public int setCraftingCost() {
        return RECYCLING_COST;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state ->  PlayState.STOP));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("infuser.progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("infuser.progress");

    }

    public ItemStack getInputAndOutputRenderer() {
        if(this.outputItemHandler.getStackInSlot(0).isEmpty()) {
            return this.inputItemHandler.getStackInSlot(0);
        }
        return this.outputItemHandler.getStackInSlot(0);
    }

    private void shiftProcessedItemsToOutput(){
        var outputHandler = this.outputItemHandler;
        var inputHandler = this.inputItemHandler;
        var essenceFragment = ESSENCE_FRAGMENT.get();

        if (outputHandler.getStackInSlot(0).isEmpty() && inputHandler.getStackInSlot(0).is(essenceFragment)) {
            outputHandler.setStackInSlot(0, new ItemStack(essenceFragment));
            inputHandler.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        shiftProcessedItemsToOutput();
        assignTankBlockInRange(pLevel, pPos, RECYCLING_COST);

        if(this.hasTankAndFuel()){
            if (this.progress == maxProgress) {

                this.completedRecycling(pLevel);
            } else {
                this.recyclingProcess();
            }
        }
    }

    private void completedRecycling(Level level){
        var handler = this.inputItemHandler;
        var originalItem = handler.getStackInSlot(0);
        if(!(originalItem.getItem() instanceof JahdooItem jahdooItem)) return;
        var recycleItem = getRecycleWithChance(jahdooItem, originalItem);

        this.outputItemHandler.setStackInSlot(0, recycleItem);
        handler.setStackInSlot(0, ItemStack.EMPTY);
        this.chargeTankFuel(RECYCLING_COST);
        level.sendBlockUpdated(this.tankPosition, level.getBlockState(this.tankPosition), this.getBlockState(), 3);
        this.progress = 0;
    }

    private static ItemStack getRecycleWithChance(JahdooItem jahdooItem, ItemStack originalItem) {
        // Tries to get the rarity level from the item (likely an int or enum stored in a component).
        var getRarity = originalItem.get(ComponentReg.JAHDOO_RARITY);

        // If the item doesn't have a rarity set, always return the default recycle item.
        if(getRarity == null || jahdooItem.customRecycleChance(originalItem) >= 0) {
            var v = jahdooItem.customRecycleChance(originalItem);
            return Maths.percentageChance(v) ? jahdooItem.getRecycleItem() : ItemStack.EMPTY;
        }

        // Gets the durability percent. We're using just the percent here.
        var getPercent = CustomHudOverlay.getDurabilityWithColor(originalItem);

        // Checks if the item has a durability component (can be damaged).
        var hasDurability = originalItem.has(DataComponents.MAX_DAMAGE);

        // Base percentage chance: each rarity level increases the chance by 10%
        var percentageChance = ((getRarity + 1) * 10);

        // i is a decimal representation of the chance (e.g., 0.6 for 60%)
        var i = (double) percentageChance / 100;

        // i1 is how much durability is missing (e.g., 100 - 80% durability = 20% missing)
        var i1 = 100 - getPercent.getFirst();

        // If the item has durability, we reduce the chance based on missing durability
        // The more it's damaged, the lower the recycle chance.
        var durabilityAdjustment = hasDurability ? (i * i1) : 0;

        // Final adjusted chance, ensuring it's not negative.
        var actualChance = Math.max(percentageChance - durabilityAdjustment, 0);

        // Performs the random roll. Returns true with `actualChance` percent chance.
        var v = jahdooItem.customRecycleChance(originalItem);
        var recycleChance = Maths.percentageChance(v == -1 ? actualChance : v);

        // If the roll succeeded, return the recycled item. Otherwise, return nothing.
        return recycleChance ? jahdooItem.getRecycleItem() : ItemStack.EMPTY;
    }

    private void tableProcessingParticle(Level level, ServerLevel serverLevel, BlockPos pPos){
        Helpers.getSoundWithPosition(level, pPos, SoundEvents.BEACON_ACTIVATE, 0.05f, 2f);
        PositionFinders.getOuterRingOfRadiusRandom(pPos.getCenter(), 0.2, 50,
            worldPosition -> {
                var directions = worldPosition.subtract(pPos.getCenter()).normalize();
                ParticleHandlers.sendParticles(
                    serverLevel,
                    processingParticle(4, 0.5f, false, 0.3f),
                    worldPosition.add(0, 0.21, 0), 0, directions.x, directions.y, directions.z, 0.05
                );
            }
        );
    }

    private void recyclingProcess(){
        var isInputAugment = this.inputItemHandler.getStackInSlot(0);
        var isOutputEmpty = this.outputItemHandler.getStackInSlot(0).isEmpty();
        if (isOutputEmpty && isInputAugment.getItem() instanceof JahdooItem jahdooItem){
            this.progress++;

            if (this.getTankEntity().inputItemHandler.getStackInSlot(0).getCount() >= 6) {
                if(progress % 21 == 0){
                    if(!(this.level instanceof ServerLevel serverLevel)) return;
                    this.tableProcessingParticle(this.level, serverLevel, this.getBlockPos());
                }
            }

        } else {
            if(this.progress > 0) this.progress = 0;
        }
    }
}

