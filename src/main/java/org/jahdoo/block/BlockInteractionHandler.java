package org.jahdoo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

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

//        if (!itemStack.isEmpty() && itemStack.is(item)) {
//            if (inputSlot.isEmpty() || itemStack.is(inputSlot.getItem())) {
//                int remainingSpace = inputSlot.getMaxStackSize() - inputSlot.getCount();
//                if (remainingSpace > 0) {
//                    int amountToAdd = Math.min(remainingSpace, itemStack.getCount());
//
//                    ItemStack itemStackCopy = itemStack.copyWithCount(amountToAdd);
//
//                    if (!player.getAbilities().instabuild) {
//                        itemStack.shrink(amountToAdd);
//                    }
//                    itemHandler.setStackInSlot(inputSlotNumber, itemStackCopy);
////                    itemHandler.insertItem(inputSlotNumber, itemStackCopy, false);
//                    return true;
//                }
//            }
//        }
        return false;
    }

    public static boolean removeItemsFromHandToSlot(
        ItemStackHandler itemStackHandler,
        int outputSlot,
        Player player,
        int itemCount
    ){
        ItemStack mainHandItems = player.getMainHandItem();
        if(mainHandItems.isEmpty()) return false;
        itemStackHandler.setStackInSlot(outputSlot, mainHandItems.copyWithCount(itemCount));
        if(!player.isCreative()) mainHandItems.shrink(itemCount);
        return true;
    }

    public static boolean removeItemsFromSlotToHand(
        ItemStackHandler itemStackHandler,
        int outputSlot,
        Player player,
        InteractionHand interactionHand
    ) {
        ItemStack outputSlotTotal = itemStackHandler.getStackInSlot(outputSlot);
        Inventory playInventory = player.getInventory();
        if(outputSlotTotal.isEmpty()) return false;

        // Removes all items from slot to empty hand
        if (player.getMainHandItem().isEmpty()) {
            player.setItemInHand(interactionHand, itemStackHandler.extractItem(outputSlot, outputSlotTotal.getCount(), false));
            return true;
        } else if(player.getMainHandItem().is(outputSlotTotal.getItem()) &&  player.getMainHandItem().getCount() < 64) {
            player.getMainHandItem().grow(outputSlotTotal.getCount() - player.getMainHandItem().getCount());
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
            if (player.getMainHandItem().isEmpty() && player.isShiftKeyDown()) {
                player.setItemInHand(interactionHand, outputSlotTotal);
                itemStackHandler.extractItem(outputSlot, outputSlotTotal.getCount(), false);
                level.playLocalSound(blockPos, soundEvents, SoundSource.NEUTRAL, volume, pitch, false);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

}
