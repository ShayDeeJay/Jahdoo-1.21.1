package org.jahdoo.common.block.lock;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.LevelBoonReg;
import org.jahdoo.trial_nexus.level_manager.RoomData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.sounds.SoundEvents.*;
import static net.minecraft.world.ItemInteractionResult.*;
import static net.minecraft.world.level.block.Blocks.OBSERVER;
import static org.jahdoo.common.particle.ParticleHandlers.getNonBakedParticles;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.BlockEntityReg.LOCK_BE;
import static org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon.SyncableData.EMPTY;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeNewSide;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.PositionFinders.innerRadiusRandom;

public class LockBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static final VoxelShape SHAPE_BASE = Block.box(0, 0, 0, 16, 16, 16);

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
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        if(context instanceof EntityCollisionContext entityCollisionContext){
        }

        var noCol = Block.box(0, 0, 0, 0, 0, 0);
        return super.getCollisionShape(state, level, pos, context);
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        System.out.println("imim");
        return super.useWithoutItem(state, level, pos, player, hitResult);
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
        if(hand.equals(InteractionHand.OFF_HAND)) return FAIL;
        if(!(level.getBlockEntity(pos) instanceof LockBlockEntity entity)) return FAIL;
        if(!(level instanceof ServerLevel serverLevel)) return FAIL;
        if(!entity.isInitialized()) return FAIL;

        if(stack.is(ItemReg.DICE)) {
            entity.setDataByDifficulty();
            entity.updateBlock();
            SoundHelpers.getSoundWithPosition(level, pos, SoundReg.RE_ROLL.get());
            var getPositions = innerRadiusRandom(pos.getCenter().subtract(0, 1, 0), 2, 100);
            for (var vec3 : getPositions) {
                var colour = ColourHelpers.getUniqueB();
                var particle = getNonBakedParticles(colour, colour, Random.nextInt(6, 12), Random.nextInt(2, 4));
                sendParticles(level, particle, vec3, 0, 0, 0.5, 0, Random.nextDouble(0.6, 2.2));
            }
            stack.shrink(1);
            return SUCCESS;
        }

        var keyUsed = false;
        //Todo Check logic as seems non useful right now
        if(RoomData.isLockKey(stack)) {
            if(KeyItem.isValidKey(stack, level)){
                var component = RoomData.getByItem(stack).getComponent();
                entity.setRoomData(component);
                keyUsed = true;
                stack.shrink(1);
            } else {
                return FAIL;
            }
        }

        var getState = state.getValue(FACING);
        var noDifficultySelected = serverLevel.getData(INSTANCE_DATA).getDifficulty().isEmpty();

        if (noDifficultySelected) {
            entity.setDifficulty();
            if(player instanceof ServerPlayer serverPlayer){
                JahdooHelpers.sendClientSound(serverPlayer, SoundReg.LOOP.get(), 0.4F, 1, true);
            }
        }

        if ((!entity.getDifficulty.isEmpty() && !noDifficultySelected)) {
            var message = "Difficulty already selected";
            player.displayClientMessage(TextHelpers.withStyleComponent(message, ColourHelpers.getNegativeRed()), true);
            SoundHelpers.getSoundWithPosition(level, pos, VAULT_REJECT_REWARDED_PLAYER, SoundSource.BLOCKS, 0.3F, 2F);
            return FAIL;
        }

        if (!entity.isStartingRoom() && !entity.canPlace()) {
            var message = "Can't place, room already generated";
            player.displayClientMessage(TextHelpers.withStyleComponent(message, ColourHelpers.getNegativeRed()), true);
            SoundHelpers.getSoundWithPosition(level, pos, VAULT_REJECT_REWARDED_PLAYER, SoundSource.BLOCKS, 2F);
            return FAIL;
        }

        onUnlock(entity, serverLevel);

        entity.clicked = true;
        SoundHelpers.getSoundWithPosition(serverLevel, pos, LODESTONE_COMPASS_LOCK, SoundSource.BLOCKS, 1, 1.4F);
        SoundHelpers.getSoundWithPosition(serverLevel, pos, VAULT_ACTIVATE, SoundSource.BLOCKS, 0.6F);
        placeNewSide(serverLevel, getState, pos.relative(getState, entity.isStartingRoom() ? 12 : 1), TextHelpers.nameToId(entity.roomId.getString()), keyUsed);

        if (entity.isStartingRoom()) destroyDoors(serverLevel, pos.relative(getState, 12));
        destroyDoors(serverLevel, pos);
        return CONSUME;
    }

    public static void getItemInteractionResult(BlockState state, BlockPos pos, LockBlockEntity entity, ServerLevel serverLevel) {
        var getState = state.getValue(FACING);
        var noDifficultySelected = serverLevel.getData(INSTANCE_DATA).getDifficulty().isEmpty();
        if (noDifficultySelected) entity.setDifficulty();

        placeNewSide(serverLevel, getState, pos.relative(getState, entity.isStartingRoom() ? 12 : 1), TextHelpers.nameToId(entity.roomId.getString()), false);
        destroyDoors(serverLevel, pos);
        entity.clicked = true;
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

    public static void destroyDoors(ServerLevel serverLevel, BlockPos pos) {
        var range = betweenClosed(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 3, pos.getZ() + 1);

        for (var blockPos : range) {
            var netherite = serverLevel.getBlockState(blockPos).is(BlockReg.LOCK_SUPPORT);
            var observer = serverLevel.getBlockState(blockPos).is(OBSERVER);
            if (observer || netherite) serverLevel.removeBlock(blockPos, false);
        }
    }

}

