package org.jahdoo.common.block.tank;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
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
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.sounds.SoundEvents.*;
import static net.minecraft.sounds.SoundSource.*;
import static org.jahdoo.common.block.BlockInteractionHandler.*;
import static org.jahdoo.common.registers.AttachmentReg.BOOL;
import static org.jahdoo.common.registers.BlockEntityReg.*;
import static org.jahdoo.common.registers.ItemReg.*;

public class TankBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static final VoxelShape SHAPE_BASE = Block.box(1.95, 0, 1.95, 14.05, 2.75, 14.05);
    public static final VoxelShape JAR = Block.box(3, 2.75, 3, 13, 12.75, 13);
    public static final VoxelShape TOP = Block.box(3, 13.25, 3, 13, 16, 13);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, JAR, TOP);
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public TankBlock() {
        super(
            BlockBehaviour.Properties.of()
                .strength(1f)
                .sound(SoundType.COPPER_BULB)
                .lightLevel((state) -> state.getValue(LIT) ? 4 : 0)
                .noOcclusion()
        );
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec((x) -> new TankBlock());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COMMON;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TankBlockEntity(pos,state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
        builder.add(WATERLOGGED);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, TANK_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    private static void getItemInteractionResult(ItemStack heldItem, TankBlockEntity tankBlock, Player player, Level level) {
        if (heldItem.getItem() == AUGMENT_CORE.get()) {
            if(player.isCreative()) {
                Helpers.getSoundWithPosition(level, tankBlock.getBlockPos(), NOTE_BLOCK_BELL.value());
                tankBlock.setData(BOOL, !tankBlock.getData(BOOL));
            };
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TankBlockEntity tankBlockEntity)
                tankBlockEntity.dropsAllInventory(level);
        }
        super.onRemove(state, level, pos, newState, moveByPiston);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var blockpos = context.getClickedPos();
        var blockstate = context.getLevel().getBlockState(blockpos);

        if (blockstate.is(this)) {
            return blockstate.setValue(WATERLOGGED, false);
        } else {
            var fluidstate = context.getLevel().getFluidState(blockpos);
            return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var handItem = player.getItemInHand(hand);
        var entity = level.getBlockEntity(pos);

        if (entity instanceof TankBlockEntity tank) {
            getItemInteractionResult(handItem, tank, player, level);

            if (stackHandlerWithFeedBack(tank.inputItemHandler, handItem, NEXITE_POWDER.get(), 0, tank.getMaxSlotSizeInput(), player)) {
                level.playSound(player, player.blockPosition(), SAND_PLACE, BLOCKS);
                return ItemInteractionResult.SUCCESS;
            } else {
                RemoveItemsFromSlotToHand(tank.inputItemHandler,0,player,hand,level, pos, SAND_PLACE,1,1);
            }
        }

        return ItemInteractionResult.SUCCESS;
    }
}

