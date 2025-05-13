package org.jahdoo.common.items.tome;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.items.BaseItem;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class TomeOfUnity extends BaseItem implements ICurioItem, JahdooItem {

    public TomeOfUnity() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Helpers.withStyleComponent(super.getName(stack).getString(), ColourStore.PERK_GREEN);
    }

    @Override
    public void implicitModifiers(ItemStack stack, List<Component> toolTips) {
        super.implicitModifiers(stack, toolTips);
        var list = stack.getAttributeModifiers().modifiers().stream().toList();
        if(!list.isEmpty()){
            for (var entry : list) toolTips.add(RuneHelpers.standAloneAttributes(entry));
        }
    }

}
