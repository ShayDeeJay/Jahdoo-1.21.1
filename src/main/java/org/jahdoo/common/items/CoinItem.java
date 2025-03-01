package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CoinItem extends Item {

    public CoinItem() {
        super(new Properties());
    }

    @Override
    public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(data == null) return Component.literal("Bronze Coin");

        return Component.literal(
            switch (data.value()){
                case 1 -> "Silver";
                case 2 -> "Gold";
                default -> "Platinum";
            } + " Coin"
        );
    }

}
