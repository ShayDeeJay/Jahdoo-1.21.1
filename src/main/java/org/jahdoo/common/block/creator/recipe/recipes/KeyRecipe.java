package org.jahdoo.common.block.creator.recipe.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.block.creator.recipe.CreatorRecipes;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.common.items.KeyItem.KEY_PIECE;

public abstract class KeyRecipe implements CreatorRecipes {

    abstract ItemStack specialItem();

    abstract int keyId();

    @Override
    public boolean canCraft(List<ItemStack> inputItems) {
        if(!inputItems.stream().map(ItemStack::getItem).toList().contains(specialItem().getItem())) return false;
        var count = 0;
        for (var inputItem : inputItems) {
            if(inputItem.is(ItemReg.LOOT_KEY)){
                var cData = inputItem.get(CUSTOM_MODEL_DATA);
                if(cData != null && cData.value() == KEY_PIECE) count++;
            }
        }
        return count == 4;
    }

    @Override
    public ItemStack result() {
        var resultItem = new ItemStack(ItemReg.LOOT_KEY);
        resultItem.set(CUSTOM_MODEL_DATA, new CustomModelData(keyId()));
        return resultItem;
    }

    @Override
    public int nexiteCost() {
        return 32;
    }

}
