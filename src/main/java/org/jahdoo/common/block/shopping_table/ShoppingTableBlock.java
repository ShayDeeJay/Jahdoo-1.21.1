package org.jahdoo.common.block.shopping_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.Helpers;

import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter.EMPTY;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter.checkAndPurchase;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.withStyleComponent;

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
        if(!table.canPurchase()) return FAIL;
        var enoughToBuy = checkAndPurchase(table.itemCosts, player);

        if(enoughToBuy){
            var isRandomisedTable = state.getValue(TEXTURE) == 3;
            if (isRandomisedTable) table.insertRandomItem();

            var item = table.getItem();
            var stackInSlot = item.extractItem(0, item.getStackInSlot(0).getCount(), false);
            if (!stackInSlot.isEmpty()) {
                player.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 0.8F);
                Helpers.throwOrAddItem(player, stackInSlot);
                item.setStackInSlot(1, ItemStack.EMPTY);
                table.itemCosts = EMPTY;
                Helpers.getSoundWithPosition(level, pos, SoundReg.COINBOX_OPEN.get(), SoundSource.BLOCKS, 1F, 2F);
            }

        } else {
            player.displayClientMessage(withStyleComponent("Insufficient Funds!", ColourStore.NEGATIVE_RED), true);
            Helpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_REJECT_REWARDED_PLAYER, SoundSource.BLOCKS, 0.3F, 2F);
        }

        return SUCCESS;
    }

}


