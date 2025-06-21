package org.jahdoo.common.block.creator.recipe.recipes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.creator.recipe.CreatorRecipes;

import java.util.List;

public abstract class SingleItemRecipe implements CreatorRecipes {

    abstract Item item();

    abstract int quantity();

    @Override
    public boolean canCraft(List<ItemStack> inputItems) {
        if(inputItems.size() != quantity()) return false;
        for (var inputItem : inputItems) {
            if(!inputItem.is(item())) return false;
        }
        return true;
    }

}
