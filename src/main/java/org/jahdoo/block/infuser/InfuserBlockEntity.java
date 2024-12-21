package org.jahdoo.block.infuser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;


public class InfuserBlockEntity extends AbstractTankUser implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int maxProgress = 200;
    private static final int RECYCLING_COST = 6;

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

    public InfuserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.INFUSER_BE.get(), pPos, pBlockState, 5);
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

    public ItemStack getInputAndOutputRenderer() {
        if(this.outputItemHandler.getStackInSlot(0).isEmpty()) {
            return this.inputItemHandler.getStackInSlot(0);
        }
        return this.outputItemHandler.getStackInSlot(0);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("infuser.progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        progress = pTag.getInt("infuser.progress");

    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        this.shiftProcessedItemsToOutput();
        this.assignTankBlockInRange(pLevel, pPos, RECYCLING_COST);

        if(this.hasTankAndFuel()){
            if (this.progress == maxProgress) {
                this.completedRecycling(pLevel);
            } else {
                this.recyclingProcess();
            }
        }
    }

    private void shiftProcessedItemsToOutput(){
        if (this.outputItemHandler.getStackInSlot(0).isEmpty() && this.inputItemHandler.getStackInSlot(0).is(ItemsRegister.AUGMENT_FRAGMENT.get())) {
            this.outputItemHandler.setStackInSlot(0, new ItemStack(ItemsRegister.AUGMENT_FRAGMENT.get()));
            this.inputItemHandler.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    private void recyclingProcess(){
        var isInputAugment = this.inputItemHandler.getStackInSlot(0).is(ItemsRegister.AUGMENT_ITEM.get());
        var isOutputEmpty = this.outputItemHandler.getStackInSlot(0).isEmpty();
        if (isOutputEmpty && isInputAugment){
            this.progress++;

            if (this.getTankEntity().inputItemHandler.getStackInSlot(0).getCount() >= 6) {
                if (this.inputItemHandler.getStackInSlot(0).copy().is(ItemsRegister.AUGMENT_ITEM.get())) {
                    if(progress % 21 == 0){
                        if(!(this.level instanceof ServerLevel serverLevel)) return;
                        this.tableProcessingParticle(this.level, serverLevel, this.getBlockPos());
                    }
                }
            }
        } else {
            if(this.progress > 0) this.progress = 0;
        }
    }

    private void tableProcessingParticle(Level pLevel, ServerLevel serverLevel, BlockPos pPos){
        ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.BEACON_ACTIVATE, 0.05f, 2f);
        PositionGetters.getOuterRingOfRadiusRandom(pPos.getCenter(), 0.2, 50,
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

    private void completedRecycling(Level level){
        var getRandom = ModHelpers.Random.nextInt(0,20);
        var itemCore = ItemsRegister.AUGMENT_CORE.get();
        var itemFragment = ItemsRegister.AUGMENT_FRAGMENT.get();
        this.inputItemHandler.setStackInSlot(0, ItemStack.EMPTY);
        this.outputItemHandler.setStackInSlot(0, new ItemStack(getRandom == 0 ? itemCore : itemFragment));
        this.chargeTankFuel(RECYCLING_COST);
        level.sendBlockUpdated(this.tankPosition, level.getBlockState(this.tankPosition), this.getBlockState(), 3);
        this.progress = 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state ->  PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public int setCraftingCost() {
        return RECYCLING_COST;
    }
}

