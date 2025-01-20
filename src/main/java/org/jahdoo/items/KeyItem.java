package org.jahdoo.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

public class KeyItem extends Item implements JahdooItem {

    public KeyItem() {
        super(new Properties());
    }

    @Override
    public Component getName(ItemStack stack) {
        var getId = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(getId == null) return super.getName(stack);

        var getRarity = getJahdooRarity(getId);

        return ModHelpers.withStyleComponent(getRarity.getSerializedName() + " Key", getRarity.getColour());
    }

    public static @NotNull JahdooRarity getJahdooRarity(CustomModelData getId) {
        return switch (getId.value()){
            case 1 -> JahdooRarity.RARE;
            case 2 -> JahdooRarity.LEGENDARY;
            case 3 -> JahdooRarity.ETERNAL;
            default -> JahdooRarity.COMMON;
        };
    }

    @Override
    public Component getDescription() {
        return super.getDescription();
    }

}
