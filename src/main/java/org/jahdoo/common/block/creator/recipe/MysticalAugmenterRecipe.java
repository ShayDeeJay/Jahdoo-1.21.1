package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jahdoo.common.block.creator.CreatorEntity;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

public class MysticalAugmenterRecipe implements CreatorRecipes {

    @Override
    public boolean canCraft(List<ItemStack> inputItems, CreatorEntity creator) {
        var chaosCubes = 0;
        var Diamonds = 0;

        for (var inputItem : inputItems) {
            if(inputItem.is(ItemReg.MODULAR_CHAOS_CUBE_ITEM)) chaosCubes++;
            if(inputItem.is(Items.DIAMOND)) Diamonds++;
        }

        return chaosCubes == 4 && Diamonds == 4;
    }

    @Override
    public ItemStack result(CreatorEntity creator) {
        return new ItemStack(ItemReg.MYSTICAL_AUGMENTER_ITEM);
    }

    @Override
    public String recipeId() {
        return "mystical_augmenter";
    }

    @Override
    public int nexiteCost() {
        return 64;
    }

}
