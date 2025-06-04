package org.jahdoo.common.block.ticket_bureau;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.block.BlockInteractionHandler;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.components.TicketData;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.utils.Helpers;

import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static net.minecraft.world.level.block.EnchantingTableBlock.BOOKSHELF_OFFSETS;
import static org.jahdoo.common.registers.BlockEntityReg.TICKET_BUREAU_BE;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class TicketBureauBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static final VoxelShape SHAPE_COMMON = Shapes.or(
        Block.box(0, 6, 0, 16, 9, 16),
        Block.box(1, 0, 1, 4, 6, 4),
        Block.box(12, 0, 1, 15, 6, 4),
        Block.box(12, 0, 12, 15, 6, 15),
        Block.box(1, 0, 12, 4, 6, 15)
    );

    public TicketBureauBlock() {
        super(
            Properties.of()
                .strength(1f)
                .sound(SoundType.CHERRY_WOOD)
                .noOcclusion()
        );
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, SOUTH));
    }
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec((x) -> new TicketBureauBlock());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COMMON;
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TicketBureauBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, TICKET_BUREAU_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TicketBureauBlockEntity tankBlockEntity)
                tankBlockEntity.dropsAllInventory(level);
        }
        super.onRemove(state, level, pos, newState, moveByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var handItem = player.getMainHandItem();
        var entity = level.getBlockEntity(pos);

        if(entity instanceof TicketBureauBlockEntity entity1){
            var ticket = entity1.getTicketItem();
            if(handItem.is(ItemReg.STAMP) && !ticket.isEmpty()){
                var comp = handItem.get(DataComponents.CUSTOM_NAME);
                var x = comp.getStyle().getColor();
                System.out.println(x);
                for (BlockPos blockpos : BOOKSHELF_OFFSETS) {
                    level.addParticle(
                        new GenericParticleOptions(ParticleStore.MAGIC_MOVE_PARTICLE, ElementReg.random().textColourA(), 0, Random.nextInt(10, 30), Random.nextInt(2,5), false, 5),
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 2.0,
                        (double)pos.getZ() + 0.5,
                        (double)((float)blockpos.getX() + Random.nextFloat()) - 0.5,
                        (double)((float)blockpos.getY() - Random.nextFloat() - 1.0F),
                        (double)((float)blockpos.getZ() + Random.nextFloat()) - 0.5
                    );
                }
                Helpers.getSoundWithPosition(level,pos, SoundReg.DASH_EFFECT.get(), 1, 0.5F);
                Helpers.getSoundWithPosition(level,pos, SoundReg.UNLOCK_NOTIFICATION.get(), 1, 0.2F);
                addStampToTicket(handItem, ticket);
                var filled = CoreData.getFilled(ticket);
                var required = CoreData.getRequired(ticket);
                ticket.set(ComponentReg.CORE_DATA, new CoreData(required + 30, filled));
                handItem.shrink(1);
                return SUCCESS;
            }

            if (handItem.is(ItemReg.TRIAL_TICKET) || handItem.isEmpty()) {
                BlockInteractionHandler.swapItemsWithHand(entity1.inputItemHandler, 0, player, hand);
                return SUCCESS;
            }

            return FAIL;
        }

        return FAIL;
    }

    public static void addStampToTicket(ItemStack handItem, ItemStack ticket) {
        var getData = handItem.get(ComponentReg.TICKET_DATA);
        if(getData == null) return;
        for (var stringDoubleEntry : getData.values().entrySet()) {
            TicketData.addNewEntry(ticket, stringDoubleEntry.getKey(), stringDoubleEntry.getValue());
        }
    }
}

