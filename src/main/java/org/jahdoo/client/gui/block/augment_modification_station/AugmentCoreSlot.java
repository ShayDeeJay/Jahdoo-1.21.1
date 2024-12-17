package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class AugmentCoreSlot extends SlotItemHandler {
    Item item;

    public AugmentCoreSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        Item item
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.item = item;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 64;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return itemStack.is(this.item);
    }


}