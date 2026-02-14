package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.creator.CreatorEntity;
import org.jahdoo.common.items.BaseJahdooItem;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

public class DeedRecipe implements CreatorRecipes {

    @Override
    public boolean canCraft(List<ItemStack> inputItems, CreatorEntity creator) {
        var chargedCore = 0;
        var lisite = 0;
        var grass = 0;

        var x = (BaseJahdooItem) ItemReg.DEED.get();



        return false;
    }

    @Override
    public ItemStack result(CreatorEntity creator) {
        return new ItemStack(ItemReg.DEED);
    }

    @Override
    public String recipeId() {
        return "deed_item";
    }

    @Override
    public int nexiteCost() {
        return 64;
    }

}
