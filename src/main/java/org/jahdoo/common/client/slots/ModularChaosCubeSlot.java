package org.jahdoo.common.client.slots;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.registers.AbilityRegister;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.jahdoo.common.registers.ElementRegistry.*;
import static org.jahdoo.common.registers.ItemsRegister.*;

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
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isHighlightable() {
        return true;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        var abilityName = DataComponentHelper.getAbilityTypeItemStack(itemStack);
        var isValid = new AtomicBoolean(false);

        AbilityRegister.getFirstSpellByTypeId(abilityName)
            .ifPresent(ability -> isValid.set(itemStack.is(AUGMENT.get()) && ability.getElemenType() == utility()));

        return isValid.get();
    }
}