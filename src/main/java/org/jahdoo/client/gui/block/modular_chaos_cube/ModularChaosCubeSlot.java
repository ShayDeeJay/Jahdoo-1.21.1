package org.jahdoo.client.gui.block.modular_chaos_cube;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
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
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        var abilityName = DataComponentHelper.getAbilityTypeItemStack(itemStack);
        AtomicBoolean isValid = new AtomicBoolean(false);
        AbilityRegister.getFirstSpellByTypeId(abilityName).ifPresent(
            ability -> isValid.set(itemStack.is(ItemsRegister.AUGMENT_ITEM.get()) && ability.getElemenType() == ElementRegistry.UTILITY.get())
        );
        return isValid.get();
    }



    @Override
    public boolean isHighlightable() {
        return true;
    }
}