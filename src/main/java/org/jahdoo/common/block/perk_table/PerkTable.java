package org.jahdoo.common.block.perk_table;

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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.shaydee.shaydeeapi.helpers.BlockHelpers;

import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.common.registers.BlockEntityReg.PERK_TABLE_BE;

public class PerkTable extends BaseEntityBlock {

    public static final VoxelShape SHAPE_BASE = Block.box(0, 0, 0, 16, 16, 16);
    public static final IntegerProperty TEXTURE = BlockStateProperties.LEVEL;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public PerkTable() {
        super(
            Properties.of()
                .strength(1f)
                .sound(SoundType.COPPER_BULB)
                .noOcclusion()
        );
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(TEXTURE, 1)
                .setValue(HALF, DoubleBlockHalf.LOWER)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new PerkTable());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BASE;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        BlockHelpers.placeDoubleTallBlock(state, level, pos);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEXTURE).add(HALF);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PerkTableEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, PERK_TABLE_BE.get(),
            (levelA, pos, newState, entity) -> entity.tick(levelA, pos, newState)
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
        var isUpper = state.getValue(HALF).equals(DoubleBlockHalf.UPPER);
        var getBottomHalf = level.getBlockEntity(isUpper ? pos.below() : pos);
        if(!(getBottomHalf instanceof PerkTableEntity entity)) return FAIL;

        entity.setUsed(state, player);
        return SUCCESS;
    }

}

