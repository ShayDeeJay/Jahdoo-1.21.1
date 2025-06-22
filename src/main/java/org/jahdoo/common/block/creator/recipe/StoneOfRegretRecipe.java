package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

public class StoneOfRegretRecipe implements CreatorRecipes {

    @Override
    public boolean canCraft(List<ItemStack> inputItems) {
        var core = 0;
        var eye = 0;

        for (var inputItem : inputItems) {
            if(inputItem.is(ItemReg.ADVANCED_AUGMENT_CORE) && CoreData.isFull(inputItem)) core++;
            if(inputItem.is(Items.ENDER_EYE)) eye++;
        }

        return core == 4 && eye == 4;
    }

    @Override
    public ItemStack result() {
        return new ItemStack(ItemReg.STONE_OF_REGRET);
    }

    @Override
    public String recipeId() {
        return "stone_of_regret";
    }

    @Override
    public int nexiteCost() {
        return 64;
    }

}
