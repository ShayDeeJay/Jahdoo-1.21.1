package org.jahdoo.ascension.level_manager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static net.minecraft.world.level.block.Blocks.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.*;
import static org.jahdoo.ascension.level_manager.StructureManager.*;
import static org.jahdoo.ascension.trading_post.ShoppingItems.getEliteShoppingItem;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.block.TrialPortalBlock.*;
import static org.jahdoo.common.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.common.block.shopping_table.ShoppingTableBlock.TEXTURE;
import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.common.registers.ItemReg.AUGMENT;
import static org.jahdoo.common.registers.ItemReg.RUNE;

public class BlockSetupManager {

    private static void setLootChests(ServerLevel level, BlockPos pos, Direction direction) {
        var chestState = LOOT_CHEST.get().defaultBlockState().setValue(FACING, direction);
        if(level.getBlockState(pos).is(MAGENTA_CONCRETE)){
            level.setBlockAndUpdate(pos, chestState);
        }
    }

    public static void placePerkTables(ServerLevel level, BlockPos blockPos) {
        var blockState = level.getBlockState(blockPos);

        if(blockState.is(WHITE_CONCRETE)) setPerkTable(level, blockPos, 0);
        if(blockState.is(LIGHT_GRAY_CONCRETE)) setPerkTable(level, blockPos, 1);
    }

    public static void setPerkTable(ServerLevel level, BlockPos blockPos, int state) {
        level.setBlockAndUpdate(blockPos, PERK_TABLE.get().defaultBlockState().setValue(TEXTURE, state));
        level.setBlockAndUpdate(blockPos.above(1), BARRIER.defaultBlockState());
    }

    public static void setBlockGenerator(ServerLevel level, Iterable<BlockPos> pos, Direction direction, String id) {
        for (var blockPos : pos) {
            if(Objects.equals(id, SANCTUARY)) placePerkTables(level, blockPos);
            if(id.contains("the") || id.contains("boss")) setLocks(level, blockPos, true);
            if(Objects.equals(id, BAZAAR)){
                var table = SHOPPING_TABLE.get().defaultBlockState();
                setLootChests(level, blockPos, direction);
                uniqueItems(level, table, blockPos, direction);
                otherShopping(level, table, blockPos, direction);
                keyTable(level, table, blockPos, direction);
                setHelperBlocks(level, table, blockPos, direction);
            }
        }
    }

    public static void setLocks(ServerLevel level, BlockPos blockPos, boolean ignoreCheck) {
        var blockState = level.getBlockState(blockPos);
        if(blockState.is(OBSERVER)){
            level.setBlockAndUpdate(blockPos, BlockReg.LOCK.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING)));

