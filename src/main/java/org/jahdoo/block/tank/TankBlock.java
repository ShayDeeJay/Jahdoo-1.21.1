package org.jahdoo.block.tank;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import org.jahdoo.block.BlockInteractionHandler;
import org.jahdoo.block.crafter.CreatorBlock;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jetbrains.annotations.Nullable;

public class TankBlock extends BaseEntityBlock {

    public static final VoxelShape SHAPE_BASE = Block.box(1.95, 0, 1.95, 14.05, 2.75, 14.05);
    public static final VoxelShape JAR = Block.box(3, 2.75, 3, 13, 12.75, 13);
    public static final VoxelShape TOP = Block.box(3, 13.25, 3, 13, 16, 13);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, JAR, TOP);

    public TankBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any() );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(TankBlock::new);
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
            if (blockEntity instanceof TankBlockEntity tankBlockEntity) tankBlockEntity.dropsAllInventory(pLevel);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }



    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
//        if(pLevel.isClientSide) return ItemInteractionResult.FAIL;
        ItemStack stack = pPlayer.getItemInHand(pHand);
        BlockEntity entity = pLevel.getBlockEntity(pPos);

        if (entity instanceof TankBlockEntity tankBlockEntity) {
            if (BlockInteractionHandler.stackHandlerWithFeedBack(tankBlockEntity.inputItemHandler, stack, ItemsRegister.JIDE_POWDER.get(), 0, tankBlockEntity.getMaxSlotSize(), pPlayer)) {
                pPlayer.level().playSound(pPlayer, pPlayer.blockPosition(), SoundEvents.SAND_PLACE, SoundSource.BLOCKS);
                return ItemInteractionResult.SUCCESS;
            } else {
                BlockInteractionHandler.RemoveItemsFromSlotToHand(tankBlockEntity.inputItemHandler,0,pPlayer,pHand,pLevel, pPos, SoundEvents.SAND_PLACE,1,1);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TankBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) return null;

        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.TANK_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}

