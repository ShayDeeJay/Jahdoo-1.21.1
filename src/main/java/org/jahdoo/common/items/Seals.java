package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

public class Seals extends BaseJahdooItem {

    public Seals() {
        super(new Properties());
    }

    @Override
    public Component getName(ItemStack stack) {
        return getConverted(stack);
    }

    public static Component getConverted(ItemStack stack) {
        return TextHelpers.withStyleComponentTrans(getSealTranslationKey(stack), -1);
    }

    private static String getSealTranslationKey(ItemStack stack) {
        var value = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if (value == null) {
            return "item.jahdoo.seal_of_extension";
        }

        return switch (value.value()) {
            case 2 -> "item.jahdoo.seal_of_power";
            case 3 -> "item.jahdoo.seal_of_defence";
            default -> "item.jahdoo.seal_of_repair";
        };
    }

    @Override
    public String descriptionId(ItemStack stack) {
        return "description."+getSealTranslationKey(stack);
    }
}
