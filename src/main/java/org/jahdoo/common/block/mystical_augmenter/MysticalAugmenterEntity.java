package org.jahdoo.common.block.mystical_augmenter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.attachments.ChaosCubeData;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import static net.minecraft.core.Direction.byName;
import static org.jahdoo.common.entities.EntityAnimations.*;


public class MysticalAugmenterEntity extends AbstractTankUser implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int ticker;
    private AbilityHolder holder;
    public ChaosCubeData getData = ChaosCubeData.initData();

    public MysticalAugmenterEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.MYSTICAL_AUGMENTER_BE.get(), pos, state, 1);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("infuser.progress", progress);
        AbilityHolder.writeTag(holder == null ? AbilityHolder.DEFAULT : holder, tag);
        if(this.getData != null) ChaosCubeData.saveChaosData(tag, this.getData);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("infuser.progress");
        this.holder = AbilityHolder.readTag(tag);
        var data1 = ChaosCubeData.loadChaosData(tag);
        this.getData = data1 == null ? ChaosCubeData.initData() : data1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 0, state -> state.setAndContinue(IDLE_BLOCK)));
        controllers.add(new AnimationController<>(this, "side_anim", 0, this::getPlayState));
    }

    @Override
    public int setCraftingCost() {
        return 64;
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        ticker++;
        if(this.ticker % 10 == 0) animateTicks(level);

//        speedTicksAttached(level, pos, blockState);
    }

    private static void speedTicksAttached(Level level, BlockPos pos, BlockState blockState) {
        for (int i = 0; i < 256; i++){
            var relative = pos.relative(blockState.getValue(FACING));
            var blockEntity = level.getBlockEntity(relative);
            if(blockEntity instanceof BlockEntity entity) {
                BlockEntityTicker<BlockEntity> ticker = entity.getBlockState().getTicker(level, (BlockEntityType<BlockEntity>) entity.getType());
                if(ticker != null){
                    ticker.tick(level, relative, entity.getBlockState(), blockEntity);
                }
            }
        }
    }

    private void animateTicks(Level level) {
        if(level.isClientSide) return;
        var radius = 0.55D;
        var positions = 0.2;
        PositionFinders.getRandomSphericalPositions(this.getBlockPos().getCenter(), radius, positions,
            poss -> {
                var directions = this.getBlockPos().getCenter().subtract(poss).normalize();
                var particle = ParticleHandlers.genericParticle(ParticleStore.MAGIC_PARTICLE, ElementReg.utility(), (int) (radius * 15), 0.8f);
                ParticleHandlers.sendParticles(level, particle, poss, 0, directions.x, directions.y, directions.z, Helpers.Random.nextFloat(0.05F, 0.14F));
            }
        );
    }

    public @NotNull BlockPos getDirectionPos(String directionName) {
        var direction = byName(directionName);
        return this.getBlockPos().relative(direction == null ? Direction.UP : direction);
    }

    private PlayState getPlayState(AnimationState<MysticalAugmenterEntity> state) {
        state.setControllerSpeed(3F);

        var bs = this.getBlockState();

        var anim = MYS_DOWN; // default
        var direction = Direction.DOWN;

        if (bs.getValue(BlockStateProperties.NORTH)) {
            anim = MYS_NORTH;
            direction = Direction.NORTH;
        }
        if (bs.getValue(BlockStateProperties.SOUTH)) {
            anim = MYS_SOUTH;
            direction = Direction.SOUTH;
        }
        if (bs.getValue(BlockStateProperties.EAST)) {
            anim = MYS_EAST;
            direction = Direction.EAST;
        }
        if (bs.getValue(BlockStateProperties.WEST)) {
            anim = MYS_WEST;
            direction = Direction.WEST;
        }
        if (bs.getValue(BlockStateProperties.UP)) {
            anim = MYS_UP;
            direction = Direction.UP;
        }

        if(getLevel().getBlockState(this.getBlockPos().relative(direction)).isAir()){
            return PlayState.STOP;
        } else {
            return state.setAndContinue(anim);
        }
    }



    @Override
    public int setInputSlots() {
        return 0;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeInput() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 0;
    }

}

