package org.jahdoo.common.block.augment_modification_station;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.ModTags;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.block.BlockInteractionHandler;
import org.jahdoo.common.items.augments.Augment;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static net.minecraft.sounds.SoundEvents.VAULT_ACTIVATE;
import static org.jahdoo.common.registers.BlockReg.sharedBehaviour;

public class AugmentModificationBlock extends BaseEntityBlock{

    public static VoxelShape SHAPE_COMBINED = Shapes.or(
        Block.box(7, 0.575, 7, 9, 13, 9),
        Block.box(6.75, 5.725, 6.75, 9.25, 9.15, 9.25),
        Block.box(6.75, 9.875, 6.75, 9.25, 13, 9.25),
        Block.box(5.25, 0, 5.25, 10.75, 2, 10.75),
        Block.box(6, 11.25, 7, 7, 12.75, 9),
        Block.box(9, 11.25, 7, 10, 12.75, 9)
    );

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public AugmentModificationBlock() {
        super(sharedBehaviour);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((t) -> new AugmentModificationBlock());
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COMBINED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AugmentModificationEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType,
            BlockEntityReg.AUGMENT_MODIFICATION_STATION_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AugmentModificationEntity augmentStation) {
                augmentStation.dropsAllInventory(level);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack itemStack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        var fail = ItemInteractionResult.FAIL;
        var success = ItemInteractionResult.SUCCESS;

        if(!(level.getBlockEntity(pos) instanceof AugmentModificationEntity augmentStation)) return fail;
        var hands = player.getItemInHand(hand);
        var result = augmentBlockInteraction(level, pos, player, hand, augmentStation, hands, VAULT_ACTIVATE, 0.05, 10, 0.9, 0.15);
        if(result == fail && !augmentStation.getInteractionSlot().isEmpty() && augmentStation.getInteractionSlot().has(CUSTOM_MODEL_DATA)){
            if(!(player instanceof ServerPlayer serverPlayer)) return fail;
            serverPlayer.openMenu(augmentStation, pos);
            return success;
        }
        return success;
    }

    public static void setOuterRingPulse(
        Level level,
        int getType,
        BlockPos blockPos,
        double yOffset,
        int lifetime,
        double speed,
        double radius
    ){
        ElementReg.fromId(getType).ifPresent(
            element -> {
                var particle = ParticleHandlers.genericParticle(element, lifetime, 0.8f);
                PositionFinders.getOuterRingOfRadiusRandom(blockPos.getBottomCenter().add(0,yOffset,0), radius, 40,
                    positions -> {
                        ParticleHandlers.sendParticles(
                            level, particle, positions.offsetRandom(RandomSource.create(), 0.2f),
                            0, 0, Helpers.Random.nextDouble(0.02,0.2),0,speed
                        );
                    }
                );
            }
        );
    }

    public static ItemInteractionResult infuserBlockInteraction(
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        AbstractBEInventory augmentStation,
        ItemStack stack,
        SoundEvent soundEvent,
        double yOffset,
        int lifetime,
        double speed,
        double radius
    ) {
        if (stack.is(ModTags.Items.ESSENCE_FRAGMENT) || stack.isEmpty() && player.isShiftKeyDown()) {
            if(!stack.isEmpty()) Helpers.getSoundWithPosition(level, pos, soundEvent, 1, 1.2f);
            BlockInteractionHandler.swapItemsWithHand(augmentStation.inputItemHandler, 0, player, hand);
            var stackInSlot = augmentStation.inputItemHandler.getStackInSlot(0);
            var type = stackInSlot.get(CUSTOM_MODEL_DATA);
            if (type != null) {
                setOuterRingPulse(level, type.value(), pos, yOffset, lifetime, speed, radius);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

    public static ItemInteractionResult augmentBlockInteraction(
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        AbstractBEInventory augmentStation,
        ItemStack stack,
        SoundEvent soundEvent,
        double yOffset,
        int lifetime,
        double speed,
        double radius
    ) {
        if (stack.getItem() instanceof Augment || stack.isEmpty() && player.isShiftKeyDown()) {
            if(!stack.isEmpty()) Helpers.getSoundWithPosition(level, pos, soundEvent, 1, 1.2f);
            BlockInteractionHandler.swapItemsWithHand(augmentStation.inputItemHandler, 0, player, hand);
            var stackInSlot = augmentStation.inputItemHandler.getStackInSlot(0);
            var type = stackInSlot.get(CUSTOM_MODEL_DATA);
            if (type != null) {
                setOuterRingPulse(level, type.value(), pos, yOffset, lifetime, speed, radius);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

}
