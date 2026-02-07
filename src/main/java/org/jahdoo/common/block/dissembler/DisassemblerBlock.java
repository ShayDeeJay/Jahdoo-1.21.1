package org.jahdoo.common.block.dissembler;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.BlockEntityReg;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;
import org.shaydee.shaydeeapi.block.BlockInteractionHandler;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.common.block.wand_manager.WandManagerBlock.setOuterRingPulse;

public class DisassemblerBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE_BASE = Block.box(0, 8, 0, 16, 11, 16);
    private static final VoxelShape SHAPE_BASE_SECOND = Block.box(3, 2, 3, 13, 8, 13);
    private static final VoxelShape SHAPE_BASE_THIRD = Block.box(2, 0, 2, 14, 2, 14);
    private static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE_THIRD, SHAPE_BASE_SECOND, SHAPE_BASE);
    private static final BooleanProperty IS_INFUSING = BooleanProperty.create("is_infusing");
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public DisassemblerBlock() {
        super(Properties.of().strength(1f).sound(SoundType.DEEPSLATE_BRICKS).noOcclusion());
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(IS_INFUSING, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new DisassemblerBlock());
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COMMON;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisassemblerBlockEntity(pos,state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(IS_INFUSING);
    }

    private void addItemToHand(
        Player player,
        ItemStack slotItem
    ){
        Helpers.throwOrAddItem(player, slotItem);
        slotItem.shrink(slotItem.getCount());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType,
            BlockEntityReg.INFUSER_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DisassemblerBlockEntity) {
                ((DisassemblerBlockEntity) blockEntity).dropsAllInventory(level);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof DisassemblerBlockEntity tableEntity)) return ItemInteractionResult.FAIL;
        var hands = player.getItemInHand(hand);
        var outputItemHandler = tableEntity.getOutputItemHandler();
        var getOutputSlot = outputItemHandler.getStackInSlot(0);
        var getOutputSlot1 = outputItemHandler.getStackInSlot(1);
        if(!getOutputSlot.isEmpty() || !getOutputSlot1.isEmpty()){
            this.addItemToHand(player, getOutputSlot);
            this.addItemToHand(player, getOutputSlot1);
            return ItemInteractionResult.SUCCESS;
        } else {
            infuserBlockInteraction(level, pos, player, hand, tableEntity, hands, SoundEvents.VAULT_DEACTIVATE, 0.64, 8, 0.4, 0.3);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public static void infuserBlockInteraction(
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        AbstractBEInventory augmentStation,
        ItemStack stack,
        SoundEvent soundEvent,
        double yOffset,
        int lifetime,
        double speed,
        double radius
    ) {
        if (stack.getItem() instanceof JahdooItem || stack.isEmpty() && player.isShiftKeyDown()) {
            if(!stack.isEmpty()) Helpers.getSoundWithPosition(level, pos, soundEvent, SoundSource.BLOCKS, 1F, 1.2f);
            BlockInteractionHandler.swapItemsWithHand(augmentStation.getInputItemHandler(), 0, player, hand);
            var stackInSlot = augmentStation.getInputItemHandler().getStackInSlot(0);
            var type = stackInSlot.get(CUSTOM_MODEL_DATA);
            if (type != null) {
                setOuterRingPulse(level, type.value(), pos, yOffset, lifetime, speed, radius);
            }
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if(blockState.getValue(IS_INFUSING)){
            level.playLocalSound(blockPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.03F, 2.0f, false);
            for(int l3 = 0; l3 < 1; ++l3) {
                float f12 = 2.2F * randomSource.nextFloat() - 1.0F;
                float f14 = 2.2F * randomSource.nextFloat() - 1.0F;
                float f15 = 2.2F * randomSource.nextFloat() - 1.0F;
                level.addParticle(ParticleTypes.SCULK_CHARGE_POP,
                    (double)blockPos.getX() + 0.5D,
                    (double)blockPos.getY() + 0.40D,
                    (double)blockPos.getZ() + 0.5D,
                    f12 * 0.02F,
                    f14 * 0.02F,
                    f15 * 0.02F);
            }
        }
    }
}

