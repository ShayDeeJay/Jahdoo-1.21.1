package org.jahdoo.ascension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.trading_post.ItemCosts;
import org.jahdoo.common.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.items.runes.rune_data.RuneData;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;

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

    private static void setLootChests(ServerLevel level) {
        var chestState = LOOT_CHEST.get().defaultBlockState().setValue(FACING, Direction.SOUTH);
        var chestPositions = new BlockPos(-21, 55, -25);

        for(int i = 0; i <= 4; i += 2) level.setBlockAndUpdate(chestPositions.west(i), chestState);
    }

    static void setTrialDim(ServerLevel level, ChallengeLevelData data) {
        var pos = new BlockPos(-25, 67, -72);
        level.setBlockAndUpdate(pos, BlockReg.CHALLENGE_ALTAR.get().defaultBlockState());
        if(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altar){
            altar.setData(CHALLENGE_ALTAR, data);
            altar.setChanged();
        }
    }

    static void generateTradingPost(ServerLevel level) {
        var shoppingTableState = SHOPPING_TABLE.get().defaultBlockState();

        setLootChests(level);
        uniqueItems(level, shoppingTableState);
        otherShopping(level, shoppingTableState);
        keyTable(level, shoppingTableState);
        generateEntranceAndExit(level);
    }

    private static void generateEntranceAndExit(ServerLevel level) {
        //Entrance and exit portals
        var portalBlock = TRAIL_PORTAL.get().defaultBlockState();
        var portalExit = new BlockPos(-4, 51, -17);
        var portalNextLevel = new BlockPos(-40, 51, -17);

        for(int i = 0; i < 3; i++) {
            var pos = portalExit.west(i);
            var pos1 = portalNextLevel.west(i);
            for(int x = 0; x < 5; x++){
                level.setBlockAndUpdate(pos.above(x), portalBlock.setValue(DIMENSION_KEY, KEY_HOME));
                level.setBlockAndUpdate(pos1.above(x), portalBlock.setValue(DIMENSION_KEY, KEY_TRIAL));
            }
        }
    }

    private static void uniqueItems(ServerLevel level, BlockState shoppingTableState) {
        var eliteItemPosition = new BlockPos(-16, 54, -15);
        var eliteState = shoppingTableState.setValue(FACING, Direction.NORTH).setValue(TEXTURE, 2);

        for(int i = 0; i <= 14; i += 7){
            var pos = eliteItemPosition.west(i);
            level.setBlockAndUpdate(pos.above(), Blocks.BARRIER.defaultBlockState());
            level.setBlockAndUpdate(pos, eliteState);
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                var shoppingItem = getEliteShoppingItem(level);
                entity.setItem(shoppingItem.ShoppingItem());
                entity.setCost(shoppingItem.itemCosts());
            }
        }
    }

    private static void keyTable(ServerLevel level, BlockState shoppingTableState) {
        var keyItemPosition = new BlockPos(-32, 53, -28);
        var keyState = shoppingTableState.setValue(FACING, Direction.EAST).setValue(TEXTURE, 1);

        for(int i = 6; i >= 0; i -= 3) {
            var pos = keyItemPosition.south(i);
            level.setBlockAndUpdate(pos, keyState);
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                var itemStack = new ItemStack(ItemReg.LOOT_KEY);
                var value = Random.nextInt(4);
                itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(value));
                entity.setItem(itemStack);

                var cost = switch (value){
                    case 1 -> ItemCosts.getBronzeCost(40);
                    case 2 -> ItemCosts.getSilverCost(40);
                    case 3 -> ItemCosts.getPlatinumCost(3);
                    default -> ItemCosts.getBronzeCost(20);
                };

                entity.setCost(cost);
            }
        }
    }

    private static void otherShopping(ServerLevel level, BlockState shoppingTableState) {
        var normalItemPosition = new BlockPos(-14, 53, -28);
        var normalState = shoppingTableState.setValue(FACING, Direction.WEST);

        for(int i = 0; i <= 6; i += 3) {
            var isRandomTable = i == 0;
            var pos = normalItemPosition.south(i);
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, isRandomTable ? 3 : 0));
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                switch (i){
                    case 0 -> entity.setCost(ItemCosts.getGoldCost(1));
                    case 3 -> {
                        entity.setItem(new ItemStack(AUGMENT));
                        entity.setCost(ItemCosts.getGoldCost(20));
                    }
                    case 6 -> {
                        var randomLootItem = new ItemStack(RUNE);
                        RuneData.RuneHelpers.generateRandomTypAttribute(randomLootItem, null);
                        entity.setItem(randomLootItem);
                        entity.setCost(ItemCosts.getGoldCost(10));
                    }
                }
            }
        }
    }

}
