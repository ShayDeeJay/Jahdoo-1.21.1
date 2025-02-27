package org.jahdoo.common.client.gui.slots;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.registers.AbilityRegister;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.ItemsRegister;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class ModularChaosCubeSlot extends SlotItemHandler {

    public ModularChaosCubeSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        var abilityName = DataComponentHelper.getAbilityTypeItemStack(itemStack);
        AtomicBoolean isValid = new AtomicBoolean(false);
        AbilityRegister.getFirstSpellByTypeId(abilityName).ifPresent(
            ability -> isValid.set(itemStack.is(ItemsRegister.AUGMENT_ITEM.get()) && ability.getElemenType() == ElementRegistry.utility())
        );
        return isValid.get();
    }

    @Override
    public boolean isHighlightable() {
        return true;
    }
}