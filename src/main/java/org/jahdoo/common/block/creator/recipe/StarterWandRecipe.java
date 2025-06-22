package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

public class StarterWandRecipe implements CreatorRecipes {

    @Override
    public boolean canCraft(List<ItemStack> inputItems) {
       var totalStick = 0;
       var totalLapis = 0;
        for (var inputItem : inputItems) {
            if(inputItem.is(Items.STICK)) totalStick++;
            if(inputItem.is(Items.LAPIS_LAZULI)) totalLapis++;
        }

        return totalStick == 4 && totalLapis == 1;
    }

    @Override
    public ItemStack result() {
        return new ItemStack(ItemReg.STARTER_WAND);
    }

    @Override
    public String recipeId() {
        return "starter_wand";
    }

    @Override
    public int nexiteCost() {
        return 32;
    }

}
