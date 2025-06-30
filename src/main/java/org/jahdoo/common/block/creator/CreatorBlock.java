package org.jahdoo.common.block.creator;

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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.common.block.BlockInteractionHandler.removeItemsFromHandToSlot;
import static org.jahdoo.common.event.event_helpers.EventHelpers.setChaosCubeAbility;


public class CreatorBlock extends BaseEntityBlock{

    public static VoxelShape SHAPE_COMMON = Shapes.or(
        Block.box(6, 4, 6, 10, 7, 10),
        Block.box(5.25, 0, 5.25, 10.75, 2, 10.75),
        Block.box(6.25, 2, 6.25, 9.75, 5.5, 9.75),
        Block.box(0, 13, 0, 16, 16, 16)
    );

    public CreatorBlock() {
        super(Properties.of().strength(1f).sound(SoundType.DEEPSLATE_BRICKS).noOcclusion());
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
    protected @NotNull ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if(!(level.getBlockEntity(pos) instanceof CreatorEntity wandManager)) return FAIL;
        var stack = player.getMainHandItem();
        var inputItemHandler = wandManager.inputItemHandler;
        var outputItemHandler = wandManager.outputItemHandler;

        if(setChaosCubeAbility(player, level, pos, stack)) return SUCCESS;

        if(!stack.isEmpty()){
            for (int i = 0; i < inputItemHandler.getSlots(); i++) {
                if(inputItemHandler.getStackInSlot(i).isEmpty()){
                    if (removeItemsFromHandToSlot(inputItemHandler, i, player, 1, hand)) return SUCCESS;
                }
            }
            return FAIL;
        } else {
            if(!outputItemHandler.getStackInSlot(0).isEmpty()){
                Helpers.throwOrAddItem(player, outputItemHandler.getStackInSlot(0));
                return SUCCESS;
            } else {
                for (int i = 0; i < inputItemHandler.getSlots(); i++) {
                    int entry = inputItemHandler.getSlots() - (i+1);
                    if(!inputItemHandler.getStackInSlot(entry).isEmpty()){
                        Helpers.throwOrAddItem(player, inputItemHandler.getStackInSlot(entry));
                        return SUCCESS;
                    }
                }
                return FAIL;
            }
        }
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
            BlockEntityReg.CREATOR_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}
