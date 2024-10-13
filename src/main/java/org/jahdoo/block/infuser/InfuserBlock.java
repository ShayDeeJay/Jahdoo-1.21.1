package org.jahdoo.block.infuser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;

public class InfuserBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE_BASE = Block.box(0, 8, 0, 16, 11, 16);
    public static final VoxelShape SHAPE_BASE_SECOND = Block.box(3, 2, 3, 13, 8, 13);
    public static final VoxelShape SHAPE_BASE_THIRD = Block.box(2, 0, 2, 14, 2, 14);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE_THIRD,SHAPE_BASE_SECOND, SHAPE_BASE);

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty IS_INFUSING = BooleanProperty.create("is_infusing");

    public InfuserBlock() {
        super(sharedBlockBehaviour());
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(IS_INFUSING, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new InfuserBlock());
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if(blockState.getValue(IS_INFUSING)){
            level.playLocalSound(blockPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.03F, 2.0f, false);
            for(int l3 = 0; l3 < 1; ++l3) {
                float f12 = 2.2F * randomSource.nextFloat() - 1.0F;
                float f14 = 2.2F * randomSource.nextFloat() - 1.0F;
                float f15 = 2.2F * randomSource.nextFloat() - 1.0F;
                level.addParticle(ParticleTypes.SCULK_CHARGE_POP,
                    (double)blockPos.getX() + 0.5D,
                    (double)blockPos.getY() + 0.40D,
                    (double)blockPos.getZ() + 0.5D,
                    f12 * 0.02F,
                    f14 * 0.02F,
                    f15 * 0.02F);
            }
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        pBuilder.add(IS_INFUSING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMMON;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof InfuserBlockEntity) {
                ((InfuserBlockEntity) blockEntity).dropsAllInventory(pLevel);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!(pLevel.getBlockEntity(pPos) instanceof InfuserBlockEntity tableEntity)) return InteractionResult.FAIL;


        ItemStack itemInPlayerHand = pPlayer.getItemInHand(pPlayer.getUsedItemHand());
        ItemStack getInputSlot = tableEntity.inputItemHandler.getStackInSlot(0);
        ItemStack getOutputSlot = tableEntity.outputItemHandler.getStackInSlot(0);

        if(getInputSlot.isEmpty() && getOutputSlot.isEmpty()){
            if (itemInPlayerHand.getItem() == ItemsRegister.AUGMENT_ITEM.get()) {
                tableEntity.inputItemHandler.setStackInSlot(0, itemInPlayerHand.copyWithCount(1));
                itemInPlayerHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }

        if(!getOutputSlot.isEmpty()){
            this.addItemToHand(pPlayer.getMainHandItem(), pPlayer.getUsedItemHand(), pPlayer, getOutputSlot);
            return InteractionResult.SUCCESS;
        }

        if(getInputSlot.is(ItemsRegister.AUGMENT_ITEM.get())){
            this.addItemToHand(pPlayer.getMainHandItem(), pPlayer.getUsedItemHand(), pPlayer, getInputSlot);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private void addItemToHand(
        ItemStack itemInPlayerHand,
        InteractionHand interactionHand,
        Player player,
        ItemStack slotItem
    ){
        if (itemInPlayerHand.is(slotItem.getItem())) {
            itemInPlayerHand.grow(1);
            player.setItemInHand(interactionHand, itemInPlayerHand);
            slotItem.shrink(1);
        } else {
            if(itemInPlayerHand.isEmpty()){
                player.setItemInHand(interactionHand,slotItem.copy());
                slotItem.shrink(1);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InfuserBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.INFUSER_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }
}

