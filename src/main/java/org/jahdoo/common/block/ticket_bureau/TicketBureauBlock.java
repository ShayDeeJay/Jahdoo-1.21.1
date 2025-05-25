package org.jahdoo.common.block.ticket_bureau;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import org.jahdoo.common.block.BlockInteractionHandler;
import org.jahdoo.common.components.TicketData;
import org.jahdoo.common.registers.ItemReg;

import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.world.ItemInteractionResult.*;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static org.jahdoo.common.registers.BlockEntityReg.TICKET_BUREAU_BE;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class TicketBureauBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static final VoxelShape SHAPE_COMMON = Shapes.or(
        Block.box(0, 6, 0, 16, 9, 16),
        Block.box(1, 0, 1, 4, 6, 4),
        Block.box(12, 0, 1, 15, 6, 4),
        Block.box(12, 0, 12, 15, 6, 15),
        Block.box(1, 0, 12, 4, 6, 15)
    );

    public TicketBureauBlock() {
        super(
            Properties.of()
                .strength(1f)
                .sound(SoundType.CHERRY_WOOD)
                .noOcclusion()
        );
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, SOUTH));
    }
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec((x) -> new TicketBureauBlock());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COMMON;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TicketBureauBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, TICKET_BUREAU_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TicketBureauBlockEntity tankBlockEntity)
                tankBlockEntity.dropsAllInventory(level);
        }
        super.onRemove(state, level, pos, newState, moveByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var handItem = player.getItemInHand(hand);
        var entity = level.getBlockEntity(pos);
        if(!(level instanceof ServerLevel)) return FAIL;

        if(entity instanceof TicketBureauBlockEntity entity1){
            if ((handItem.is(ItemReg.TRIAL_TICKET) || handItem.isEmpty())) {
                BlockInteractionHandler.swapItemsWithHand(entity1.inputItemHandler, 0, player, hand);
            }

            if (handItem.is(Items.REDSTONE)) {
                var copy = entity1.getTicketItem().copy();
                var setType = Random.nextInt(0, 5);
                TicketData.initTicket(copy, setType);
                entity1.inputItemHandler.setStackInSlot(0, copy);
            }
        }

        return SUCCESS;
    }
}

