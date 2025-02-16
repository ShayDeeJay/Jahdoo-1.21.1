package org.jahdoo.block.challange_altar;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ability.abilities.ability_data.EscapeDecoyAbility;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.challenge.LevelGenerator;
import org.jahdoo.challenge.RewardLootTables;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.Direction.SOUTH;
import static org.jahdoo.attachments.player_abilities.ChallengeAltarData.*;
import static org.jahdoo.attachments.player_abilities.ChallengeAltarData.nextSubRound;
import static org.jahdoo.attachments.player_abilities.ChallengeAltarData.resetWithRound;
import static org.jahdoo.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.registers.BlocksRegister.LOOT_CHEST;
import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;
import static org.jahdoo.utils.ModHelpers.Random;

public class ChallengeAltarBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE_BASE = Block.box(4.5, 5, 4.5, 11.5, 32, 11.5);
    public static final VoxelShape SHAPE_BASE_SECOND = Block.box(3.5, 0, 3.5, 12.5, 5, 12.5);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE_SECOND, SHAPE_BASE);

    public ChallengeAltarBlock() {
        super(sharedBlockBehaviour().strength(-1.0F, 3600000.0F));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new ChallengeAltarBlock());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMMON;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altarE)) return ItemInteractionResult.FAIL;
        if (!(level instanceof ServerLevel)) return ItemInteractionResult.FAIL;
        var altarData = getProperties(altarE);
        if (!isCompleted(altarE)) {
            var startingRound = Math.max(1, altarData.round());
            if (!isActive(altarE)) {
                startNewChallenge(altarE, startingRound);
            } /*else {
                readyNextSubRound(altarE, altarData, startingRound);
            }*/
        }

        return ItemInteractionResult.SUCCESS;
    }

    private static void startNewChallenge(ChallengeAltarBlockEntity altarE, int startingRound) {
        var endingRound = startingRound + 5;
        resetWithRound(altarE, startingRound, endingRound);
    }

    public static void readyNextSubRound(ChallengeAltarBlockEntity altarE, ChallengeAltarData altarData, int startingRound) {
        if (!altarData.isSubRoundActive(altarE)) nextSubRound(altarE, startingRound);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ChallengeAltarBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.CHALLENGE_ALTAR_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }
}

