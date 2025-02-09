package org.jahdoo.challenge.trading_post;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.registers.ItemsRegister;

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

    public static void saveData(CompoundTag compoundTag, ItemCosts itemCosts){
        compoundTag.putInt("type", itemCosts.CurrencyType);
        compoundTag.putInt("cost", itemCosts.value);
    }

    public static ItemCosts loadData(CompoundTag compoundTag){
        return new ItemCosts(compoundTag.getInt("type"), compoundTag.getInt("cost"));
    }



    public static ItemStack getItemStack(int type){
        return switch (type){
            case 1 -> new ItemStack(ItemsRegister.BRONZE_COIN);
            case 2 -> new ItemStack(ItemsRegister.SILVER_COIN);
            case 3 -> new ItemStack(ItemsRegister.GOLD_COIN);
            case 4 -> new ItemStack(ItemsRegister.PLATINUM_COIN);
            default -> ItemStack.EMPTY;
        };
    }
}
