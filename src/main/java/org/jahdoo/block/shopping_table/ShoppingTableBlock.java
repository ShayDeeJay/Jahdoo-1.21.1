package org.jahdoo.block.shopping_table;

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
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.utils.ModHelpers;
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMBINED;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEXTURE);
        builder.add(FACING);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        var entity = level.getBlockEntity(pos);
        if(!(entity instanceof ShoppingTableEntity shoppingTable)) return ItemInteractionResult.FAIL;

        if(shoppingTable.canPurchase()){
            var cost = shoppingTable.getCost();
            var enoughToBuy = hasEnoughToBuy(player, cost.getItem(), cost.getCount());

            if(enoughToBuy){
                var isRandomisedTable = state.getValue(TEXTURE) == 3;
                if (isRandomisedTable) {
                    System.out.println("im ere");
                    shoppingTable.insertRandomItem();
                }

                var item = shoppingTable.getItem();
                var stackInSlot = item.extractItem(0, item.getStackInSlot(0).getCount(), false);
                if (!stackInSlot.isEmpty()) {
                    player.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 0.8F);
                    AugmentItemHelper.throwOrAddItem(player, stackInSlot);
                    item.setStackInSlot(1, ItemStack.EMPTY);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        ModHelpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_CLOSE_SHUTTER, 0.6F,2F);
        if(level.isClientSide) player.displayClientMessage(Component.literal("Insufficient Funds"), true);
        return ItemInteractionResult.SUCCESS;
    }

    public static boolean hasEnoughToBuy(Player player, Item item, int quantity) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == item) count += stack.getCount();
            if (count >= quantity) break;
        }

        if (count >= quantity) {
            int remaining = quantity;
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == item) {
                    int stackSize = stack.getCount();

                    if (stackSize <= remaining) {
                        remaining -= stackSize;
                        stack.setCount(0);
                    } else {
                        stack.shrink(remaining);
                        remaining = 0;
                    }

                    if (remaining <= 0) break;
                }
            }
            return true;
        }

        return false; // Not enough items
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ShoppingTableEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(
            pBlockEntityType, BlockEntitiesRegister.SHOPPING_TABLE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}

