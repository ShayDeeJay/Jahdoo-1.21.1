package org.jahdoo.common.client.slots;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.common.components.CoreData;
import org.jetbrains.annotations.NotNull;

public class GeneralItemSlot extends SlotItemHandler {

    Item item;
    int maxStackSize;
    boolean isActive = true;

    public GeneralItemSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        Item item
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.item = item;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return maxStackSize == 0 ? 64 : maxStackSize;
    }

    public Item getSlotType(){
        return this.item;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public void set(ItemStack stack) {
        super.set(stack);
    }

    @Override
    public boolean isHighlightable() {
        return this.isActive && !this.getItem().isEmpty();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        var isCorrectCore = this.item != null && itemStack.is(this.item);
        var isFullCore = CoreData.isFull(itemStack);
        return (isCorrectCore) && isActive && isFullCore;
    }

}