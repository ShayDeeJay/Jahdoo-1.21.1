package org.jahdoo.common.block.shopping_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.trading_post.ItemCosts;
import org.jahdoo.common.items.augments.AugmentItemHelper;
import org.jahdoo.common.registers.BlockEntitiesRegister;
import org.jetbrains.annotations.Nullable;

public class ShoppingTableBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static VoxelShape SHAPE_COMBINED = Shapes.or(
        Block.box(2, 0, 2, 14, 10, 14),
        Block.box(0, 10, 0, 16, 14, 16),
        Block.box(12, 0, 12, 16, 10, 16),
        Block.box(12, 0, 0, 16, 10, 4),
        Block.box(0, 0, 12, 4, 10, 16),
        Block.box(0, 0, 0, 4, 10, 4)
    );

    public static final IntegerProperty TEXTURE = BlockStateProperties.LEVEL;
    public static final DirectionProperty FACING = DirectionalBlock.FACING;


    public ShoppingTableBlock() {
        super(Properties.of().strength(1f).noOcclusion());
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(TEXTURE, 2)
                .setValue(FACING, Direction.SOUTH)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec((x) -> new ShoppingTableBlock());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COMBINED;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShoppingTableEntity(pos,state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEXTURE);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> entityType
    ) {
        return createTickerHelper(
            entityType, BlockEntitiesRegister.SHOPPING_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    public static boolean hasEnoughToBuy(Player player, Item item, int quantity) {
        var count = 0;
        for (var stack : player.getInventory().items) {
            if (stack.getItem() == item) count += stack.getCount();
            if (count >= quantity) break;
        }

        if (count < quantity) return false;
        var remaining = quantity;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == item) {
                var stackSize = stack.getCount();

                if (stackSize <= remaining) {
                    stack.setCount(0);
                    remaining -= stackSize;
                } else {
                    stack.shrink(remaining);
                    remaining = 0;
                }

                if (remaining <= 0) break;
            }
        }

        return true;
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
        if(!(entity instanceof ShoppingTableEntity shoppingTable)) return ItemInteractionResult.FAIL;
        var success = ItemInteractionResult.SUCCESS;

        if(!shoppingTable.canPurchase()) return success;
        var enoughToBuy = hasEnoughToBuy(player, shoppingTable.getCurrencyType().getItem(), shoppingTable.getCost());

        if(enoughToBuy){
            var isRandomisedTable = state.getValue(TEXTURE) == 3;
            if (isRandomisedTable) {
                shoppingTable.insertRandomItem();
            }

            var item = shoppingTable.getItem();
            var stackInSlot = item.extractItem(0, item.getStackInSlot(0).getCount(), false);
            if (!stackInSlot.isEmpty()) {
                player.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 0.8F);
                AugmentItemHelper.throwOrAddItem(player, stackInSlot);
                item.setStackInSlot(1, ItemStack.EMPTY);
                shoppingTable.itemCosts = ItemCosts.EMPTY_COST;
            }
        } else {
            if(level.isClientSide) player.displayClientMessage(Component.literal("Insufficient Funds"), true);
        }

        return success;
    }

}

