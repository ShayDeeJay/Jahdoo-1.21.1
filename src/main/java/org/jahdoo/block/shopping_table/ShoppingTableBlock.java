package org.jahdoo.block.shopping_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.block.BlockInteractionHandler;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.Nullable;

public class ShoppingTableBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static VoxelShape SHAPE_COMBINED = Shapes.or(
        Block.box(2, 0, 2, 14, 10, 14),
        Block.box(0, 10, 0, 16, 14, 16),
        Block.box(12, 0, 12, 16, 10, 16),
        Block.box(12, 0, 0, 16, 10, 4),
        Block.box(0, 0, 12, 4, 10, 16),
        Block.box(0, 0, 0, 4, 10, 4)
    );

    public ShoppingTableBlock() {
        super(Properties.of().strength(1f).noOcclusion());
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec((x) -> new ShoppingTableBlock());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMBINED;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            var blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ShoppingTableEntity shoppingTable) shoppingTable.dropsAllInventory(pLevel);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        var stack = pPlayer.getItemInHand(pHand);
        var entity = pLevel.getBlockEntity(pPos);
        if(!(entity instanceof ShoppingTableEntity shoppingTable)) return ItemInteractionResult.FAIL;

        BlockInteractionHandler.removeItemsFromHandToSlot(shoppingTable.inputItemHandler, 0, pPlayer, 1);

        return ItemInteractionResult.SUCCESS;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ShoppingTableEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(
            pBlockEntityType, BlockEntitiesRegister.SHOPPING_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}

