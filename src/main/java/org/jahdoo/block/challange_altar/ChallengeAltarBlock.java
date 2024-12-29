package org.jahdoo.block.challange_altar;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.attachments.player_abilities.ChallengeAltarData.altarClickToStart;
import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;

public class ChallengeAltarBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE_BASE = Block.box(4.5, 5, 4.5, 11.5, 32, 11.5);
    public static final VoxelShape SHAPE_BASE_SECOND = Block.box(3.5, 0, 3.5, 12.5, 5, 12.5);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE_SECOND, SHAPE_BASE);

    public ChallengeAltarBlock() {
        super(sharedBlockBehaviour());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new ChallengeAltarBlock());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMMON;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altarE)) return ItemInteractionResult.FAIL;
        altarClickToStart(altarE);
        return ItemInteractionResult.SUCCESS;
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

