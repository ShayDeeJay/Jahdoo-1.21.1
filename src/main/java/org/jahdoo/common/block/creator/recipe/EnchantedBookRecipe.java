package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jahdoo.common.block.creator.CreatorEntity;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.utils.EnchantmentHelpers;

import java.util.List;

public class EnchantedBookRecipe implements CreatorRecipes {

    @Override
    public boolean canCraft(List<ItemStack> inputItems, CreatorEntity creator) {
        var enchantedBook = ItemStack.EMPTY;
        var enchantable = ItemStack.EMPTY;
        var xp = ItemStack.EMPTY;

        for (var inputItem : inputItems) {
            if(inputItem.is(Items.ENCHANTED_BOOK)){
                enchantedBook = inputItem;
            } else if(inputItem.is(ItemReg.RADIANT_EXPERIENCE_ORB)){
                xp = new ItemStack(ItemReg.RADIANT_EXPERIENCE_ORB);
            } else {
                enchantable = inputItem;
            }
        }

        return EnchantmentHelpers.applyBookEnchantsToItem(enchantedBook.copy(), enchantable.copy(), true) && xp != ItemStack.EMPTY;
    }

    @Override
    public ItemStack result(CreatorEntity creator) {
        var getBook = creator.getAllCraftables().get(0).copy();
        var secondaryItem = creator.getAllCraftables().get(1).copy();

        for (var allCraftable : creator.getAllCraftables()) {
            if(!allCraftable.is(ItemReg.RADIANT_EXPERIENCE_ORB)){
                if (allCraftable.is(Items.ENCHANTED_BOOK)) {
                    getBook = allCraftable;
                } else {
                    secondaryItem = allCraftable;
                }
            }

        }

        EnchantmentHelpers.applyBookEnchantsToItem(getBook, secondaryItem, false);
        return secondaryItem;
    }

    @Override
    public String recipeId() {
        return "enchanting_books";
    }

    @Override
    public int nexiteCost() {
        return 64;
    }

}
