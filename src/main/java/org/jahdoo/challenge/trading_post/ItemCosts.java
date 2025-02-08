package org.jahdoo.challenge.trading_post;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.registers.ItemsRegister;

public record ItemCosts(ItemStack CurrencyType, int value){

    public static ItemCosts getBronzeCost(int value){
        var coin = ItemsRegister.BRONZE_COIN.get();
        return new ItemCosts(new ItemStack(coin), value);
    }

    public static ItemCosts getSilverCost(int value){
        var coin = ItemsRegister.SILVER_COIN.get();
        return new ItemCosts(new ItemStack(coin), value);
    }

    public static ItemCosts getGoldCost(int value){
        var coin = ItemsRegister.GOLD_COIN.get();
        return new ItemCosts(new ItemStack(coin), value);
    }

    public static ItemCosts getPlatinumCost(int value){
        var coin = ItemsRegister.PLATINUM_COIN.get();
        return new ItemCosts(new ItemStack(coin), value);
    }
}
