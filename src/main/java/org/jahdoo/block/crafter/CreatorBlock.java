package org.jahdoo.block.crafter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.block.BlockInteractionHandler;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;

public class CreatorBlock extends BaseEntityBlock{

    public static final VoxelShape SHAPE_BASE = Block.box(3, 3, 3, 13, 13, 13);
    public static final VoxelShape SHAPE_BASE_SECOND = Block.box(2, 0, 2, 14, 3, 14);
    public static final VoxelShape SHAPE_BASE_THIRD = Block.box(0, 13, 0, 16, 16, 16);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE_THIRD, SHAPE_BASE_SECOND, SHAPE_BASE);

    public CreatorBlock() {
        super(sharedBlockBehaviour());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((t) -> new CreatorBlock());
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMMON;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CreatorEntity creatorEntity) {
                creatorEntity.dropsAllInventory(pLevel);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if(!(pLevel.getBlockEntity(pPos) instanceof CreatorEntity wandManager)) return ItemInteractionResult.FAIL;
        ItemStack stack = pPlayer.getItemInHand(pHand);
        ItemStackHandler inputItemHandler = wandManager.inputItemHandler;
        ItemStackHandler outputItemHandler = wandManager.outputItemHandler;

        if(!stack.isEmpty()){
            for (int i = 0; i < inputItemHandler.getSlots(); i++) {
                if(inputItemHandler.getStackInSlot(i).isEmpty()){
                    if (BlockInteractionHandler.removeItemsFromHandToSlot(inputItemHandler, i, pPlayer, 1)) {
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        } else {
            if(!outputItemHandler.getStackInSlot(0).isEmpty()){
                for (int i = 0; i < outputItemHandler.getSlots(); i++) {
                    if(BlockInteractionHandler.removeItemsFromSlotToHand(outputItemHandler, i, pPlayer, pHand)){
                        return ItemInteractionResult.CONSUME;
                    }
                }
            } else {
                for (int i = 0; i < inputItemHandler.getSlots(); i++) {
                    int entry = inputItemHandler.getSlots() - (i+1);
                    if(!inputItemHandler.getStackInSlot(entry).isEmpty()){
                        if (BlockInteractionHandler.removeItemsFromSlotToHand(inputItemHandler, entry, pPlayer, pHand)) {
                            return ItemInteractionResult.CONSUME;
                        }
                    }
                }
            }
        }

        return ItemInteractionResult.FAIL;
    }

    private Optional<CreatorEntity> getEntity(Level level, BlockPos blockPos) {
        return Optional.ofNullable(level.getBlockEntity(blockPos))
            .filter(blockEntity -> blockEntity instanceof CreatorEntity)
            .map(blockEntity -> (CreatorEntity) blockEntity);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CreatorEntity(pPos,pState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.CREATOR_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}
