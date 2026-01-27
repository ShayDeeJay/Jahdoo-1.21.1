package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.common.event.event_helpers.EventHelpers;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

import static org.jahdoo.common.event.event_helpers.EventHelpers.getOverEnchantColour;
import static org.jahdoo.trial_nexus.utils.ColourStore.CHAMPION_GOLD;
import static org.jahdoo.trial_nexus.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.trial_nexus.utils.Helpers.highlightTextComponent;
import static org.jahdoo.trial_nexus.utils.Maths.roundNonWholeString;
import static org.jahdoo.trial_nexus.utils.Maths.singleFormattedDouble;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.*;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;

public interface JahdooItem {

    default ItemStack getRecycleItem(){
        return new ItemStack(ItemReg.ESSENCE_FRAGMENT);
    };

    default int customRecycleChance(ItemStack itemStack){
        return -1;
    }

    default void appendItemToolTips(ItemStack stack, Item.TooltipContext context, List<Component> toolTips, boolean addSpacer){
        addRarity(stack, context, toolTips);
        appendPotentialComponent(toolTips, stack);
        toolTips.add(appendDurability(stack));
        appendRepairSlotsComponent(toolTips, stack);
        if(addSpacer) toolTips.add(Component.literal(" "));
    }

    default void brokenGearMessage(List<Component> toolTips, ItemStack gearItem){
        toolTips.add(Helpers.withStyleComponentTrans("❌Broken❌", ColourStore.RATING_2_RED));
    }

    default boolean isItemBroken(ItemStack gearItem){
        return Helpers.durabilityDamageCount(gearItem) == 0;
    }

    default void appendRepairSlotsComponent(List<Component> toolTips, ItemStack gearItem) {
        getRepairSlotsComponent(gearItem, (r) -> toolTips.add(toolTips.size(), r));
    }

    default void appendPotentialComponent(List<Component> toolTips, ItemStack gearItem){
        var wandData = gearItem.get(JAHDOO_GEAR_DATA);
        if(wandData == null) return;

        getPotentialComponent(gearItem, (s) -> toolTips.add(toolTips.size(), s));
    }

    default void addRarity(ItemStack stack, Item.TooltipContext context, List<Component> toolTips) {
        var rarity = JahdooRarity.attachRarityTooltip(stack, context.level());
        if(rarity != null) toolTips.add(rarity);
    }

    default void appendWeaponToolTip(ItemStack stack, Item.TooltipContext context, List<Component> toolTips){
        toolTips.add(Component.literal(" "));
        toolTips.add(Helpers.withStyleComponent("When Equipped", ColourStore.SUB_HEADER_COLOUR));
        for (var modifier : stack.getAttributeModifiers().modifiers()) {
            var value = roundNonWholeString(singleFormattedDouble(modifier.modifier().amount()));
            var getColour = ColourStore.ABSORPTION_TEXT_YELLOW;

            if(modifier.attribute() == Attributes.ENTITY_INTERACTION_RANGE){
                toolTips.add(Helpers.withStyleComponent("+"+value+" Attack Range", getColour));
            }

            if(modifier.attribute() == Attributes.ATTACK_DAMAGE){
                toolTips.add(Helpers.withStyleComponentTrans("+"+value+" Attack Damage", getColour));
            }

            if(modifier.attribute() == Attributes.ATTACK_SPEED){
                var value1 = modifier.attribute().value().getDefaultValue();
                var amount = modifier.modifier().amount();
                var v = roundNonWholeString(singleFormattedDouble(value1 + amount));
                toolTips.add(Helpers.withStyleComponent(v+" Attack Speed", getColour));
            }
        }
        runeSpacer(stack, toolTips);
    }

    default void enchantmentTooltip(ItemStack stack, List<Component> tooltipComponents, boolean addSpacer, Level level) {
        var itemEnchantments = stack.get(DataComponents.ENCHANTMENTS);
        if(itemEnchantments != null && !itemEnchantments.entrySet().isEmpty()){
            if(addSpacer) tooltipComponents.add(Component.literal(" "));
            tooltipComponents.add(Helpers.withStyleComponent("Enchantments", ColourStore.SUB_HEADER_COLOUR));
            for (var holderEntry : itemEnchantments.entrySet()) {
                var value = holderEntry.getKey().value();
                var string = value.description().getString();
                var s = JahdooRarity.romanNumeralConverter(holderEntry.getIntValue() - 1);
                if(!EventHelpers.isOverEnchanted(holderEntry.getKey(), holderEntry.getIntValue())){
                    tooltipComponents.add(Helpers.withStyleComponent(string + " " + s, ColourStore.NETHERITE_BOX));
                } else {
                    var recoloured = Helpers.withStyleComponent(string + " " + s, getOverEnchantColour(level));
                    tooltipComponents.add(recoloured);
                }
            }
        }
    }

    default void runeSpacer(ItemStack stack, List<Component> tooltipComponents) {
        var runeHolder = JahdooGearData.getGearData(stack);
        if(runeHolder != null && !runeHolder.runeSlots().isEmpty()){
            tooltipComponents.add(Component.literal(" "));
        }
    }

    default void standAloneModifiersWithLabel(
        ItemStack stack,
        List<Component> toolTip,
        Item.TooltipContext context,
        String label,
        int colourA,
        int colourB,
        double speed,
        double duration,
        boolean addSpace
    ) {
        var list = stack.getAttributeModifiers().modifiers().stream()
            .filter(e -> e.modifier().id().getPath().contains("bonus") || e.modifier().id().getPath().contains("boon"))
            .toList();
        if(list.isEmpty()) return;
        if(addSpace) toolTip.add(Component.literal(" "));
        var comp = highlightTextComponent(context.level(), label, colourA, colourB, speed, duration);
        toolTip.add(comp);

        for (var entry : list) toolTip.add(RuneHelpers.standAloneAttributes(entry));
    }

    default void bonusModifierTooltip(ItemStack stack, List<Component> toolTip, Item.TooltipContext context, boolean addSpace) {
        var text = "Bonus Modifiers";
        standAloneModifiersWithLabel(stack, toolTip, context, text, HEADER_COLOUR, CHAMPION_GOLD, 1.5, 20, addSpace);
    }

}
