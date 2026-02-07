package org.jahdoo.common.items;

import net.minecraft.world.item.Item;

public class BaseJahdooItem extends Item implements JahdooItem {
    public BaseJahdooItem(Properties properties) {
        super(properties);
    }


    @Override
    public String descriptionId() {
        return "description."+this.getDescriptionId();
    }
}
