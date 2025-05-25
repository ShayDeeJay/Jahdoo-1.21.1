package org.jahdoo.common.block.wand_manager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.utils.PositionFinders;

import static org.jahdoo.common.block.BlockInteractionHandler.swapItemsWithHand;
import static org.jahdoo.common.registers.mod.ElementReg.fromWand;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;
import static org.jahdoo.trial_nexus.utils.Helpers.getSoundWithPosition;

public class WandManagerBlock extends BaseEntityBlock {

    public static VoxelShape SHAPE_COMBINED = Shapes.or(
        Block.box(5, 0, 11.125, 11, 2, 13.125),
        Block.box(5, 0, 2.875, 11, 2, 4.875),
        Block.box(2.875, 0, 5, 4.875, 2, 11),
        Block.box(11.125, 0, 5, 13.125, 2, 11),
        Block.box(4.75, 0, 4.75, 5.75, 3.0124999999999993, 11.25),
        Block.box(4.75, 3.0124999999999993, 4.75, 5.75, 11.4875, 5.75),
        Block.box(4.75, 3.0124999999999993, 10.25, 5.75, 11.4875, 11.25),
        Block.box(4.75, 11.4875, 4.75, 5.75, 15, 11.25),
        Block.box(6.2414200000000015, 14.125, 7.25, 7.2414200000000015, 15.875, 8.75),
        Block.box(8.758579999999998, 14.125, 7.25, 9.758579999999998, 15.875, 8.75),
        Block.box(7.25, 14.125, 8.75858, 8.75, 15.875, 9.75858),
        Block.box(7.25, 14.125, 6.2414200000000015, 8.75, 15.875, 7.2414200000000015),
        Block.box(7, 0, 11, 9, 1.75, 13),
        Block.box(3, 0, 7, 5, 1.75, 9),
        Block.box(7, 0, 3, 9, 1.75, 5),
        Block.box(11, 0, 7, 13, 1.75, 9),
        Block.box(7.05905, 14.987450000000003, 8.31365, 8.94095, 16.24205, 8.94095),
        Block.box(8.31365, 14.987450000000003, 7.05905, 8.94095, 16.24205, 8.94095),
        Block.box(7.05905, 14.987450000000003, 7.05905, 8.94095, 16.24205, 7.68635),
        Block.box(7.05905, 14.987450000000003, 7.05905, 7.68635, 16.24205, 8.94095),
        Block.box(5.75, 3, 9.25, 10.25, 11.4875, 10.75),
        Block.box(5.75, 3, 5.25, 10.25, 11.4875, 6.75),
        Block.box(9.25, 3, 5.75, 10.75, 11.4875, 10.25),
        Block.box(5.25, 3, 5.75, 6.75, 11.4875, 10.25),
        Block.box(5.75, 0, 4.75, 10.25, 3.0124999999999993, 11.25),
        Block.box(5.75, 11.4875, 4.75, 10.25, 15, 11.25),
        Block.box(10.25, 0, 4.75, 11.25, 3.0124999999999993, 11.25),
        Block.box(10.25, 3.0124999999999993, 4.75, 11.25, 11.4875, 5.75),
        Block.box(10.25, 3.0124999999999993, 10.25, 11.25, 11.4875, 11.25),
        Block.box(10.25, 11.4875, 4.75, 11.25, 15, 11.25)
    );

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public WandManagerBlock() {
        super(Properties.of().strength(1f).sound(SoundType.DEEPSLATE_BRICKS).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new WandManagerBlock());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getClockWise());
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WandManagerEntity(pPos,pState);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        return SHAPE_COMBINED;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(
            pBlockEntityType,
            BlockEntityReg.WAND_MANAGER_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        var fail = ItemInteractionResult.FAIL;
        var success = ItemInteractionResult.SUCCESS;

        if(!(level.getBlockEntity(pos) instanceof WandManagerEntity wandManager)) return fail;
        var hands = player.getItemInHand(interactionHand);
        var result = augmentBlockInteraction(level, pos, player, interactionHand, wandManager, hands, SoundEvents.VAULT_ACTIVATE, 1, 10, 0.6, 0.12);

        if(result == fail && !wandManager.getWandSlot().isEmpty()){
            if(!(player instanceof ServerPlayer serverPlayer)) return fail;
            serverPlayer.openMenu(wandManager, pos);
            return success;
        }

        return success;
    }

    public static ItemInteractionResult augmentBlockInteraction(
        Level pLevel,
        BlockPos pPos,
        Player pPlayer,
        InteractionHand pHand,
        WandManagerEntity wandManagerTable,
        ItemStack hand,
        SoundEvent soundEvent,
        double yOffset,
        int lifetime,
        double speed,
        double radius
    ) {
        if (CastHelper.validCasterType(hand.getItem()) || hand.isEmpty() && pPlayer.isShiftKeyDown()) {
            if(!hand.isEmpty()) getSoundWithPosition(pLevel, pPos, soundEvent, 1, 1.2f);
            swapItemsWithHand(wandManagerTable.inputItemHandler, 0, pPlayer, pHand);

            var stackInSlot = wandManagerTable.inputItemHandler.getStackInSlot(0);
            var type = fromWand(stackInSlot.getItem());

            wandManagerTable.privateTicks = 0;
            type.ifPresent(getType -> setOuterRingPulse(pLevel, getType.id(), pPos, yOffset, lifetime, speed, radius));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

    public static void setOuterRingPulse(
        Level level,
        int getType,
        BlockPos blockPos,
        double yOffset,
        int lifetime,
        double speed,
        double radius
    ){
        var element = ElementReg.fromId(getType);
        element.ifPresent(
            e -> {
                var particle = ParticleHandlers.genericParticle(e, lifetime, 0.8f);
                PositionFinders.getOuterRingOfRadiusRandom(blockPos.getBottomCenter().add(0,yOffset,0), radius, 40,
                    positions -> {
                        ParticleHandlers.sendParticles(
                            level, particle, positions.offsetRandom(RandomSource.create(), 0.2f),
                            0, 0, Random.nextDouble(0.02,0.2),0,speed
                        );
                    }
                );
            }
        );
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            var blockEntity = pLevel.getBlockEntity(pPos);

            if (blockEntity instanceof WandManagerEntity wandManagerTable) {
                var inputInventory = new SimpleContainer(wandManagerTable.setInputSlots());
                var outputInventory = new SimpleContainer(wandManagerTable.setOutputSlots());
                var outHandler = wandManagerTable.outputItemHandler;

                for (int i = 0; i < 4; i++) {
                    if (i < inputInventory.getContainerSize()) {
                        inputInventory.setItem(i, wandManagerTable.inputItemHandler.getStackInSlot(i));
                    }
                }

                for (int i = 0; i < outHandler.getSlots(); i++) {
                    if (i < outputInventory.getContainerSize()) {
                        outputInventory.setItem(i, outHandler.getStackInSlot(i));
                    }
                }

                Containers.dropContents(pLevel, pPos, inputInventory);
                Containers.dropContents(pLevel, pPos, outputInventory);
            }

        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

}

