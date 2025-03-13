package org.jahdoo.common.block.perk_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.registers.AttachmentReg;

import java.awt.*;

import static net.minecraft.world.ItemInteractionResult.*;
import static org.jahdoo.common.registers.BlockEntityReg.PERK_TABLE_BE;

public class PerkTable extends BaseEntityBlock {

    public static final VoxelShape SHAPE_BASE = Block.box(2, 0, 2, 14, 3, 14);
    public static final IntegerProperty TEXTURE = BlockStateProperties.LEVEL;

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
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        level.destroyBlock(pos, false);
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEXTURE);
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
        if (!(level instanceof ServerLevel)) return FAIL;
        if(!(level.getBlockEntity(pos) instanceof PerkTableEntity entity)) return FAIL;

        entity.setUsed(state, player);
        return SUCCESS;
    }

}

