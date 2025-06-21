package org.jahdoo.common.block.creator.recipe.recipes;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.registers.ItemReg;

public class SanctuaryKey extends KeyRecipe {

    @Override
    ItemStack specialItem() {
        return new ItemStack(ItemReg.AUGMENT_CORE);
    }

    @Override
    int keyId() {
        return KeyItem.SANCTUARY_KEY;
    }

    @Override
    public String recipeId() {
        return "sanctuary_key";
    }

}
