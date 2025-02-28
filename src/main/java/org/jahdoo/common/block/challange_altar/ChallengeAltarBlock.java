package org.jahdoo.common.block.challange_altar;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.ItemInteractionResult.*;
import static org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData.*;
import static org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData.nextSubRound;
import static org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData.resetWithRound;
import static org.jahdoo.common.registers.BlockReg.sharedBehaviour;

public class ChallengeAltarBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE_BASE = Block.box(4.5, 5, 4.5, 11.5, 32, 11.5);
    private static final VoxelShape SHAPE_BASE_SECOND = Block.box(3.5, 0, 3.5, 12.5, 5, 12.5);
    private static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE_SECOND, SHAPE_BASE);

    public ChallengeAltarBlock() {
        super(sharedBehaviour.strength(-1.0F, 3600000.0F));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new ChallengeAltarBlock());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COMMON;
    }

    public static void readyNextSubRound(ChallengeAltarBlockEntity altarE, ChallengeLevelData altarData, int startingRound) {
        if (!altarData.isSubRoundActive(altarE)) nextSubRound(altarE, startingRound);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChallengeAltarBlockEntity(pos,state);
    }

    private static void startNewChallenge(ChallengeAltarBlockEntity altarE, int startingRound) {
        var endingRound = startingRound + 5;
        resetWithRound(altarE, startingRound, endingRound);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {

        return createTickerHelper(
            entityType,
            BlockEntityReg.CHALLENGE_ALTAR_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altarE)) return FAIL;
        if (!(level instanceof ServerLevel)) return FAIL;

        var altarData = getProperties(altarE);
        if (!isCompleted(altarE)) {
            var startingRound = Math.max(1, altarData.round());
            if (!isActive(altarE)) {
                startNewChallenge(altarE, startingRound);
            }
        }

        return SUCCESS;
    }
}

