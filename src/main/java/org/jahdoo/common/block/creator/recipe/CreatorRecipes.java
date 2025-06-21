package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface CreatorRecipes {

    int nexiteCost();

    boolean canCraft(List<ItemStack> inputItems);

    ItemStack result();

    String recipeId();

}
