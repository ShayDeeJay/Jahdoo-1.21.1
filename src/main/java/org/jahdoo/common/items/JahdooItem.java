package org.jahdoo.common.items;

import kotlin.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jahdoo.common.event.event_helpers.EventHelpers;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.ItemHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jahdoo.common.event.event_helpers.EventHelpers.getOverEnchantColour;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.*;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.highlightTextComponent;

public interface JahdooItem {


    default String descriptionId(ItemStack stack){
        return null;
    }

    @Nullable
    default Pair<String, List<ItemStack>> getAdditional(){
        return null;
    }

    default ItemStack getRecycleItem(){
        return new ItemStack(ItemReg.ESSENCE_FRAGMENT);
    }

    default int customRecycleChance(ItemStack itemStack){
        return -1;
    }

    static void addItems(int i1, ItemStack itemStack, ArrayList<ItemStack> getItems) {
        for (int i = 0; i < i1; i++){
            getItems.add(itemStack);
        }
    }

    default void appendItemToolTips(ItemStack stack, Item.TooltipContext context, List<Component> toolTips, boolean addSpacer){
        addRarity(stack, context, toolTips);
        appendPotentialComponent(toolTips, stack);
        toolTips.add(appendDurability(stack));
        appendRepairSlotsComponent(toolTips, stack);
        if(addSpacer) toolTips.add(Component.literal(" "));
    }

    default void brokenGearMessage(List<Component> toolTips, ItemStack gearItem){
        toolTips.add(TextHelpers.withStyleComponentTrans("❌Broken❌", ColourHelpers.getRating2Red()));
    }

    default boolean isItemBroken(ItemStack gearItem){
        return ItemHelpers.durabilityDamageCount(gearItem) == 0;
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
        toolTips.add(TextHelpers.withStyleComponent("When Equipped", ColourHelpers.getSubHeaderColour()));
        for (var modifier : stack.getAttributeModifiers().modifiers()) {
            var value = MathHelpers.roundNonWholeString(MathHelpers.singleFormattedDouble(modifier.modifier().amount()));
            var getColour = ColourHelpers.getAbsorptionTextYellow();

            if(modifier.attribute() == Attributes.ENTITY_INTERACTION_RANGE){
                toolTips.add(TextHelpers.withStyleComponent("+"+value+" Attack Range", getColour));
            }

            if(modifier.attribute() == Attributes.ATTACK_DAMAGE){
                toolTips.add(TextHelpers.withStyleComponentTrans("+"+value+" Attack Damage", getColour));
            }

            if(modifier.attribute() == Attributes.ATTACK_SPEED){
                var value1 = modifier.attribute().value().getDefaultValue();
                var amount = modifier.modifier().amount();
                var v = MathHelpers.roundNonWholeString(MathHelpers.singleFormattedDouble(value1 + amount));
                toolTips.add(TextHelpers.withStyleComponent(v+" Attack Speed", getColour));
            }
        }
        runeSpacer(stack, toolTips);
    }

    default void enchantmentTooltip(ItemStack stack, List<Component> tooltipComponents, boolean addSpacer, Level level) {
        var itemEnchantments = stack.get(DataComponents.ENCHANTMENTS);
        tooltipWithoutType(tooltipComponents, addSpacer, true, level, itemEnchantments);
    }

    static void tooltipWithoutType(List<Component> tooltipComponents, boolean addSpacer, boolean showHeader, Level level, ItemEnchantments itemEnchantments) {
        if(itemEnchantments != null && !itemEnchantments.entrySet().isEmpty()){

            if(addSpacer) tooltipComponents.add(Component.literal(" "));
            if(showHeader) tooltipComponents.add(TextHelpers.withStyleComponentTrans("info.jahdoo.enchantments", ColourHelpers.getSubHeaderColour()));

            for (var holderEntry : itemEnchantments.entrySet()) {
                var value = holderEntry.getKey().value();
                var string = value.description().getString();
                var numeric = JahdooRarity.romanNumeralConverter(holderEntry.getIntValue() - 1);

                if(!EventHelpers.isOverEnchanted(holderEntry.getKey(), holderEntry.getIntValue())){
                    tooltipComponents.add(TextHelpers.withStyleComponent(string + " " + numeric, ColourHelpers.getNetheriteBox()));
                } else {
                    var recoloured = TextHelpers.withStyleComponent(string + " " + numeric, getOverEnchantColour(level));
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
        boolean addSpace,
        String...filters
    ) {
        var list = stack.getAttributeModifiers().modifiers().stream()
            .filter(e -> Arrays.stream(filters).anyMatch(filter -> e.modifier().id().getPath().contains(filter)))
            .toList();

        if(list.isEmpty()) return;
        if(addSpace) toolTip.add(Component.literal(" "));
        var comp = highlightTextComponent(context.level(), label, colourA, colourB, speed, duration);
        toolTip.add(comp);

        for (var entry : list) toolTip.add(RuneHelpers.standAloneAttributes(entry));
    }

    default void bonusModifierTooltip(ItemStack stack, List<Component> toolTip, Item.TooltipContext context, boolean addSpace) {
        var text = "Bonus Modifiers";
        standAloneModifiersWithLabel(stack, toolTip, context, text, ColourHelpers.getHeaderColour(), ColourHelpers.getChampionGold(), 1.5, 20, addSpace, "boon", "bonus");

        var text2 = "Elder Effect Enhancers";
        standAloneModifiersWithLabel(stack, toolTip, context, text2, ColourHelpers.getHeaderColour(), ColourHelpers.getOffWhite(), 1, 10, addSpace, "effect");

        var text3 = "Skill Amplifiers";
        standAloneModifiersWithLabel(stack, toolTip, context, text3, ColourHelpers.getHeaderColour(), ColourHelpers.getChampionGold(), 1.5, 20, addSpace, "skill");
    }

}
