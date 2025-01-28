package org.jahdoo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jahdoo.items.augments.AugmentItemHelper;

import java.util.Collections;
import java.util.Optional;

public class BlockInteractionHandler {

    public static boolean stackHandler(
        ItemStackHandler itemHandler,
        ItemStack itemStack,
        Item item,
        int inputSlotNumber,
        Player player
    ) {
        ItemStack inputSlot = itemHandler.getStackInSlot(inputSlotNumber);

        if (!itemStack.isEmpty() && itemStack.is(item)) {
            if (inputSlot.isEmpty() || itemStack.is(inputSlot.getItem())) {
                int remainingSpace = inputSlot.getMaxStackSize() - inputSlot.getCount();
                if (remainingSpace > 0) {
                    int amountToAdd = Math.min(remainingSpace, itemStack.getCount());
                    ItemStack itemStackCopy = itemStack.copyWithCount(amountToAdd);

                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(amountToAdd);
                    }

                    itemHandler.insertItem(inputSlotNumber, itemStackCopy, false);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean stackHandlerWithFeedBack(
        ItemStackHandler itemHandler,
        ItemStack itemStack,
        Item item,
        int inputSlotNumber,
        int maxSize,
        Player player
    ) {
        ItemStack inputSlot = itemHandler.getStackInSlot(inputSlotNumber);


        if (!itemStack.isEmpty() && itemStack.is(item)) {
            if (inputSlot.isEmpty() || itemStack.is(inputSlot.getItem())) {
                int remainingSpace = maxSize - inputSlot.getCount();
                if (remainingSpace > 0) {
                    int amountToAdd = Math.min(remainingSpace, itemStack.getCount());
                    ItemStack itemStackCopy = itemStack.copyWithCount(amountToAdd);
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(amountToAdd);
                    }
                    itemHandler.insertItem(inputSlotNumber, itemStackCopy, false);
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean removeItemsFromHandToSlot(
        ItemStackHandler itemStackHandler,
        int outputSlot,
        Player player,
        int itemCount
    ){
        ItemStack mainHandItems = player.getItemInHand(player.getUsedItemHand());
        if(mainHandItems.isEmpty()) return false;
        itemStackHandler.setStackInSlot(outputSlot, mainHandItems.copyWithCount(itemCount));
        /*if(!player.isCreative())*/ mainHandItems.shrink(itemCount);
        return true;
    }

    public static void swapItemsWithHand(
        ItemStackHandler itemStackHandler,
        int outputSlot,
        Player player,
        InteractionHand hand
    ){
        var count = 1;
        var playerItem = player.getItemInHand(hand);
        var inventoryItem = itemStackHandler.getStackInSlot(outputSlot);
        itemStackHandler.setStackInSlot(outputSlot, playerItem.copyWithCount(count));
        if(playerItem.getCount() > 1){
            AugmentItemHelper.throwOrAddItem(player, inventoryItem.copyWithCount(count));
            playerItem.shrink(1);
        } else {
            player.setItemInHand(hand, inventoryItem.copyWithCount(count));
        }
    }

    public static boolean removeItemsFromSlotToHand(
        ItemStackHandler itemStackHandler,
        int outputSlot,
        Player player,
        InteractionHand interactionHand
    ) {
        var outputSlotTotal = itemStackHandler.getStackInSlot(outputSlot);
        var playInventory = player.getInventory();
        if(outputSlotTotal.isEmpty()) return false;

        // Removes all items from slot to empty hand
        var mainHandItem = player.getItemInHand(player.getUsedItemHand());
        if (mainHandItem.isEmpty()) {
            player.setItemInHand(interactionHand, itemStackHandler.extractItem(outputSlot, outputSlotTotal.getCount(), false));
            return true;
        } else if(mainHandItem.is(outputSlotTotal.getItem()) &&  mainHandItem.getCount() < 64) {
            mainHandItem.grow(outputSlotTotal.getCount() - mainHandItem.getCount());
            return true;
        }

        return false;

    }

    public static InteractionResult RemoveItemsFromSlotToHand(
        ItemStackHandler itemStackHandler,
        int outputSlot,
        Player player,
        InteractionHand interactionHand,
        Level level,
        BlockPos blockPos,
        SoundEvent soundEvents,
        float volume,
        float pitch
    ) {
        ItemStack outputSlotTotal = itemStackHandler.getStackInSlot(outputSlot);

        if(!itemStackHandler.getStackInSlot(outputSlot).isEmpty()){
            if (player.getItemInHand(player.getUsedItemHand()).isEmpty() && player.isShiftKeyDown()) {
                player.setItemInHand(interactionHand, outputSlotTotal);
                itemStackHandler.extractItem(outputSlot, outputSlotTotal.getCount(), false);
                level.playLocalSound(blockPos, soundEvents, SoundSource.NEUTRAL, volume, pitch, false);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }


    public static Optional<Pair<IItemHandler, Object>> getItemHandlerAt(Level worldIn, double x, double y, double z, Direction side) {
        var blockpos = BlockPos.containing(x, y, z);
        var state = worldIn.getBlockState(blockpos);
        var blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(blockpos) : null;
        var blockCap = worldIn.getCapability(Capabilities.ItemHandler.BLOCK, blockpos, state, blockEntity, side);
        if (blockCap != null) {
            return Optional.of(ImmutablePair.of(blockCap, blockEntity));
        } else {
            var list = worldIn.getEntities((Entity)null, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.ENTITY_STILL_ALIVE);
            if (!list.isEmpty()) {
                Collections.shuffle(list);
                for (Entity entity : list) {
                    IItemHandler entityCap = entity.getCapability(Capabilities.ItemHandler.ENTITY_AUTOMATION, side);
                    if (entityCap != null) {
                        return Optional.of(ImmutablePair.of(entityCap, entity));
                    }
                }
            }
            return Optional.empty();
        }
    }
}
