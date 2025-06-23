package org.jahdoo.common.block.divine_forge;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;

import static net.minecraft.core.Direction.*;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.common.block.BlockInteractionHandler.removeItemsFromSlotToHand;
import static org.jahdoo.common.block.BlockInteractionHandler.swapItemsWithHand;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class DivineForge extends BaseEntityBlock {

    public static final VoxelShape SHAPE_COMBINED = Shapes.or(
        Block.box(3, 10, 0, 13, 16, 16),
        Block.box(6, 5, 4, 10, 10, 12),
        Block.box(3, 3, 3, 13, 5, 13),
        Block.box(0, 0, 0, 16, 3, 16)
    );

    public static final VoxelShape SHAPE_COMBINED_1 = Shapes.or(
        Block.box(0, 10, 3, 16, 16, 13),
        Block.box(4, 5, 6, 12, 10, 10),
        Block.box(3, 3, 3, 13, 5, 13),
        Block.box(0, 0, 0, 16, 3, 16)
    );

    public static final VoxelShape SHAPE_COMBINED_2 = Shapes.or(
        Block.box(3, 10, 0, 13, 16, 16),
        Block.box(6, 5, 4, 10, 10, 12),
        Block.box(3, 3, 3, 13, 5, 13),
        Block.box(0, 0, 0, 16, 3, 16)
    );

    public static final VoxelShape SHAPE_COMBINED_4 = Shapes.or(
        Block.box(0, 10, 3, 16, 16, 13),
        Block.box(4, 5, 6, 12, 10, 10),
        Block.box(3, 3, 3, 13, 5, 13),
        Block.box(0, 0, 0, 16, 3, 16)
    );

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public DivineForge() {
        super(Properties.of().strength(1f).sound(SoundType.DEEPSLATE_BRICKS).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, SOUTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new DivineForge());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var direction = state.getValue(FACING);
        return  direction == NORTH ? SHAPE_COMBINED_1 : direction == SOUTH ? SHAPE_COMBINED_4 : direction == EAST ? SHAPE_COMBINED_2 : SHAPE_COMBINED;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if(!level.isClientSide){
            Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundEvents.ANVIL_PLACE, 0.1F, 0.6F);
            Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundReg.SPELL_SOUND.get(), 0.6F, 1.2F);
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DivineForgeEntity(pos,state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> entityType
    ) {
        return createTickerHelper(
            entityType, BlockEntityReg.RUNE_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    public void onRemove(
        BlockState state,
        Level level,
        BlockPos pos,
        BlockState newState,
        boolean movedByPiston
    ) {
        if (level.getBlockEntity(pos) instanceof DivineForgeEntity runeTable) {
            var handler = runeTable.inputItemHandler;
            for(int i = 0; i < 4; i++){
                var inputInventory = new SimpleContainer(1);
                inputInventory.addItem(handler.getStackInSlot(i));
                Containers.dropContents(level, pos, inputInventory);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult result
    ) {

        var entity = level.getBlockEntity(pos);
        if(!(entity instanceof DivineForgeEntity divineForge)) return FAIL;
        var hasItem = divineForge.getItem().getStackInSlot(0).isEmpty();

        if(!hasItem && player.isShiftKeyDown()){
            removeItemsFromSlotToHand(divineForge.inputItemHandler, 0, player, hand);
            return SUCCESS;
        } else if (stack.has(JAHDOO_GEAR_DATA)) {

            swapItemsWithHand(divineForge.inputItemHandler, 0, player, hand);
            divineForge.stand = EntityType.ARMOR_STAND.create(level);
            setOuterRingPulse(level, pos, 0.8, 20, 1.5, 0.2, ColourStore.NEGATIVE_RED, 80);
            Helpers.getSoundWithPosition(level, pos, SoundReg.SPELL_SOUND.get(), 0.4F);
            Helpers.getSoundWithPosition(level, pos, SoundReg.SUSPEND.get());
            divineForge.privateTicks = 0;

            return SUCCESS;
        } else {
            if(!(player instanceof ServerPlayer serverPlayer)) return SUCCESS;
            serverPlayer.openMenu(divineForge, pos);
            return SUCCESS;
        }

    }

    public static void setOuterRingPulse(
        Level level,
        BlockPos blockPos,
        double yOffset,
        int lifetime,
        double speed,
        double radius,
        int colour,
        int points
    ){
        var particle = genericParticle(lifetime, 1, colour, colour);
        PositionFinders.getOuterRingOfRadiusRandom(blockPos.getBottomCenter().add(0,yOffset,0), radius, points,
            positions -> {
                sendParticles(
                    level, particle, positions.offsetRandom(RandomSource.create(), 0.2f),
                    0, 0, Random.nextDouble(0.02,0.1),0,speed
                );
            }
        );
    }

}

