package org.jahdoo.common.items.tome;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.RelicItem;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;

public class TomeOfUnity extends RelicItem implements JahdooItem {

    public TomeOfUnity() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        var name = super.getName(stack).getString();
        return Helpers.withStyleComponent(name, ColourStore.SUB_HEADER_COLOUR);
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        return super.getSlotsTooltip(tooltips, context, stack);
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var list = new ArrayList<Component>();

        var lists = stack.getAttributeModifiers().modifiers().stream().toList();
        if(!lists.isEmpty()){
            for (var entry : lists) {
                tooltips.add(RuneHelpers.standAloneAttributes(entry));
            }
        }
        return list;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltips, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltips, tooltipFlag);
        this.appendItemToolTips(stack, context, tooltips, false);
        tooltips.add(Component.empty());

        var list = stack.getAttributeModifiers().modifiers().stream().toList();
        if(!list.isEmpty()){
            for (var entry : list) {
                tooltips.add(RuneHelpers.standAloneAttributes(entry));
            }
        }
    }

}
