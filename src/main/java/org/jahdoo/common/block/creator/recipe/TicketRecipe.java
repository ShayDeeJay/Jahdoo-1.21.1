package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

public class TicketRecipe implements CreatorRecipes {

    @Override
    public boolean canCraft(List<ItemStack> inputItems) {
       var totalDiamonds = 0;
       var totalPaper = 0;
        for (var inputItem : inputItems) {
            if(inputItem.is(Items.DIAMOND)) totalDiamonds++;
            if(inputItem.is(Items.PAPER)) totalPaper++;
        }

        return totalDiamonds == 4 && totalPaper == 4;
    }

    @Override
    public ItemStack result() {
        return new ItemStack(ItemReg.TRIAL_TICKET);
    }

    @Override
    public String recipeId() {
        return "nexus_ticket";
    }

    @Override
    public int nexiteCost() {
        return 32;
    }

}
