package org.jahdoo.block.loot_chest;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ability.abilities.ability_data.EscapeDecoyAbility;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.challenge.RewardLootTables;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.attachments.player_abilities.ChallengeAltarData.*;
import static org.jahdoo.block.augment_modification_station.AugmentModificationBlock.SHAPE_COMBINED;
import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;
import static org.jahdoo.utils.ModHelpers.Random;

public class LootChestBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final VoxelShape SHAPE = Block.box(0.5, 0.47499999999999964, 1.4749999999999979, 15.5, 9.225, 14.474999999999998);
    public static final VoxelShape SHAPE2 = Block.box(1.5, 0.47499999999999964, 0.47499999999999787, 14.5, 9.225, 15.474999999999998);
    public LootChestBlock() {
        super(sharedBlockBehaviour());
        this.registerDefaultState(
            this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new LootChestBlock());
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(FACING) == Direction.EAST || pState.getValue(FACING) == Direction.WEST ? SHAPE2 : SHAPE;
    }
    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof LootChestEntity lootChestEntity)) return ItemInteractionResult.FAIL;
        lootChestEntity.setOpen(true);
        if (level instanceof ServerLevel serverLevel) {
            completionLoot(serverLevel, pos);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }


    private static void completionLoot(ServerLevel serverLevel, BlockPos pos) {
        var rewards = RewardLootTables.getCompletionLoot(serverLevel, pos.getCenter());
        lootSplosion(serverLevel, pos, serverLevel, rewards);
//        serverLevel.destroyBlock(pos, false);
    }

    private static void lootSplosion(Level level, BlockPos pos, ServerLevel serverLevel, ObjectArrayList<ItemStack> rewards) {
        for (var reward : rewards) {
            var pCenter = pos.getCenter();
            var itemEntity = new ItemEntity(serverLevel, pCenter.x(), pCenter.y() , pCenter.z(), reward);
            var angle = Random.nextDouble() * 2 * Math.PI;
            var horizontalOffset = 0.2 + Random.nextDouble() * 0.45;
            var offsetX = Math.cos(angle) * horizontalOffset;
            var offsetZ = Math.sin(angle) * horizontalOffset;
            var velocity = new Vec3(offsetX * (Math.random() - 0.5), 7, offsetZ * (Math.random() - 0.5));
            itemEntity.setDeltaMovement(velocity);
            itemEntity.setPickUpDelay(30);

            for (var element : ElementRegistry.REGISTRY) {
                ParticleHandlers.particleBurst(
                    serverLevel, pCenter.add(0, 0.5f, 0), 1,
                    EscapeDecoyAbility.getFromAllRandom(element, 20, 1),
                    0, 0.7, 0, 0.2f, 3
                );
            }

            serverLevel.addFreshEntity(itemEntity);
        }
        ModHelpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_OPEN_SHUTTER, 1f, 1.8f);
        ModHelpers.getSoundWithPosition(level, pos, SoundEvents.ILLUSIONER_CAST_SPELL, 1f, 1f);
        ModHelpers.getSoundWithPosition(level, pos, SoundRegister.EXPLOSION.get(), 1f, 0.9f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LootChestEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.LOOT_CHEST_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }
}

