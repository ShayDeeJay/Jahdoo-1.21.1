package org.jahdoo.common.block.mystical_augmenter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.block.enchanted_block.ConverterValues;
import org.jahdoo.common.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import static org.jahdoo.common.block.mystical_augmenter.MysticalAugmenterBlock.property;
import static org.jahdoo.common.entities.EntityAnimations.*;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE;
import static org.jahdoo.common.registers.mod.ElementReg.utility;


public class MysticalAugmenterEntity extends AbstractTankUser implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MysticalAugmenterEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.MYSTICAL_AUGMENTER_BE.get(), pos, state, 1);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public int setCraftingCost() {
        return 1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 0, state -> state.setAndContinue(IDLE_BLOCK)));
        controllers.add(new AnimationController<>(this, "side_anim", 0, this::getPlayState));
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        this.assignTankBlockInRange(level, pos, setCraftingCost());

        if(!level.isClientSide){
            if(this.hasTankAndFuel()){
                enchantedBlockConverter(level, pos, blockState);
            }
        }
    }

    private void enchantedBlockConverter(Level level, BlockPos pos, BlockState blockState) {
        var property = property(blockState);
        if(property != null){
            var relative = pos.relative(property);
            var state = level.getBlockState(relative);
            if(level instanceof ServerLevel serverLevel){
                if(ConverterValues.isConvertibleBlock(state.getBlock())){
                    level.setBlockAndUpdate(relative, BlockReg.ENCHANTED_BLOCK.get().defaultBlockState());
                    if (level.getBlockEntity(relative) instanceof EnchantedBlockEntity enchantedBlockEntity) {
                        if (!state.isAir()) {
                            chargeTankFuel(1);
                            BlockPos diff = relative.subtract(getBlockPos());
                            Direction dir = Direction.fromDelta(diff.getX(), diff.getY(), diff.getZ());
                            enchantedBlockEntity.setBlockType(state.getBlock(), 0, dir);
                        }
                    }

                    PositionFinders.getCubeCornersAndFaceCenters(
                        relative,0.8, pos1 -> {
                            var directions = pos.getCenter().subtract(pos1).normalize();
                            var particle = ParticleHandlers.genericParticle(MAGIC_PARTICLE, utility(), 20, 1f);
                            sendParticles(serverLevel, particle, pos1, 0, directions.x, directions.y, directions.z, 0.1);
                        }
                    );
                }
            }
        }
    }

    private static void speedTicksAttached(Level level, BlockPos pos, BlockState blockState) {
        var property = property(blockState);
        if(property != null){
            var relative = pos.relative(property);
            var blockEntity = level.getBlockEntity(relative);
            if (blockEntity instanceof BlockEntity entity) {
                for (int i = 0; i < 64; i++){
                    @SuppressWarnings("unchecked")
                    var ticker = entity.getBlockState().getTicker(level, (BlockEntityType<BlockEntity>) entity.getType());
                    if (ticker != null) {
                        ticker.tick(level, relative, entity.getBlockState(), blockEntity);
                    }
                }
            }
        }
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

        if(getLevel() != null && getLevel().getBlockState(this.getBlockPos().relative(direction)).isAir()){
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

