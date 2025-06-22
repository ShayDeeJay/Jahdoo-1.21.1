package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.registers.ItemReg;

public class BazaarKeyRecipe extends KeyRecipe {

    @Override
    ItemStack specialItem() {
        return new ItemStack(ItemReg.RUNE);
    }

    @Override
    int keyId() {
        return KeyItem.BAZAAR_KEY;
    }

    @Override
    public String recipeId() {
        return "bazaar_key";
    }

}
