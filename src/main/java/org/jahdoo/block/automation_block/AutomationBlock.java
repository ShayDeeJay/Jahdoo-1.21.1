package org.jahdoo.block.automation_block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.block.crafter.CreatorEntity;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;

public class AutomationBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public AutomationBlock() {
        super(sharedBlockBehaviour());
        this.registerDefaultState(
            this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new AutomationBlock());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof AutomationBlockEntity automationBlock) {
                automationBlock.dropsAllInventory(pLevel);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.PUSH_ONLY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AutomationBlockEntity(pPos,pState);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return openWandGUI(player, pos, level);
    }

    private ItemInteractionResult openWandGUI(Player player, BlockPos blockPos, Level level){
        var fail = ItemInteractionResult.FAIL;
        if (!(level.getBlockEntity(blockPos) instanceof AutomationBlockEntity automationBlockEntity)) return fail;
        if(!(player instanceof ServerPlayer serverPlayer)) return fail;
        serverPlayer.openMenu(automationBlockEntity, blockPos);
        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
//        if(pLevel.isClientSide()) return null;
        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.AUTOMATION_BLOCK.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }
}

