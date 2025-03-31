package org.jahdoo.common.block.infuser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.utils.ModTags;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;

import static org.jahdoo.common.registers.ItemReg.*;
import static software.bernie.geckolib.util.GeckoLibUtil.*;


public class InfuserBlockEntity extends AbstractTankUser implements GeoBlockEntity {

    private static final int RECYCLING_COST = 6;
    private final AnimatableInstanceCache cache = createInstanceCache(this);
    private int maxProgress = 200;

    public InfuserBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.INFUSER_BE.get(), pos, state, 5);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> InfuserBlockEntity.this.progress;
                    case 1 -> InfuserBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> InfuserBlockEntity.this.progress = pValue;
                    case 1 -> InfuserBlockEntity.this.maxProgress = pValue;
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
    public int getMaxSlotSize() {
        return 1;
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
        var itemFragment = ESSENCE_FRAGMENT.get();
        this.inputItemHandler.setStackInSlot(0, ItemStack.EMPTY);
        this.outputItemHandler.setStackInSlot(0, new ItemStack(itemFragment));
        this.chargeTankFuel(RECYCLING_COST);
        level.sendBlockUpdated(this.tankPosition, level.getBlockState(this.tankPosition), this.getBlockState(), 3);
        this.progress = 0;
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
        var isInputAugment = this.inputItemHandler.getStackInSlot(0).is(ModTags.Items.ESSENCE_FRAGMENT);
        var isOutputEmpty = this.outputItemHandler.getStackInSlot(0).isEmpty();
        if (isOutputEmpty && isInputAugment){
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

