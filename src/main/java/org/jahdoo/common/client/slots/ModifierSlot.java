package org.jahdoo.common.client.slots;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.common.block.divine_forge.DivineForgeMenu;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.NotNull;

public class ModifierSlot extends SlotItemHandler {

    int maxStackSize;
    boolean isActive = true;
    private final DivineForgeMenu menu;

    public ModifierSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        DivineForgeMenu menu
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return maxStackSize == 0 ? 64 : maxStackSize;
    }

    @Override
    public boolean isActive() {
        return menu.hideModifierSlot;
    }

    @Override
    public void set(ItemStack stack) {
        super.set(stack);
    }

    @Override
    public boolean isHighlightable() {
        return true;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        var hideModifierSlot = menu.hideModifierSlot;
        System.out.println(hideModifierSlot);
        return itemStack.is(ItemReg.SEAL_OF_CHANGE) && hideModifierSlot;
    }



}