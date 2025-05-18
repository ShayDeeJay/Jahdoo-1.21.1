package org.jahdoo.common.block.rune_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleContainer;
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
import org.jahdoo.trial_nexus.utils.Helpers;

import static net.minecraft.core.Direction.*;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.common.block.BlockInteractionHandler.removeItemsFromSlotToHand;
import static org.jahdoo.common.block.BlockInteractionHandler.swapItemsWithHand;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;

public class RuneTable extends BaseEntityBlock {

//    public static final VoxelShape SHAPE_COMBINED = Shapes.or(
//        Block.box(0, 7, 0, 16, 10, 16),
//        Block.box(11, 2, 2, 14, 7, 5),
//        Block.box(4.5, 8.5, 4.5, 11.5, 10.5, 11.5),
//        Block.box(11.25, 3, 5, 13.75, 5.5, 11),
//        Block.box(5, 3, 11.25, 11, 5.5, 13.75),
//        Block.box(5, 3, 2.25, 11, 5.5, 4.75),
//        Block.box(2.25, 3, 5, 4.75, 5.5, 11),
//        Block.box(5.5, 5, 0, 10.5, 7, 0),
//        Block.box(5.5, 5, 0, 10.5, 7, 0),
//        Block.box(5.5, 5, 16, 10.5, 7, 16),
//        Block.box(0, 5, 5.5, 0, 7, 10.5),
//        Block.box(16, 5, 5.5, 16, 7, 10.5),
//        Block.box(11.5, 0, 2.5, 13.5, 2, 4.5),
//        Block.box(2, 2, 2, 5, 7, 5),
//        Block.box(2.5, 0, 2.5, 4.5, 2, 4.5),
//        Block.box(11, 2, 11, 14, 7, 14),
//        Block.box(11.5, 0, 11.5, 13.5, 2, 13.5),
//        Block.box(2.5, 0, 11.5, 4.5, 2, 13.5),
//        Block.box(2, 2, 11, 5, 7, 14)
//    );

    public static final VoxelShape SHAPE_COMBINED = Shapes.or(
        Block.box(5, 13, -4, 11, 16, 0),
        Block.box(3, 10, 0, 13, 16, 16),
        Block.box(4, 12, 17, 12, 16, 19),
        Block.box(6, 5, 4, 10, 10, 12),
        Block.box(3, 3, 3, 13, 5, 13),
        Block.box(0, 0, 0, 16, 3, 16),
        Block.box(5, 12.5, 13, 11, 15.5, 17)
    );

    public static final VoxelShape SHAPE_COMBINED_1 = Shapes.or(
        Block.box(16, 13, 5, 20, 16, 11),
        Block.box(0, 10, 3, 16, 16, 13),
        Block.box(-3, 12, 4, -1, 16, 12),
        Block.box(4, 5, 6, 12, 10, 10),
        Block.box(3, 3, 3, 13, 5, 13),
        Block.box(0, 0, 0, 16, 3, 16),
        Block.box(-1, 12.5, 5, 3, 15.5, 11)
    );

    public static final VoxelShape SHAPE_COMBINED_2 = Shapes.or(
        Block.box(5, 13, 16, 11, 16, 20),
        Block.box(3, 10, 0, 13, 16, 16),
        Block.box(4, 12, -3, 12, 16, -1),
        Block.box(6, 5, 4, 10, 10, 12),
        Block.box(3, 3, 3, 13, 5, 13),
        Block.box(0, 0, 0, 16, 3, 16),
        Block.box(5, 12.5, -1, 11, 15.5, 3)
    );

    public static final VoxelShape SHAPE_COMBINED_4 = Shapes.or(
        Block.box(-4, 13, 5, 0, 16, 11),
        Block.box(0, 10, 3, 16, 16, 13),
        Block.box(17, 12, 4, 19, 16, 12),
        Block.box(4, 5, 6, 12, 10, 10),
        Block.box(3, 3, 3, 13, 5, 13),
        Block.box(0, 0, 0, 16, 3, 16),
        Block.box(13, 12.5, 5, 17, 15.5, 11)
    );


    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public RuneTable() {
        super(Properties.of().strength(1f).sound(SoundType.DEEPSLATE_BRICKS).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, SOUTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new RuneTable());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var direction = state.getValue(FACING);
        return  direction == NORTH ? SHAPE_COMBINED_1 : direction == SOUTH ? SHAPE_COMBINED_4 : direction == EAST ? SHAPE_COMBINED_2 : SHAPE_COMBINED;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if(!level.isClientSide){
            Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundEvents.ANVIL_PLACE, 0.05F, 0.6F);
            Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundReg.SPELL_SOUND.get(), 1F, 1.2F);
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RuneTableEntity(pos,state);
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
        if (level.getBlockEntity(pos) instanceof RuneTableEntity runeTable) {
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
        if(!(entity instanceof RuneTableEntity runeTable)) return FAIL;
        var hasItem = runeTable.getItem().getStackInSlot(0).isEmpty();

        if(!hasItem && player.isShiftKeyDown()){
            removeItemsFromSlotToHand(runeTable.inputItemHandler, 0, player, hand);
            return SUCCESS;
        } else if (stack.has(JAHDOO_GEAR_DATA) && hasItem) {
            swapItemsWithHand(runeTable.inputItemHandler, 0, player, hand);
            return SUCCESS;
        } else {
            if(!(player instanceof ServerPlayer serverPlayer)) return SUCCESS;
            serverPlayer.openMenu(runeTable, pos);
            return SUCCESS;
        }

    }

}

