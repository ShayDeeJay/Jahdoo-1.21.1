package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;

import java.util.List;

import static org.jahdoo.ascension.utils.Maths.roundNonWholeString;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.*;

public interface JahdooItem {

    default void appendItemToolTips(ItemStack stack, Item.TooltipContext context, List<Component> toolTips, boolean addSpacer){
        if(addSpacer) toolTips.add(Component.literal(" "));

        var rarity = JahdooRarity.attachRarityTooltip(stack, context.level());
        if(rarity != null) toolTips.add(rarity);

        appendPotentialComponent(toolTips, stack);
        toolTips.add(appendDurability(stack));
        appendRepairSlotsComponent(toolTips, stack);
    }

    default void appendWeaponToolTip(ItemStack stack, Item.TooltipContext context, List<Component> toolTips){
        toolTips.add(Component.literal(" "));
        toolTips.add(Helpers.withStyleComponent("When Equipped", ColourStore.SUB_HEADER_COLOUR));
        for (var modifier : stack.getAttributeModifiers().modifiers()) {
            var value = roundNonWholeString(singleFormattedDouble(modifier.modifier().amount()));
            var getColour = ColourStore.ABSORPTION_TEXT_YELLOW;
            if(modifier.attribute() == Attributes.ATTACK_DAMAGE){
                toolTips.add(Helpers.withStyleComponent("+"+value+" Attack Damage", getColour));
            }

            if(modifier.attribute() == Attributes.ATTACK_SPEED){
                var value1 = modifier.attribute().value().getDefaultValue();
                var amount = modifier.modifier().amount();
                var v = roundNonWholeString(singleFormattedDouble(value1 + amount));
                toolTips.add(Helpers.withStyleComponent(v+" Attack Speed", getColour));
            }
        }
    }

}
