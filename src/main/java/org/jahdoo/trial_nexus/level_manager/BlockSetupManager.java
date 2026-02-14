package org.jahdoo.trial_nexus.level_manager;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.block.loot_chest.LootChestEntity;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.entities.custom_entities.CustomVillager;
import org.jahdoo.common.items.perk_soda.PerkaSoda;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.shaydee.shaydeeapi.block.SyncedBlockEntity;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static net.minecraft.world.level.block.Blocks.*;
import static org.jahdoo.common.block.TrialPortalBlock.*;
import static org.jahdoo.common.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.common.block.perk_table.PerkTable.HALF;
import static org.jahdoo.common.block.shopping_table.ShoppingTableBlock.TEXTURE;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter.*;
import static org.jahdoo.trial_nexus.level_manager.RoomData.*;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.EXIT_BARRIER;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.roomBoundingFromCenter;
import static org.jahdoo.trial_nexus.trading_post.ShoppingItems.getEliteShoppingItem;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class BlockSetupManager {

    public static void setAttributePerkTable(ServerLevel level, BlockPos pos, int roomId){
        setPerkTable(level, pos, 3);
        if(level.getBlockEntity(pos) instanceof SyncedBlockEntity syncedBlockEntity){
            syncedBlockEntity.setSaveInt(roomId);
        }
    }

    public static void setCoinLootChest(ServerLevel level, BlockPos pos, Direction direction, int type, boolean ignore, int roomId){
        setLootChests(level, pos, direction, type, ignore);
        if(level.getBlockEntity(pos) instanceof SyncedBlockEntity syncedBlockEntity){
            syncedBlockEntity.setSaveInt(roomId);
        }
    }

    public static void setCryptCoinChest(ServerLevel level, BlockPos pos, Direction direction, int type, boolean ignore) {
        var chestState = LOOT_CHEST.get().defaultBlockState().setValue(FACING, direction.getCounterClockWise());
        if(ignore || level.getBlockState(pos).is(YELLOW_CONCRETE)){
            level.setBlockAndUpdate(pos, chestState);
            if(level.getBlockEntity(pos) instanceof LootChestEntity l){
                l.setShowHover(true);
                l.getRarity = type;
                l.setData(INSTANCE_DATA, InstanceData.copyInstance(level.getData(INSTANCE_DATA)));
            }
        }

        var chestState1 = LOOT_CHEST.get().defaultBlockState().setValue(FACING, direction.getClockWise());
        if(ignore || level.getBlockState(pos).is(PINK_CONCRETE)){
            level.setBlockAndUpdate(pos, chestState1);
            if(level.getBlockEntity(pos) instanceof LootChestEntity l){
                l.setShowHover(true);
                l.getRarity = type;
                l.setData(INSTANCE_DATA, InstanceData.copyInstance(level.getData(INSTANCE_DATA)));
            }
        }

        var chestState2 = LOOT_CHEST.get().defaultBlockState().setValue(FACING, direction);
        if(ignore || level.getBlockState(pos).is(GREEN_CONCRETE)){
            level.setBlockAndUpdate(pos, chestState2);
            if(level.getBlockEntity(pos) instanceof LootChestEntity l){
                l.setShowHover(true);
                l.getRarity = type;
                l.setData(INSTANCE_DATA, InstanceData.copyInstance(level.getData(INSTANCE_DATA)));
            }
        }
    }

    public static void setLootChests(ServerLevel level, BlockPos pos, Direction direction, int type, boolean ignore) {
        var chestState = LOOT_CHEST.get().defaultBlockState().setValue(FACING, direction);
        if(ignore || level.getBlockState(pos).is(WHITE_CONCRETE)){
            level.setBlockAndUpdate(pos, chestState);
            if(level.getBlockEntity(pos) instanceof LootChestEntity l){
                l.getRarity = type;
                l.setData(INSTANCE_DATA, InstanceData.copyInstance(level.getData(INSTANCE_DATA)));
            }
        }
    }

    public static void setPerkTable(ServerLevel level, BlockPos blockPos, int state) {
        level.setBlockAndUpdate(blockPos, PERK_TABLE.get().defaultBlockState().setValue(TEXTURE, state).setValue(HALF, DoubleBlockHalf.LOWER));
//        level.setBlockAndUpdate(blockPos.above(), PERK_TABLE.get().defaultBlockState().setValue(TEXTURE, state).setValue(HALF, DoubleBlockHalf.LOWER));

        //        level.setBlockAndUpdate(blockPos.above(1), BARRIER.defaultBlockState());
    }

    public static void placePerkTables(ServerLevel level, BlockPos blockPos) {
        var blockState = level.getBlockState(blockPos);

        if(blockState.is(WHITE_CONCRETE)) setPerkTable(level, blockPos, 0);
        if(blockState.is(LIGHT_GRAY_CONCRETE)) setPerkTable(level, blockPos, 1);
    }

    public static void setBlockGenerator(ServerLevel level, Iterable<BlockPos> pos, Direction direction, String id) {
        var counter = new AtomicInteger();
        for (var blockPos : pos) {
            if(CHALLENGER_DOME.isRoom(id)) setLocks(level, blockPos, true);
            if(EXIT.isRoom(id)) generateExit(level, blockPos, direction.getOpposite());
            if(SANCTUARY.isRoom(id)) placePerkTables(level, blockPos);
            if(CRYPT.isRoom(id)){
                setLootChests(level, blockPos, direction, Random.nextInt(4), false);
                setCryptCoinChest(level, blockPos, direction, -1, false);
            }
            if(BAZAAR.isRoom(id)){
                var table = SHOPPING_TABLE.get().defaultBlockState();
                uniqueItems(level, table, blockPos, direction);
                otherShopping(level, table, blockPos, direction);
                keyTable(level, table, blockPos, direction);
                setHelperBlocks(level, table, blockPos, direction);
                starterPack(level, table, blockPos, direction, counter);
            }
        }
    }

    public static void setLocks(ServerLevel level, BlockPos blockPos, boolean ignoreCheck) {
        var blockState = level.getBlockState(blockPos);
        if(blockState.is(OBSERVER)){
            var getState = LOCK.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING));
            level.setBlockAndUpdate(blockPos, getState);

            if(ignoreCheck && level.getBlockEntity(blockPos) instanceof LockBlockEntity lEntity){
                if(!lEntity.canPlace()){
                    level.setBlockAndUpdate(blockPos, LOCK_SUPPORT.get().defaultBlockState());
                    var relative = blockPos.relative(blockState.getValue(FACING), 1);
                    if(level.getBlockEntity(relative) instanceof LockBlockEntity lockBlockEntity1) {
                        if(!lockBlockEntity1.clicked){
                            level.setBlockAndUpdate(relative, LOCK_SUPPORT.get().defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    //Don't delete as useful for generating exit in entry room
    public static void generateExit(ServerLevel level, BlockPos pos, Direction direction) {
        var portal = TRAIL_PORTAL.get().defaultBlockState().setValue(AXIS, direction.getClockWise().getAxis());
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
            level.setBlockAndUpdate(pos, RUNE_TABLE.get().defaultBlockState().setValue(FACING, direction.getCounterClockWise()));
        } else if (green){
            level.setBlockAndUpdate(pos, WAND_MANAGER_TABLE.get().defaultBlockState());
        }
    }

    private static void starterPack(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction, AtomicInteger counter) {
        var keyState = shoppingTableState.setValue(FACING, direction.getCounterClockWise()).setValue(TEXTURE, 1);

        if(level.getBlockState(pos).is(RED_CONCRETE)){
            level.setBlockAndUpdate(pos, keyState);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShoppingTableEntity entity) {
                var itemStack = new ItemStack(ItemReg.CARE_PACKAGE);
                if(counter.get() != 0){
                    itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(counter.get()));
                }
                entity.setItem(itemStack);

                var cost = switch (counter.get()) {
                    case 1 -> setSilverCost(50);
                    case 2 -> setGoldCost(3);
                    default -> setBronzeCost(20);
                };

                entity.setCost(cost);
            }
            counter.incrementAndGet();
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
                    case 1 -> setSilverCost(2);
                    case 2 -> setGoldCost(2);
                    case 3 -> setPlatinumCost(2);
                    default -> setBronzeCost(50);
                };

                entity.setCost(cost);
            }
        }
    }

    private static void otherShopping(ServerLevel level, BlockState shoppingTableState, BlockPos pos, Direction direction) {
        var normalState = shoppingTableState.setValue(FACING, direction.getClockWise());

        if (level.getBlockState(pos).is(PURPLE_CONCRETE)) {
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, 0));
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShoppingTableEntity entity) {
                var item = new ItemStack(ItemReg.PERKA_SODA);
                PerkaSoda.addPerk(item);
                entity.setItem(item);
                entity.setCost(setBronzeCost(50));
            }
        }

        if (level.getBlockState(pos).is(LIME_CONCRETE)) {
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, 0));
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShoppingTableEntity entity) {
                var item = new ItemStack(ItemReg.PERKA_SODA);
                PerkaSoda.addPerk(item);
                entity.setItem(item);
                entity.setCost(setBronzeCost(50));
            }
        }

        if (level.getBlockState(pos).is(LIGHT_BLUE_CONCRETE)) {
            var direction1 = direction.getClockWise();
            var profession = VillagerProfession.LIBRARIAN;

            spawnShopOwner(level, pos, direction, profession, direction1);
        }

        if (level.getBlockState(pos).is(BLACK_CONCRETE)) {
            var direction1 = direction.getClockWise().getOpposite();
            var profession = VillagerProfession.CLERIC;

            spawnShopOwner(level, pos, direction, profession, direction1);
        }

        if (level.getBlockState(pos).is(LIGHT_GRAY_CONCRETE)) {
            var direction1 = direction.getOpposite();
            var profession = VillagerProfession.ARMORER;

            spawnShopOwner(level, pos, direction, profession, direction1);
        }
    }

    private static void spawnShopOwner(ServerLevel level, BlockPos pos, Direction direction, VillagerProfession profession, Direction direction1) {
        level.setBlockAndUpdate(pos, AIR.defaultBlockState());
        var villager = new CustomVillager(level);
        level.addFreshEntity(villager);
        villager.removeFreeWill();
        villager.setVillagerData(new VillagerData(VillagerType.PLAINS, profession, 1));
        villager.moveTo(pos.getCenter(), direction.toYRot(), 0);
        villager.lookAt(EntityAnchorArgument.Anchor.EYES, pos.relative(direction1, 1).getCenter());
    }

    public static void blockExitBarrier(Level level, BlockPos pos) {
        var list = new ArrayList<>(Direction.stream().toList());
        list.remove(Direction.DOWN);
        list.remove(Direction.UP);
        var startPlace = new BlockPos(0,0,0);

        for (var blockPos : roomBoundingFromCenter(pos)) {
            if(level.getBlockState(blockPos).is(REDSTONE_BLOCK)){
                startPlace = blockPos.below(2);
                if(level.getBlockState(startPlace).isAir()){
                    for(int i = 0; i < 5; i++){
                        for (var direction : list) {
                            var newPos = startPlace.below(i);
                            var relative = newPos.relative(direction);

                            level.setBlockAndUpdate(newPos, EXIT_BARRIER);

                            for (var direction1 : list) {
                                var adjacentBlocks = relative.relative(direction1);
                                var adjacentNotAir = level.getBlockState(adjacentBlocks).is(LOCK_SUPPORT);
                                if(adjacentNotAir){
                                    level.setBlockAndUpdate(relative, EXIT_BARRIER);
                                }
                            }
                        }
                    }

                }
            }
        }

        SoundHelpers.getSoundWithPosition(level, startPlace, EXIT_BARRIER.getSoundType(level, pos, null).getBreakSound());
        SoundHelpers.getSoundWithPosition(level, startPlace, SoundReg.UNLOCK.get(), SoundSource.BLOCKS, 1F, 1.8F);
    }

}
