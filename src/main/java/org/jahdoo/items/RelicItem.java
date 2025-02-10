package org.jahdoo.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class RelicItem extends Item implements ICurioItem, JahdooItem{
    public RelicItem(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        ICurioItem.super.curioTick(slotContext, stack);
    }

}
