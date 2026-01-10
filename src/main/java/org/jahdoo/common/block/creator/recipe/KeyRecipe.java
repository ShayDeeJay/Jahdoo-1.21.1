package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.block.creator.CreatorEntity;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.common.items.KeyItem.KEY_PIECE;

public abstract class KeyRecipe implements CreatorRecipes {

    abstract ItemStack specialItem();

    abstract int keyId();

    @Override
    public boolean canCraft(List<ItemStack> inputItems, CreatorEntity creator) {
//        if(!inputItems.stream().map(ItemStack::getItem).toList().contains(specialItem().getItem())) return false;
        var count = 0;
        var specialItem = 0;
        for (var inputItem : inputItems) {
            if(inputItem.is(ItemReg.LOOT_KEY)){
                var cData = inputItem.get(CUSTOM_MODEL_DATA);
                if(cData != null && cData.value() == KEY_PIECE) count++;
            }

            if(inputItem.is(specialItem().getItem())) specialItem++;
        }
        return count == 3 && specialItem == 1;
    }

    @Override
    public ItemStack result(CreatorEntity creator) {
        var resultItem = new ItemStack(ItemReg.LOOT_KEY);
        resultItem.set(CUSTOM_MODEL_DATA, new CustomModelData(keyId()));
        return resultItem;
    }

    @Override
    public int nexiteCost() {
        return 32;
    }

}
