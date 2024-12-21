package org.jahdoo.block.augment_modification_station;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
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
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.BlockInteractionHandler;
import org.jahdoo.block.crafter.CreatorEntity;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;

public class AugmentModificationBlock extends BaseEntityBlock{

    public static VoxelShape SHAPE_COMBINED = Shapes.or(
        Block.box(7, 0.575, 7, 9, 13, 9),
        Block.box(6.75, 5.725, 6.75, 9.25, 9.15, 9.25),
        Block.box(6.75, 9.875, 6.75, 9.25, 13, 9.25),
        Block.box(5.25, 0, 5.25, 10.75, 2, 10.75),
        Block.box(6, 11.25, 7, 7, 12.75, 9),
        Block.box(9, 11.25, 7, 10, 12.75, 9)
    );

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public AugmentModificationBlock() {
        super(sharedBlockBehaviour());
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((t) -> new AugmentModificationBlock());
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMBINED;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof AugmentModificationEntity augmentStation) {
                augmentStation.dropsAllInventory(pLevel);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult fail = ItemInteractionResult.FAIL;
        ItemInteractionResult success = ItemInteractionResult.SUCCESS;

        if(!(level.getBlockEntity(pos) instanceof AugmentModificationEntity augmentStation)) return fail;
        var hands = player.getItemInHand(hand);
        var result = augmentBlockInteraction(level, pos, player, hand, augmentStation, hands, SoundEvents.VAULT_ACTIVATE, 0.05, 10, 0.9, 0.15);
        if(result == fail && !augmentStation.getInteractionSlot().isEmpty() && augmentStation.getInteractionSlot().has(DataComponents.CUSTOM_MODEL_DATA)){
            if(!(player instanceof ServerPlayer serverPlayer)) return fail;
            serverPlayer.openMenu(augmentStation, pos);
            return success;
        }
        return success;
    }

    public static ItemInteractionResult augmentBlockInteraction(
        Level pLevel,
        BlockPos pPos,
        Player pPlayer,
        InteractionHand pHand,
        AbstractBEInventory augmentStation,
        ItemStack hand,
        SoundEvent soundEvent,
        double yOffset,
        int lifetime,
        double speed,
        double radius
    ) {
        if (hand.getItem() instanceof Augment || hand.isEmpty() && pPlayer.isShiftKeyDown()) {
            ModHelpers.getSoundWithPosition(pLevel, pPos, soundEvent, 1, 1.2f);
            BlockInteractionHandler.swapItemsWithHand(augmentStation.inputItemHandler, 0, pPlayer, pHand);
            var stackInSlot = augmentStation.inputItemHandler.getStackInSlot(0);
            var type = stackInSlot.get(DataComponents.CUSTOM_MODEL_DATA);
            if (type != null) {
                setOuterRingPulse(pLevel, type.value(), pPos, yOffset, lifetime, speed, radius);
            }
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
        var particle = genericParticleOptions(ElementRegistry.getElementByTypeId(getType).getFirst(), lifetime, 0.8f);
        PositionGetters.getOuterRingOfRadiusRandom(blockPos.getBottomCenter().add(0,yOffset,0), radius, 40,
            positions -> {
                ParticleHandlers.sendParticles(
                    level, particle, positions.offsetRandom(RandomSource.create(), 0.2f),
                    0, 0, ModHelpers.Random.nextDouble(0.02,0.2),0,speed
                );
            }
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AugmentModificationEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.AUGMENT_MODIFICATION_STATION_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}