            if(ignoreCheck && level.getBlockEntity(blockPos) instanceof LockBlockEntity lockBlockEntity){
                if(!lockBlockEntity.canPlace()){
                    level.setBlockAndUpdate(blockPos, NETHERITE_BLOCK.defaultBlockState());
                    level.setBlockAndUpdate(blockPos.relative(blockState.getValue(FACING), 1), NETHERITE_BLOCK.defaultBlockState());
                }
            }
        }
    }

    //Don't delete as useful for generating exit in entry room
    public static void generateExit(ServerLevel level, BlockPos pos) {
        var portal = TRAIL_PORTAL.get().defaultBlockState().setValue(AXIS, Direction.Axis.Z);
        var state = level.getBlockState(pos);
        if(state.is(WHITE_CONCRETE)) {
            level.setBlockAndUpdate(pos, portal.setValue(DIMENSION_KEY, KEY_HOME));
        }
    }

    private static void uniqueItems(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction) {
        var state = level.getBlockState(pos);
        var orange = state.is(ORANGE_CONCRETE);
        var eliteState = shoppingTableState.setValue(FACING, orange ? direction.getCounterClockWise() : direction.getClockWise()).setValue(TEXTURE, 2);

        if(orange){
            level.setBlockAndUpdate(pos.above(), BARRIER.defaultBlockState());
            level.setBlockAndUpdate(pos, eliteState);

            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShoppingTableEntity entity) {
                var shoppingItem = getEliteShoppingItem(level);
                entity.setItem(shoppingItem.ShoppingItem());
                entity.setCost(shoppingItem.itemCosts());
            }
        }
    }

    private static void setHelperBlocks(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction) {
        var state = level.getBlockState(pos);
        var white = state.is(WHITE_CONCRETE);
        var green = state.is(GREEN_CONCRETE);
        var blue = state.is(BLUE_CONCRETE);

        if(white){
            level.setBlockAndUpdate(pos, RUNE_TABLE.get().defaultBlockState().setValue(FACING, direction.getOpposite()));
        } else if (green){
            level.setBlockAndUpdate(pos, AUGMENT_MODIFICATION_STATION.get().defaultBlockState().setValue(FACING, direction.getClockWise()));
        } else if (blue) {
            level.setBlockAndUpdate(pos, WAND_MANAGER_TABLE.get().defaultBlockState());
        }
    }

    private static void keyTable(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction) {
        var keyState = shoppingTableState.setValue(FACING, direction.getCounterClockWise()).setValue(TEXTURE, 1);

        if(level.getBlockState(pos).is(YELLOW_CONCRETE)){
            level.setBlockAndUpdate(pos, keyState);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShoppingTableEntity entity) {
                var itemStack = new ItemStack(ItemReg.LOOT_KEY);
                var value = Random.nextInt(4);
                itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(value));
                entity.setItem(itemStack);

                var cost = switch (value) {
                    case 1 -> setBronzeCost(40);
                    case 2 -> setSilverCost(40);
                    case 3 -> setPlatinumCost(3);
                    default -> setBronzeCost(20);
                };

                entity.setCost(cost);
            }
        }
    }

    private static void otherShopping(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction) {
        var normalState = shoppingTableState.setValue(FACING, direction.getClockWise());

        if(level.getBlockState(pos).is(PURPLE_CONCRETE)){
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, 0));
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                entity.setItem(new ItemStack(AUGMENT));
                entity.setCost(setGoldCost(20));
            }
        }

        if(level.getBlockState(pos).is(BROWN_CONCRETE)){
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, 0));
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                var randomLootItem = new ItemStack(RUNE);
                RuneHelpers.generateRandomTypAttribute(randomLootItem, null);
                entity.setItem(randomLootItem);
                entity.setCost(setGoldCost(10));
            }
        }

        if(level.getBlockState(pos).is(LIGHT_BLUE_CONCRETE)){
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, 3));
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                entity.setCost(setGoldCost(1));
            }
        }
    }

    public static void blockExitBarrier(Level level, BlockPos pos) {
        var list = new ArrayList<>(Direction.stream().toList());
        list.remove(Direction.DOWN);
        list.remove(Direction.UP);

        for (var blockPos : roomBoundingFromCenter(pos)) {
            if(level.getBlockState(blockPos).is(REDSTONE_BLOCK)){
                var startPlace = blockPos.below(2);
                if(level.getBlockState(startPlace).isAir()){
                    for(int i = 0; i < 5; i++){
                        for (var direction : list) {
                            var blocker = TINTED_GLASS;
                            var relative = startPlace.below(i).relative(direction);
                            var canPlaceHere = new AtomicBoolean(false);

                            level.setBlockAndUpdate(startPlace.below(i), blocker.defaultBlockState());

                            for (var direction1 : list) {
                                var adjacentBlocks = relative.relative(direction1);
                                var adjacentNotAir = !level.getBlockState(adjacentBlocks).isAir();
                                var adjacentNotBlocker = level.getBlockState(adjacentBlocks).is(blocker);
                                if(adjacentNotAir && !adjacentNotBlocker){
                                    canPlaceHere.set(true);
                                }
                            }

                            if(canPlaceHere.get()){
                                level.setBlockAndUpdate(relative, blocker.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }

}
