package org.jahdoo.common.block.mystical_augmenter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.Helpers;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class MysticalAugmenterBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(4, 4, 4, 12, 12, 12);
    public static final BooleanProperty[] SIDES_CONNECTED = new BooleanProperty[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};


    public MysticalAugmenterBlock() {
        super(Properties.of().strength(1f).sound(SoundType.DEEPSLATE_BRICKS).noOcclusion());
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(DOWN, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE)
                .setValue(NORTH, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
        );
    }

//    @Override
//    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
//        var direction = context.getNearestLookingDirection();
//        return this.defaultBlockState().setValue(Arrays.stream(SIDES_CONNECTED).reduce(direction).get(), TRUE);
//    }


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIDES_CONNECTED);
    }

    public static Direction property(BlockState state){
        for (var booleanProperty : SIDES_CONNECTED) {
            var isDirection = state.getValue(booleanProperty);
            if(isDirection) return directionFor(booleanProperty);
        }

        return null;
    }

    public static BooleanProperty propertyFor(Direction dir) {
        return switch (dir) {
            case DOWN  -> BlockStateProperties.DOWN;
            case UP    -> BlockStateProperties.UP;
            case NORTH -> BlockStateProperties.NORTH;
            case SOUTH -> BlockStateProperties.SOUTH;
            case WEST  -> BlockStateProperties.WEST;
            case EAST  -> BlockStateProperties.EAST;
        };
    }

    public static Direction directionFor(BooleanProperty property) {
        return switch (property.getName()) {
            case "down"  -> Direction.DOWN;
            case "up"    -> Direction.UP;
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "west"  -> Direction.WEST;
            case "east"  -> Direction.EAST;
            default -> throw new IllegalArgumentException(
                "Unknown BooleanProperty: " + property.getName()
            );
        };
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */

    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        var direction = context.getNearestLookingDirection();
        return defaultBlockState().setValue(propertyFor(direction), true);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new MysticalAugmenterBlock());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasDynamicShape() {
        return false;
    }

//    @Override
//    protected BlockState rotate(BlockState state, Rotation rotation) {
//        return state.setValue(FACING, state.getValue(FACING));

//    }
//
//    @Override
//    protected BlockState mirror(BlockState state, Mirror mirror) {
//        return state.rotate(mirror.getRotation(state.getValue(FACING)));
//    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MysticalAugmenterEntity(pos,state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return InteractionResult.CONSUME;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType,
            BlockEntityReg.MYSTICAL_AUGMENTER_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
//        var posOfAttached = pos.relative(state.getValue(FACING));
//        PositionFinders.getOuterSquareOfRadius(posOfAttached.getCenter().subtract(0,0.5,0), 0.55, 20,
//            positions -> {
//                var element = ElementReg.utility();
//                int col1 = element.partColourA();
//                int col2 = element.partColourFade();
//                var directions = posOfAttached.getCenter().subtract(posOfAttached.getCenter()).normalize();
//                var lifetime = 10;
//                var size = 2;
//
//                var genericParticle = ParticleHandlers.genericParticle(SOFT_PARTICLE, lifetime, (float) (size - 0.2), col1, col2, false);
//                var speedRange = Random.nextDouble(2, 3);
//                ParticleHandlers.sendParticles(
//                    level, genericParticle, positions, 0, directions.x, directions.y+0.05, directions.z, speedRange
//                );
//            }
//        );
        Helpers.getSoundWithPosition(level, pos, SoundReg.DASH_EFFECT_INSTANT.get(), SoundSource.BLOCKS, 0.5F, 2F);
        Helpers.getSoundWithPosition(level, pos, SoundReg.LEVITATE.get(), SoundSource.BLOCKS, 0.8F, 1F);
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }
}

