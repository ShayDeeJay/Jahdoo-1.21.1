package org.jahdoo.ascension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.trading_post.ItemCosts;
import org.jahdoo.common.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;

import java.util.concurrent.atomic.AtomicInteger;
import static net.minecraft.core.component.DataComponents.*;
import static org.jahdoo.ascension.trading_post.ShoppingItems.getEliteShoppingItem;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.block.TrialPortalBlock.*;
import static org.jahdoo.common.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.common.block.shopping_table.ShoppingTableBlock.TEXTURE;
import static org.jahdoo.common.registers.AttachmentReg.CHALLENGE_ALTAR;
import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.common.registers.ItemReg.AUGMENT;
import static org.jahdoo.common.registers.ItemReg.RUNE;

public class BlockSetupManager {

    private static void setLootChests(ServerLevel level, BlockPos pos, Direction direction) {
        var chestState = LOOT_CHEST.get().defaultBlockState().setValue(FACING, direction);
        if(level.getBlockState(pos).is(Blocks.MAGENTA_CONCRETE)){
            level.setBlockAndUpdate(pos, chestState);
        }
    }

    static void setTrialDim(ServerLevel level, ChallengeLevelData data) {
        var pos = new BlockPos(-25, 67, -72);
        level.setBlockAndUpdate(pos, BlockReg.CHALLENGE_ALTAR.get().defaultBlockState());
        if(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altar){
            altar.setData(CHALLENGE_ALTAR, data);
            altar.setChanged();
        }
    }

    static void generateTradingPost(ServerLevel level, Iterable<BlockPos> pos, Direction direction) {
        var shoppingTableState = SHOPPING_TABLE.get().defaultBlockState();
        for (var blockPos : pos) {
            setLocks(level, blockPos);
            setLootChests(level, blockPos, direction);
            uniqueItems(level, shoppingTableState, blockPos, direction);
            otherShopping(level, shoppingTableState, blockPos, direction);
            keyTable(level, shoppingTableState, blockPos, direction);
            generateEntranceAndExit(level, blockPos, direction);
        }
    }

    private static void setLocks(ServerLevel level, BlockPos blockPos) {
        var blockState = level.getBlockState(blockPos);
        if(blockState.is(Blocks.OBSERVER)){
            level.setBlockAndUpdate(blockPos, BlockReg.LOCK.get().defaultBlockState().setValue(FACING,blockState.getValue(FACING)));

            if(level.getBlockEntity(blockPos) instanceof LockBlockEntity lockBlockEntity){
                if(!lockBlockEntity.canPlace()){
                    level.setBlockAndUpdate(blockPos, Blocks.NETHERITE_BLOCK.defaultBlockState());
                }
            }
        }
    }

    private static void generateEntranceAndExit(ServerLevel level, BlockPos pos, Direction direction) {
        var portalBlock = TRAIL_PORTAL.get().defaultBlockState();

        if(level.getBlockState(pos).is(Blocks.BLUE_CONCRETE)){
            for(int x = 0; x < 5; x++) {
                level.setBlockAndUpdate(pos.above(x), portalBlock.setValue(DIMENSION_KEY, KEY_TRIAL));
            }
        }

        if(level.getBlockState(pos).is(Blocks.WHITE_CONCRETE)) {
            level.setBlockAndUpdate(pos, portalBlock.setValue(DIMENSION_KEY, KEY_HOME));
        }
    }

    private static void uniqueItems(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction) {
        var eliteState = shoppingTableState.setValue(FACING, direction).setValue(TEXTURE, 2);

        if(level.getBlockState(pos).is(Blocks.ORANGE_CONCRETE)){
            level.setBlockAndUpdate(pos.above(), Blocks.BARRIER.defaultBlockState());
            level.setBlockAndUpdate(pos, eliteState);

            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShoppingTableEntity entity) {
                var shoppingItem = getEliteShoppingItem(level);
                entity.setItem(shoppingItem.ShoppingItem());
                entity.setCost(shoppingItem.itemCosts());
            }
        }
    }

    private static void keyTable(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction) {
        var keyState = shoppingTableState.setValue(FACING, direction.getCounterClockWise()).setValue(TEXTURE, 1);

        if(level.getBlockState(pos).is(Blocks.YELLOW_CONCRETE)){
            level.setBlockAndUpdate(pos, keyState);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShoppingTableEntity entity) {
                var itemStack = new ItemStack(ItemReg.LOOT_KEY);
                var value = Random.nextInt(4);
                itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(value));
                entity.setItem(itemStack);

                var cost = switch (value) {
                    case 1 -> ItemCosts.getBronzeCost(40);
                    case 2 -> ItemCosts.getSilverCost(40);
                    case 3 -> ItemCosts.getPlatinumCost(3);
                    default -> ItemCosts.getBronzeCost(20);
                };

                entity.setCost(cost);
            }
        }
    }

    private static void otherShopping(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction) {
        var normalState = shoppingTableState.setValue(FACING, direction.getClockWise());
        var purple = new AtomicInteger();

        if(level.getBlockState(pos).is(Blocks.PURPLE_CONCRETE)){
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, 0));
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                if(purple.get() == 0){
                    entity.setItem(new ItemStack(AUGMENT));
                    entity.setCost(ItemCosts.getGoldCost(20));
                } else {
                    var randomLootItem = new ItemStack(RUNE);
                    RuneHelpers.generateRandomTypAttribute(randomLootItem, null);
                    entity.setItem(randomLootItem);
                    entity.setCost(ItemCosts.getGoldCost(10));
                }
            }
            purple.getAndIncrement();
        }

        if(level.getBlockState(pos).is(Blocks.LIGHT_BLUE_CONCRETE)){
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, 3));
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                entity.setCost(ItemCosts.getGoldCost(1));
            }
        }
    }

}
