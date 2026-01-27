package org.jahdoo.common.block.power_up_station;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.items.CoreItem;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.common.block.BlockInteractionHandler.swapItemsWithHand;
import static org.jahdoo.common.registers.AttachmentReg.BOOL;
import static org.jahdoo.common.registers.BlockEntityReg.POWER_UP_BE;
import static org.jahdoo.common.registers.ComponentReg.CORE_DATA;
import static org.jahdoo.common.registers.ItemReg.AUGMENT_CORE;

public class PowerUpStation extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static final VoxelShape SHAPE_BASE = Shapes.or(
        Block.box(5.25, 0, 5.25, 10.75, 2, 10.75),
        Block.box(6.75, 1.5, 6.75, 9.25, 5, 9.25),
        Block.box(4.5, 3.7674599999999985, 4.5, 11.5, 13.642460000000003, 11.5)
    );
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty TYPE = BlockStateProperties.LEVEL;

    public PowerUpStation() {
        super(
            Properties.of()
                .strength(1f)
                .sound(SoundType.COPPER_BULB)
                .lightLevel((state) -> state.getValue(LIT) ? 4 : 0)
                .noOcclusion()
        );
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false)
                .setValue(TYPE, 1)
        );
    }

    public static int colourByState(int state){
        return state == 0 ? ElementReg.utility().partColourB() : ColourStore.RATING_4_YELLOW;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec((x) -> new PowerUpStation());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BASE;
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
        return new PowerUpStationEntity(pos,state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
        builder.add(WATERLOGGED);
        builder.add(TYPE);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, POWER_UP_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    private static void getItemInteractionResult(ItemStack heldItem, PowerUpStationEntity powerUpStationEntity, Player player, Level level) {
        if (heldItem.getItem() == AUGMENT_CORE.get()) {
            if(player.isCreative()) {
                Helpers.getSoundWithPosition(level, powerUpStationEntity.getBlockPos(), NOTE_BLOCK_BELL.value());
                powerUpStationEntity.setData(BOOL, !powerUpStationEntity.getData(BOOL));
            };
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PowerUpStationEntity PowerUpStationEntity)
                PowerUpStationEntity.dropsAllInventory(level);
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
        var entity = level.getBlockEntity(pos);

        if (entity instanceof PowerUpStationEntity powerUpStation) {
            var handler = powerUpStation.inputItemHandler;

            if(stack.has(CORE_DATA) && !CoreData.isFull(stack)){
                swapItemsWithHand(handler, 0, player, hand);
                if(!stack.isEmpty()){
                    Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundReg.LEVEL_UP.get(), 1, 0.5F);
                } else {
                    Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundReg.IMPACT.get(), 1, 0.85F);
                }
                return SUCCESS;
            }


            if(handler.getStackInSlot(0).isEmpty()){
                for (var item : player.getInventory().items) {
                    if (!CoreData.isFull(item) && CoreData.getFilled(item) > 0) {
                        var success = sharedPlace(level, pos, powerUpStation, item);
                        if (success != null) return success;
                    }
                }

                for (var item : player.getInventory().items) {
                    if (!CoreData.isFull(item)) {
                        var success = sharedPlace(level, pos, powerUpStation, item);
                        if (success != null) return success;
                    }
                }
            } else {
                Helpers.throwOrAddItem(player, handler.getStackInSlot(0));
                handler.setStackInSlot(0, ItemStack.EMPTY);
            }

            Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundReg.REJECT.get(), 1, 1);
        }

        return SUCCESS;
    }

    private static @Nullable ItemInteractionResult sharedPlace(Level level, BlockPos pos, PowerUpStationEntity powerUpStation, ItemStack item) {
        if (item.getItem() instanceof CoreItem && item.has(CORE_DATA)) {
            powerUpStation.inputItemHandler.insertItem(0, item.copyWithCount(1), false);
            Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundReg.LEVEL_UP.get(), 1, 0.5F);
            item.shrink(1);
            return SUCCESS;
        }
        return null;
    }
}

