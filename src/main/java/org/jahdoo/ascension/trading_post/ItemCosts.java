package org.jahdoo.ascension.trading_post;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.registers.ItemReg;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;

public record ItemCosts(int CurrencyType, int value){

    public static final int BRONZE_COIN = 0;
    public static final int SILVER_COIN = 1;
    public static final int GOLD_COIN = 2;
    public static final int PLATINUM_COIN = 3;
    public static final ItemCosts EMPTY_COST = new ItemCosts(0, 0);

    public static ItemCosts getBronzeCost(int value){
        return new ItemCosts(BRONZE_COIN, value);
    }

    public static ItemCosts getSilverCost(int value){;
        return new ItemCosts(SILVER_COIN, value);
    }

    public static ItemCosts getGoldCost(int value){
        return new ItemCosts(GOLD_COIN, value);
    }

    public static ItemCosts getPlatinumCost(int value){
        return new ItemCosts(PLATINUM_COIN, value);
    }

    public static ItemCosts loadData(CompoundTag tag){
        return new ItemCosts(tag.getInt("type"), tag.getInt("cost"));
    }

    public static void saveData(CompoundTag compoundTag, ItemCosts itemCosts){
        compoundTag.putInt("type", itemCosts.CurrencyType);
        compoundTag.putInt("cost", itemCosts.value);
    }

    public static ItemStack getItemStack(int type){
        var itemStack = new ItemStack(ItemReg.COIN);
        if(type == 0) return itemStack;

        itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(type));
        return itemStack;
    }

}
