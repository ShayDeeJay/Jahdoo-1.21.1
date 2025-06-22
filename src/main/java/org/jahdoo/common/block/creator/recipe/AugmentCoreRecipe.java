package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.ItemReg;

public class AugmentCoreRecipe extends SingleItemRecipe{

    @Override
    Item item() {
        return ItemReg.ESSENCE_FRAGMENT.get();
    }

    @Override
    int quantity() {
        return 8;
    }

    @Override
    public int nexiteCost() {
        return 12;
    }

    @Override
    public ItemStack result() {
        return new ItemStack(ItemReg.AUGMENT_CORE);
    }

    @Override
    public String recipeId() {
        return "augment_core";
    }

}
