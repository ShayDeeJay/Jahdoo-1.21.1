package org.jahdoo.common.block.lock;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.trial_nexus.level_manager.StructureManager;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.LevelBoonReg;

import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.sounds.SoundEvents.*;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static net.minecraft.world.level.block.Blocks.NETHERITE_BLOCK;
import static net.minecraft.world.level.block.Blocks.OBSERVER;
import static org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon.SyncableData.EMPTY;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeNewSide;
import static org.jahdoo.trial_nexus.utils.Helpers.*;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.BlockEntityReg.LOCK_BE;

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

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, LOCK_BE.get(), (levelA, pos, bState, bEntity) -> bEntity.tick(levelA, pos, bState)
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
        if(!(level instanceof ServerLevel serverLevel)) return FAIL;
        if(!entity.isInitialized()) return FAIL;


        if(stack.is(ItemReg.EXIT_KEY)) {
            if(KeyItem.isValidKey(stack, level)){
                entity.setRoomData(StructureManager.EXIT_ROOM_COMPONENT);
                stack.shrink(1);
            } else {
                return FAIL;
            }
        }

        var getState = state.getValue(FACING);
        var noDifficultySelected = serverLevel.getData(INSTANCE_DATA).getDifficulty().isEmpty();
        if(noDifficultySelected) entity.setDifficulty();

        if ((!entity.getDifficulty.isEmpty() && !noDifficultySelected)) {
            var message = "Difficulty already selected";
            player.displayClientMessage(withStyleComponent(message, ColourStore.NEGATIVE_RED), true);
            getSoundWithPosition(level, pos, VAULT_REJECT_REWARDED_PLAYER, 0.3F, 2F);
            return FAIL;
        }

        if (!entity.isStartingRoom() && !entity.canPlace()) {
//            var message = "Can't place, room already generated";
//            player.displayClientMessage(withStyleComponent(message, ColourStore.NEGATIVE_RED), true);
            getSoundWithPosition(level, pos, VAULT_REJECT_REWARDED_PLAYER, 0.3F, 2F);
            return FAIL;
        }

        getSoundWithPosition(serverLevel, pos, LODESTONE_COMPASS_LOCK, 1, 1.4F);
        getSoundWithPosition(serverLevel, pos, VAULT_ACTIVATE, 1, 0.6F);
        placeNewSide(serverLevel, getState, pos.relative(getState, entity.isStartingRoom() ? 12 : 1), nameToId(entity.roomId.getString()));
        onUnlock(entity, serverLevel);

        if(entity.isStartingRoom()) destroyDoors(serverLevel, pos.relative(getState, 12));
        destroyDoors(serverLevel, pos);
        entity.clicked = true;
        return SUCCESS;
    }

    private static void onUnlock(LockBlockEntity entity, ServerLevel level) {

        if(entity.negativeBoon != EMPTY){
            var getBoonNeg = LevelBoonReg.fromId(entity.negativeBoon.id());
            getBoonNeg.ifPresent(b -> b.execute(level, entity.negativeBoon.value()));
        }

        if(entity.positiveBoon != EMPTY){
            var getBoonPos = LevelBoonReg.fromId(entity.positiveBoon.id());
            getBoonPos.ifPresent(b -> b.execute(level, entity.positiveBoon.value()));
        }

    }

    private static void destroyDoors(ServerLevel serverLevel, BlockPos pos) {
        var range = betweenClosed(pos.getX() - 2, pos.getY() - 1, pos.getZ() - 2, pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);

        for (var blockPos : range) {
            var netherite = serverLevel.getBlockState(blockPos).is(NETHERITE_BLOCK);
            var observer = serverLevel.getBlockState(blockPos).is(OBSERVER);

            if (observer || netherite) serverLevel.destroyBlock(blockPos, false);
        }
    }
}

