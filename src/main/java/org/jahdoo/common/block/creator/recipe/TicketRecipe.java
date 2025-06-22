package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;

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
        var resultItem = new ItemStack(ItemReg.TRIAL_TICKET);
        resultItem.set(CUSTOM_MODEL_DATA, new CustomModelData(1));
        return resultItem;
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
