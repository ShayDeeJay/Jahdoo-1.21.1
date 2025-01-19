package org.jahdoo.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.utils.ModHelpers;

public class KeyItem extends Item implements JahdooItem {

    public KeyItem() {
        super(new Properties());
    }

    @Override
    public Component getName(ItemStack stack) {
        var getId = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(getId == null) return super.getName(stack);

        var getRarity = switch (getId.value()){
            case 1 -> JahdooRarity.RARE;
            case 2 -> JahdooRarity.LEGENDARY;
            case 3 -> JahdooRarity.ETERNAL;
            default -> JahdooRarity.COMMON;
        };

        return ModHelpers.withStyleComponent(getRarity.getSerializedName() + " Key", getRarity.getColour());
    }

    @Override
    public Component getDescription() {
        return super.getDescription();
    }

}
