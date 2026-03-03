package org.jahdoo.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BaseJahdooItem extends Item implements JahdooItem {
    public BaseJahdooItem(Properties properties) {
        super(properties);
    }

    @Override
    public String descriptionId(ItemStack stack) {
        return "description."+this.getDescriptionId();
    }
}
