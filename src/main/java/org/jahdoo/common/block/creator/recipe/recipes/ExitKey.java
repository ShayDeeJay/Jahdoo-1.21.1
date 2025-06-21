package org.jahdoo.common.block.creator.recipe.recipes;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.registers.ItemReg;

public class ExitKey extends KeyRecipe {

    @Override
    ItemStack specialItem() {
        return new ItemStack(ItemReg.ENCHANTED_DIAMOND);
    }

    @Override
    int keyId() {
        return KeyItem.EXIT_KEY;
    }

    @Override
    public String recipeId() {
        return "exit_key";
    }

}
