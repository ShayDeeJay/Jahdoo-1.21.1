package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.creator.CreatorEntity;

import java.util.List;

public interface CreatorRecipes {

    int nexiteCost();

    boolean canCraft(List<ItemStack> inputItems, CreatorEntity creator);

    ItemStack result(CreatorEntity creator);

    String recipeId();

    default boolean secondaryCheck(CreatorEntity creator){
        return true;
    }

}
