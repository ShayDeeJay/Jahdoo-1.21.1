package org.jahdoo.block.wand_block_manager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.BlockInteractionHandler;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.block.BlockInteractionHandler.swapItemsWithHand;
import static org.jahdoo.block.augment_modification_station.AugmentModificationBlock.*;
import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;
import static org.jahdoo.utils.ModHelpers.getSoundWithPosition;

public class WandManagerTableBlock extends BaseEntityBlock {
    public static VoxelShape SHAPE_COMBINED = Shapes.or(
        Block.box(5, 0, 11.125, 11, 2, 13.125),
        Block.box(5, 0, 2.875, 11, 2, 4.875),
        Block.box(2.875, 0, 5, 4.875, 2, 11),
        Block.box(11.125, 0, 5, 13.125, 2, 11),
        Block.box(4.75, 0, 4.75, 5.75, 3.0124999999999993, 11.25),
        Block.box(4.75, 3.0124999999999993, 4.75, 5.75, 11.4875, 5.75),
        Block.box(4.75, 3.0124999999999993, 10.25, 5.75, 11.4875, 11.25),
        Block.box(4.75, 11.4875, 4.75, 5.75, 15, 11.25),
        Block.box(6.2414200000000015, 14.125, 7.25, 7.2414200000000015, 15.875, 8.75),
        Block.box(8.758579999999998, 14.125, 7.25, 9.758579999999998, 15.875, 8.75),
        Block.box(7.25, 14.125, 8.75858, 8.75, 15.875, 9.75858),
        Block.box(7.25, 14.125, 6.2414200000000015, 8.75, 15.875, 7.2414200000000015),
        Block.box(7, 0, 11, 9, 1.75, 13),
        Block.box(3, 0, 7, 5, 1.75, 9),
        Block.box(7, 0, 3, 9, 1.75, 5),
        Block.box(11, 0, 7, 13, 1.75, 9),
        Block.box(7.05905, 14.987450000000003, 8.31365, 8.94095, 16.24205, 8.94095),
        Block.box(8.31365, 14.987450000000003, 7.05905, 8.94095, 16.24205, 8.94095),
        Block.box(7.05905, 14.987450000000003, 7.05905, 8.94095, 16.24205, 7.68635),
        Block.box(7.05905, 14.987450000000003, 7.05905, 7.68635, 16.24205, 8.94095),
        Block.box(5.75, 3, 9.25, 10.25, 11.4875, 10.75),
        Block.box(5.75, 3, 5.25, 10.25, 11.4875, 6.75),
        Block.box(9.25, 3, 5.75, 10.75, 11.4875, 10.25),
        Block.box(5.25, 3, 5.75, 6.75, 11.4875, 10.25),
        Block.box(5.75, 0, 4.75, 10.25, 3.0124999999999993, 11.25),
        Block.box(5.75, 11.4875, 4.75, 10.25, 15, 11.25),
        Block.box(10.25, 0, 4.75, 11.25, 3.0124999999999993, 11.25),
        Block.box(10.25, 3.0124999999999993, 4.75, 11.25, 11.4875, 5.75),
        Block.box(10.25, 3.0124999999999993, 10.25, 11.25, 11.4875, 11.25),
        Block.box(10.25, 11.4875, 4.75, 11.25, 15, 11.25)
    );

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public WandManagerTableBlock() {
        super(sharedBlockBehaviour());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new WandManagerTableBlock());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getClockWise());
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        return SHAPE_COMBINED;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof WandManagerTableEntity) {
                ((WandManagerTableEntity) blockEntity).dropsAllInventory(pLevel);
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemInteractionResult fail = ItemInteractionResult.FAIL;
        ItemInteractionResult success = ItemInteractionResult.SUCCESS;

        if(!(level.getBlockEntity(pos) instanceof WandManagerTableEntity wandManager)) return fail;
        var hands = player.getItemInHand(interactionHand);
        var result = augmentBlockInteraction(level, pos, player, interactionHand, wandManager, hands, SoundEvents.VAULT_ACTIVATE, 1, 10, 0.6, 0.12);

        if(result == fail && !wandManager.getWandSlot().isEmpty()){
            if(!(player instanceof ServerPlayer serverPlayer)) return fail;
            serverPlayer.openMenu(wandManager, pos);
            return success;
        }

        return success;
    }

    public static ItemInteractionResult augmentBlockInteraction(
        Level pLevel,
        BlockPos pPos,
        Player pPlayer,
        InteractionHand pHand,
        WandManagerTableEntity wandManagerTable,
        ItemStack hand,
        SoundEvent soundEvent,
        double yOffset,
        int lifetime,
        double speed,
        double radius
    ) {
        if (hand.getItem() instanceof WandItem || hand.isEmpty() && pPlayer.isShiftKeyDown()) {
            getSoundWithPosition(pLevel, pPos, soundEvent, 1, 1.2f);
            swapItemsWithHand(wandManagerTable.inputItemHandler, 0, pPlayer, pHand);
            var stackInSlot = wandManagerTable.inputItemHandler.getStackInSlot(0);
            var type = getElementByWandType(stackInSlot.getItem());
            wandManagerTable.privateTicks = 0;
            if (!type.isEmpty()) setOuterRingPulse(pLevel, type.getFirst().getTypeId(), pPos, yOffset, lifetime, speed, radius);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WandManagerTableEntity(pPos,pState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.WAND_MANAGER_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}

