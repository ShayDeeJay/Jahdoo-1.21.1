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

    public static Component getConverted(ItemStack stack){
        var value = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(value == null) return TextHelpers.withStyleComponentTrans("item.jahdoo.seal_of_change", -1);

        return switch (value.value()){
            case 2 -> TextHelpers.withStyleComponentTrans("item.jahdoo.seal_of_rejuvenation", -1);
            case 3 -> TextHelpers.withStyleComponentTrans("item.jahdoo.seal_of_reinforcement", -1);
            default -> TextHelpers.withStyleComponentTrans("item.jahdoo.seal_of_repair", -1);
        };
    }
}
