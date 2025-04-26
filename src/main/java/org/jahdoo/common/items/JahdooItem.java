package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.rarity.JahdooRarity;

import java.util.List;

import static org.jahdoo.common.items.caster_item.CasterItemHelper.*;

public interface JahdooItem {

    default void appendItemToolTips(ItemStack stack, Item.TooltipContext context, List<Component> toolTips, boolean addSpacer){
        if(addSpacer) toolTips.add(Component.empty());

        var rarity = JahdooRarity.attachRarityTooltip(stack, context.level());
        if(rarity != null) toolTips.add(rarity);

        appendPotentialComponent(toolTips, stack);
        toolTips.add(appendDurability(stack));
        appendRepairSlotsComponent(toolTips, stack);
    }

    default void appendWeaponToolTip(ItemStack stack, Item.TooltipContext context, List<Component> toolTips){
        var rarity = JahdooRarity.attachRarityTooltip(stack, context.level());
        if(rarity != null)toolTips.add(rarity);
        appendPotentialComponent(toolTips, stack);
        appendDurability(stack);
    }

}
