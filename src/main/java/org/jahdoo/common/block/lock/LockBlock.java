package org.jahdoo.common.block.lock;

import com.mojang.serialization.MapCodec;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.attachments.player_abilities.InstanceData;
import org.jahdoo.ascension.boon.LevelBoon;
import org.jahdoo.ascension.boon.LevelBoonSelection;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.tank.TankBlockEntity;
import org.jahdoo.common.registers.AttachmentReg;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL;
import static net.minecraft.sounds.SoundEvents.SAND_PLACE;
import static net.minecraft.sounds.SoundSource.BLOCKS;
import static net.minecraft.world.ItemInteractionResult.*;
import static net.minecraft.world.level.block.Blocks.*;
import static org.jahdoo.ascension.StructureManager.placeNewSide;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.block.BlockInteractionHandler.RemoveItemsFromSlotToHand;
import static org.jahdoo.common.block.BlockInteractionHandler.stackHandlerWithFeedBack;
import static org.jahdoo.common.registers.AttachmentReg.BOOL;
import static org.jahdoo.common.registers.BlockEntityReg.LOCK_BE;
import static org.jahdoo.common.registers.BlockEntityReg.TANK_BE;
import static org.jahdoo.common.registers.ItemReg.AUGMENT_CORE;
import static org.jahdoo.common.registers.ItemReg.NEXITE_POWDER;

public class LockBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static final VoxelShape SHAPE_BASE = Shapes.or(
        Block.box(0, 0, 1, 16, 16, 16),
        Block.box(4, 3, 0, 12, 9, 1),
        Block.box(15, 0, 0, 16, 16, 1),
        Block.box(0, 0, 0, 1, 16, 1),
        Block.box(1, 0, 0, 15, 1, 1),
        Block.box(1, 15, 0, 15, 16, 1)
    );

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public LockBlock() {
        super(
            Properties.of()
                .strength(1f)
                .noOcclusion()
                .lightLevel((blockState) -> 4)
        );
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new LockBlock());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BASE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LockBlockEntity(pos,state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, LOCK_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if(!(level.getBlockEntity(pos) instanceof LockBlockEntity entity)) return FAIL;
        if(entity.isInitialized()){
            if (!entity.canPlace()) {
                player.displayClientMessage(Component.literal("Invalid Location"), true);
                return FAIL;
            }

            if (level instanceof ServerLevel serverLevel) {
                var getState = state.getValue(FACING);
                getSoundWithPosition(serverLevel, pos, SoundEvents.LODESTONE_COMPASS_LOCK, 1, 1.4F);
                getSoundWithPosition(serverLevel, pos, SoundEvents.VAULT_ACTIVATE, 1, 0.6F);
                placeNewSide(serverLevel, getState, pos.relative(getState, 0), Helpers.nameToStringId(entity.roomId.getString()));

                var range = betweenClosed(
                    pos.getX() - 10, pos.getY() - 10, pos.getZ() - 10,
                    pos.getX() + 10, pos.getY() + 10, pos.getZ() + 10
                );

                for (var blockPos : range) {
                    var bedrock = serverLevel.getBlockState(blockPos).is(BEDROCK);
                    var netherite = serverLevel.getBlockState(blockPos).is(NETHERITE_BLOCK);

                    if (bedrock || netherite) serverLevel.destroyBlock(blockPos, false);
                }

                serverLevel.destroyBlock(pos, false);

                LevelBoonSelection.getRun(level, entity.value, entity.getBoon.executeIndex());
                var data = level.getData(AttachmentReg.INSTANCE_DATA);
                player.sendSystemMessage(Component.literal(data.toString()));
                return SUCCESS;
            }
        } else {
            entity.setRoomData();
            return SUCCESS;
        }

        return FAIL;
    }
}

