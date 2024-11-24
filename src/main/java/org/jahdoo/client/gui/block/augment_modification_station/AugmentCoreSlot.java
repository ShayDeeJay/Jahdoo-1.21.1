package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.client.gui.block.wand_block.WandBlockMenu;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.ModTags;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

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