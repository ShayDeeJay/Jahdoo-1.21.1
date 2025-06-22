package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.registers.ItemReg;

public class CryptKeyRecipe extends KeyRecipe {

    @Override
    ItemStack specialItem() {
        return new ItemStack(ItemReg.ROSE_QUARTZ);
    }

    @Override
    int keyId() {
        return KeyItem.CRYPT_KEY;
    }

    @Override
    public String recipeId() {
        return "crypt_key";
    }

}
