package org.jahdoo.block.tank;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.block.BlockInteractionHandler;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.registers.AttachmentRegister.BOOL;

public class NexiteTankBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{
    public static final VoxelShape SHAPE_BASE = Block.box(1.95, 0, 1.95, 14.05, 2.75, 14.05);
    public static final VoxelShape JAR = Block.box(3, 2.75, 3, 13, 12.75, 13);
    public static final VoxelShape TOP = Block.box(3, 13.25, 3, 13, 16, 13);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, JAR, TOP);
    public static final BooleanProperty LIT;
    public static final BooleanProperty WATERLOGGED;

    public NexiteTankBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(NexiteTankBlock::new);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMMON;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
        builder.add(WATERLOGGED);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof NexiteTankBlockEntity nexiteTankBlockEntity)
                nexiteTankBlockEntity.dropsAllInventory(pLevel);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = context.getLevel().getBlockState(blockpos);
        if (blockstate.is(this)) {
            return blockstate.setValue(WATERLOGGED, false);
        } else {
            FluidState fluidstate = context.getLevel().getFluidState(blockpos);
            BlockState blockstate1 = this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
            return blockstate1;
        }
    }

    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        if (entity instanceof NexiteTankBlockEntity nexiteTankBlockEntity) {
            getItemInteractionResult(stack, nexiteTankBlockEntity, pPlayer, pLevel);
            if (BlockInteractionHandler.stackHandlerWithFeedBack(nexiteTankBlockEntity.inputItemHandler, stack, ItemsRegister.NEXITE_POWDER.get(), 0, nexiteTankBlockEntity.getMaxSlotSize(), pPlayer)) {
                pPlayer.level().playSound(pPlayer, pPlayer.blockPosition(), SoundEvents.SAND_PLACE, SoundSource.BLOCKS);
                return ItemInteractionResult.SUCCESS;
            } else {
                BlockInteractionHandler.RemoveItemsFromSlotToHand(nexiteTankBlockEntity.inputItemHandler,0,pPlayer,pHand,pLevel, pPos, SoundEvents.SAND_PLACE,1,1);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    private static void getItemInteractionResult(ItemStack heldItem, NexiteTankBlockEntity tankBlock, Player player, Level level) {
        if (heldItem.getItem() == ItemsRegister.AUGMENT_CORE.get()) {
            if(player.isCreative()) {
                ModHelpers.getSoundWithPosition(level, tankBlock.getBlockPos(), SoundEvents.NOTE_BLOCK_BELL.value());
                tankBlock.setData(BOOL, !tankBlock.getData(BOOL));
            };
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NexiteTankBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(
            pBlockEntityType, BlockEntitiesRegister.TANK_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    static {
        LIT = RedstoneTorchBlock.LIT;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}

