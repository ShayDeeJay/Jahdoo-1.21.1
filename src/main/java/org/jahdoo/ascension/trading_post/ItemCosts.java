package org.jahdoo.ascension.trading_post;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.ItemReg;

public record ItemCosts(int CurrencyType, int value){

    public static final int BRONZE_COIN = 1;
    public static final int SILVER_COIN = 2;
    public static final int GOLD_COIN = 3;
    public static final int PLATINUM_COIN = 4;
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
        return switch (type){
            case 1 -> new ItemStack(ItemReg.BRONZE_COIN);
            case 2 -> new ItemStack(ItemReg.SILVER_COIN);
            case 3 -> new ItemStack(ItemReg.GOLD_COIN);
            case 4 -> new ItemStack(ItemReg.PLATINUM_COIN);
            default -> ItemStack.EMPTY;
        };
    }

}
