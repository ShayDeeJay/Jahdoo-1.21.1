package org.jahdoo.block.rune_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleContainer;
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
import org.jahdoo.block.BlockInteractionHandler;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.block.BlockInteractionHandler.*;
import static org.jahdoo.block.BlockInteractionHandler.swapItemsWithHand;

public class RuneTable extends BaseEntityBlock {

    public static final VoxelShape SHAPE_COMBINED = Shapes.or(
            Block.box(0, 9, 0, 16, 12, 16),
            Block.box(10, 0, 1, 15, 2, 6),
            Block.box(11, 2, 2, 14, 9, 5),
            Block.box(1, 0, 1, 6, 2, 6),
            Block.box(2, 2, 2, 5, 9, 5),
            Block.box(1, 0, 10, 6, 2, 15),
            Block.box(2, 2, 11, 5, 9, 14),
            Block.box(10, 0, 10, 15, 2, 15),
            Block.box(11, 2, 11, 14, 9, 14),
            Block.box(4.5, 11, 4.5, 11.5, 13, 11.5),
            Block.box(11.25, 4, 5, 13.75, 6.5, 11),
            Block.box(5, 4, 11.25, 11, 6.5, 13.75),
            Block.box(5, 4, 2.25, 11, 6.5, 4.75),
            Block.box(2.25, 4, 5, 4.75, 6.5, 11),
            Block.box(5.5, 7, 0, 10.5, 9, 0),
            Block.box(5.5, 7, 0, 10.5, 9, 0),
            Block.box(5.5, 7, 16, 10.5, 9, 16),
            Block.box(0, 7, 5.5, 0, 9, 10.5),
            Block.box(16, 7, 5.5, 16, 9, 10.5)
    );

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public RuneTable() {
        super(Properties.of().strength(1f).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec((x) -> new RuneTable());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMBINED;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof RuneTableEntity runeTable) {
                SimpleContainer inputInventory = new SimpleContainer(1);

                var stackInSlot = runeTable.inputItemHandler.getStackInSlot(0);
                inputInventory.setItem(0, stackInSlot);

                Containers.dropContents(pLevel, pPos, inputInventory);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }


    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        var fail = ItemInteractionResult.FAIL;
        var entity = level.getBlockEntity(pos);
        if(!(entity instanceof RuneTableEntity runeTable)) return fail;

        var hasItem = runeTable.getItem().getStackInSlot(0).isEmpty();
        if(!hasItem && player.isShiftKeyDown()){
            removeItemsFromSlotToHand(runeTable.inputItemHandler, 0, player, hand);
            return ItemInteractionResult.SUCCESS;
        } else if (stack.has(DataComponentRegistry.RUNE_HOLDER) && hasItem) {
            swapItemsWithHand(runeTable.inputItemHandler, 0, player, hand);
            return ItemInteractionResult.SUCCESS;
        } else {
            if(!(player instanceof ServerPlayer serverPlayer)) return ItemInteractionResult.SUCCESS;
            serverPlayer.openMenu(runeTable, pos);
            return ItemInteractionResult.SUCCESS;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RuneTableEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(
            pBlockEntityType, BlockEntitiesRegister.RUNE_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}

