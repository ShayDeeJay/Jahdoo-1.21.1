package org.jahdoo.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class RelicItem extends Item implements ICurioItem, JahdooItem {

    public RelicItem(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        ICurioItem.super.curioTick(slotContext, stack);
    }

    @Override
    public void curioBreak(SlotContext slotContext, ItemStack stack) {
        ICurioItem.super.curioBreak(slotContext, stack);
    }

    @Override
    public boolean canSync(SlotContext slotContext, ItemStack stack) {


        return ICurioItem.super.canSync(slotContext, stack);
    }



}
