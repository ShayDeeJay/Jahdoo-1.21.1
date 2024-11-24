package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class InventorySlots extends Slot {
    boolean isActive = true;

    public InventorySlots(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isActive;
    }

}