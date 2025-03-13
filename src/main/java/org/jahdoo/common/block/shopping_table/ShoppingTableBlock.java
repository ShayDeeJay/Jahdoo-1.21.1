package org.jahdoo.common.block.shopping_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.trading_post.ItemCosts;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.component.DataComponents.*;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.world.ItemInteractionResult.*;
import static org.jahdoo.common.items.augments.AugmentItemHelper.throwOrAddItem;

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
                .setValue(TEXTURE, 0)
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
            entityType, BlockEntityReg.SHOPPING_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    public static boolean hasEnoughToBuy(Player player, ItemStack item, int quantity) {
        var count = 0;
        var compItem = item.get(CUSTOM_MODEL_DATA);
//        if(compItem == null) return false;

        var getCoins = player.getInventory().items.stream().filter(items -> items.is(ItemReg.COIN));

        for (var itemStack : getCoins.toList()) {
            var stack = itemStack.get(CUSTOM_MODEL_DATA);
            if(stack != null){

            } else {
//                System.out.println(itemStack.getCount());
            }
        }

//        for (var stack : player.getInventory().items) {
//            var invItem = stack.get(CUSTOM_MODEL_DATA);
//            if(invItem != null && compItem != null) {
//                if (stack.getItem() == item.getItem() && invItem.value() == compItem.value()) count += stack.getCount();
//            }
//            if (count >= quantity) break;
//        }
//
//        if (count < quantity) return false;
//        var remaining = quantity;
//
//        for (var stack : player.getInventory().items) {
//            var invItem = stack.get(CUSTOM_MODEL_DATA);
//            if(invItem != null && compItem != null){
//                if (stack.getItem() == item.getItem() &&  invItem.value() == compItem.value()) {
//                    var stackSize = stack.getCount();
//
//                    if (stackSize <= remaining) {
//                        stack.setCount(0);
//                        remaining -= stackSize;
//                    } else {
//                        stack.shrink(remaining);
//                        remaining = 0;
//                    }
//
//                    if (remaining <= 0) break;
//                }
//            }
//        }

        return false;
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
        if(!(entity instanceof ShoppingTableEntity table)) return FAIL;
        var success = SUCCESS;

        if(!table.canPurchase()) return success;
        var enoughToBuy = hasEnoughToBuy(player, table.getCurrencyType(), table.getCost());

        if(enoughToBuy){
            var isRandomisedTable = state.getValue(TEXTURE) == 3;
            if (isRandomisedTable) table.insertRandomItem();

            var item = table.getItem();
            var stackInSlot = item.extractItem(0, item.getStackInSlot(0).getCount(), false);
            if (!stackInSlot.isEmpty()) {
                player.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 0.8F);
                throwOrAddItem(player, stackInSlot);
                item.setStackInSlot(1, ItemStack.EMPTY);
                table.itemCosts = ItemCosts.EMPTY_COST;
            }

        } else {
            if(level.isClientSide) player.displayClientMessage(literal("Insufficient Funds"), true);
        }

        return success;
    }

}


