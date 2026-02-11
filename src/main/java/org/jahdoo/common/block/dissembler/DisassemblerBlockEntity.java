package org.jahdoo.common.block.dissembler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.client.overlay.CustomHudOverlay;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

import java.util.List;

import static net.minecraft.world.item.ItemStack.EMPTY;
import static org.jahdoo.common.entities.EntityAnimations.IDLE_DIS;
import static org.jahdoo.common.entities.EntityAnimations.SLAM;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.ItemReg.ESSENCE_FRAGMENT;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.processingParticle;
import static software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache;


public class DisassemblerBlockEntity extends AbstractTankUser implements GeoBlockEntity {

    private static final int RECYCLING_COST = 6;
    private static final int MAX_PROGRESS = 110;
    private final AnimatableInstanceCache cache = createInstanceCache(this);

    public DisassemblerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.INFUSER_BE.get(), pos, state, 5);
    }

    @Override
    public int setCraftingCost() {
        return RECYCLING_COST;
    }

    public ItemStack getInputAndOutputRenderer() {
        var outHandler = this.getOutputItemHandler().getStackInSlot(0);
        if(outHandler.isEmpty()) {
            return this.getInputItemHandler().getStackInSlot(0);
        }
        return outHandler;
    }

    private void shiftProcessedItemsToOutput(){
        var outputHandler = this.getOutputItemHandler();
        var inputHandler = this.getInputItemHandler();
        var essenceFragment = ESSENCE_FRAGMENT.get();

        if (outputHandler.getStackInSlot(0).isEmpty() && inputHandler.getStackInSlot(0).is(essenceFragment)) {
            outputHandler.setStackInSlot(0, new ItemStack(essenceFragment));
            inputHandler.setStackInSlot(0, EMPTY);
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        shiftProcessedItemsToOutput();
        assignTankBlockInRange(pLevel, pPos, RECYCLING_COST);

        if(this.progress == 0){
            var yColour = ColourHelpers.getAbsorptionYellow();
            var particleOptions = genericParticle(SOFT_PARTICLE, yColour, yColour, 5, 0.3F, false, 0);
            ParticleHandlers.particleBurst(level, pPos.getCenter().add(0,0.9,0), 1, particleOptions, 0.01f);
        }

        if(this.hasTankAndFuel()){
            if (this.progress == MAX_PROGRESS) {
                this.completedRecycling(pLevel);
            } else {
                this.recyclingProcess();
            }
        }
    }

    private void completedRecycling(Level level){
        var handler = this.getInputItemHandler();
        var oItem = handler.getStackInSlot(0);

        if(!(oItem.getItem() instanceof JahdooItem jItem)) return;
        var recycleItem = getRecycleWithChance(jItem, oItem);
        var tank = this.tankPosition;

        this.chargeTankFuel(RECYCLING_COST);
        handler.setStackInSlot(0, EMPTY);

        this.getOutputItemHandler().setStackInSlot(0, recycleItem.getFirst());
        this.getOutputItemHandler().setStackInSlot(1, recycleItem.get(1));
        level.sendBlockUpdated(tank, level.getBlockState(tank), this.getBlockState(), 3);
        this.progress = 0;
    }

    private static int clampChance(double v) {
        return (int) Math.max(0, Math.min(100, Math.round(v)));
    }

    private static List<ItemStack> getRecycleWithChance(JahdooItem jahdooItem, ItemStack originalItem) {

        var rarity = originalItem.get(ComponentReg.JAHDOO_RARITY);
        int custom =  jahdooItem.customRecycleChance(originalItem);
        var scrap = new ItemStack(ItemReg.GEAR_SCRAP);

        if(rarity == null && custom == -1) return List.of(EMPTY, EMPTY);

        // If no rarity OR custom chance is provided
        if (rarity == null || custom != -1) {
            int safe = clampChance(custom);
            var recycleItem = List.of(org.shaydee.shaydeeapi.Maths.percentageChance(safe) ? jahdooItem.getRecycleItem() : EMPTY, scrap.copyWithCount(2));
            var empty = List.of(EMPTY, scrap.copyWithCount(2));
            return org.shaydee.shaydeeapi.Maths.percentageChance(safe) ? recycleItem : empty;
        }

        var durabilityInfo = CustomHudOverlay.getDurabilityWithColor(originalItem);
        var hasDurability = originalItem.has(DataComponents.MAX_DAMAGE);

        var i = rarity + 5;
        int baseChance = i * 10;

        System.out.println("Base Chance");
        System.out.println(baseChance);

        System.out.println("Durability");
        System.out.println(durabilityInfo.getFirst());
        double missing = 100 - durabilityInfo.getFirst();

        System.out.println("Missing");
        System.out.println(missing);
        double reduction = hasDurability ? (baseChance / 100.0) * missing : 0;

        System.out.println("Reduction");
        System.out.println(reduction);
        double actualChance = baseChance - reduction;

        System.out.println("Actual Chance");
        System.out.println(actualChance);
        int safeChance = clampChance(actualChance);

        System.out.println("Safe Chance");
        System.out.println(safeChance);

        boolean success = org.shaydee.shaydeeapi.Maths.percentageChance(safeChance);

        var getNew = scrap.copyWithCount(i);
        return List.of(success ? jahdooItem.getRecycleItem() : EMPTY, getNew);
    }

    private void tableProcessingParticle(Level level, ServerLevel serverLevel, BlockPos pPos){
        PositionFinders.getOuterRingOfRadiusRandom(pPos.getCenter(), 0.2, 150,
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

    public void sharedSound(SoundEvent sEvent, Float volume, Float pitch){
        SoundHelpers.getSoundWithPosition(level, getBlockPos(), sEvent, SoundSource.BLOCKS, volume, pitch);
    }

    private void recyclingProcess(){
        var isInputAugment = this.getInputItemHandler().getStackInSlot(0);
        var isOutputEmpty = this.getOutputItemHandler().getStackInSlot(0).isEmpty();
        if (isOutputEmpty && isInputAugment.getItem() instanceof JahdooItem){
            this.progress++;

            if (this.getTankEntity().getInputItemHandler().getStackInSlot(0).getCount() >= 6) {
                if((19+progress) % 40 == 0){
                    sharedSound(SoundEvents.BASALT_BREAK, 1F, 1f);
                    sharedSound(SoundReg.IMPACT.get(), 1F, 1f);

                    if(!(this.level instanceof ServerLevel serverLevel)) return;
                    this.tableProcessingParticle(this.level, serverLevel, this.getBlockPos());
                }
            }

        } else {
            if(this.progress > 0) this.progress = 0;
        }
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this, "idle", 10, state -> state.setAndContinue(progress > 0 ? SLAM : IDLE_DIS))
        );
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
}

