package org.jahdoo.items.augments;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.registers.ItemsRegister;

import java.text.DecimalFormat;

import static org.jahdoo.items.augments.AugmentItemHelper.getModifierContextRange;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;

public class AugmentRatingSystem {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public static Component hoverTextHelper(ItemStack itemStack, String keys, String abilityLocation, boolean isHigherBetter) {
        if (itemStack.has(WAND_ABILITY_HOLDER.get())) {
            var modifiers = getModifier(itemStack, abilityLocation, keys);
            var max = FORMAT.format(modifiers.highestValue());
            var min = FORMAT.format(modifiers.lowestValue());
            var getLowest = isHigherBetter ? min : max;
            var getHighest = isHigherBetter ? max : min;

            return getModifierContextRange(keys, getLowest, getHighest);
        }
        return Component.empty();
    }

    public static String rangeString(Object min, Object max){
        return  min + "-" + max;
    }

    public static Component additionalInformation(ItemStack itemStack, String keys, String abilityLocation, boolean isHigherBetter){
        return Component.literal(" (")
            .append(hoverTextHelper(itemStack, keys, abilityLocation, isHigherBetter))
            .append(")")
            .withStyle(ChatFormatting.DARK_GRAY);
    }

    public static double convertToPercentage(double max) {
        if (max <= 0) return 0;
        return Double.parseDouble(FORMAT.format((1.0 / max) * 100));
    }

    public static int calculateRating(AbilityHolder.AbilityModifiers aMod) {

        boolean higherIsBetter = aMod.isHigherBetter();

        double value = aMod.actualValue();
        double minValue = aMod.lowestValue();
        double maxValue = aMod.highestValue();

        double range = maxValue - minValue;
        double relativeValue = value - minValue;

        double normalizedValue;

        if (higherIsBetter) {
            normalizedValue = relativeValue / range;
        } else {
            normalizedValue = 1 - (relativeValue / range);
        }

        normalizedValue = Math.max(0, Math.min(1, normalizedValue));
        return (int)(normalizedValue * 4) + 1; // Map to 1-5 rating
    }

    public static Item calculateRatingNext(AbilityHolder.AbilityModifiers aMod) {
        double normalizedValue = getNormalizedValue(aMod);
        var rating = (int)(normalizedValue * 4) + 1;
        var core = ItemsRegister.AUGMENT_CORE.get();
        var advanced = ItemsRegister.ADVANCED_AUGMENT_CORE.get();
        var hyperCore = ItemsRegister.AUGMENT_HYPER_CORE.get();

        if(rating == 1) return core;
        if(rating == 2 || rating == 3) return advanced;
        return hyperCore;
    }

    private static double getNormalizedValue(AbilityHolder.AbilityModifiers mod) {
        boolean higherIsBetter = mod.isHigherBetter();

        double value = higherIsBetter ? mod.actualValue() + mod.step() : mod.actualValue() - mod.step();
        double minValue = mod.lowestValue();
        double maxValue = mod.highestValue();

        double range = maxValue - minValue;
        double relativeValue = value - minValue;

        double normalizedValue;

        if (higherIsBetter) {
            normalizedValue = relativeValue / range;
        } else {
            normalizedValue = 1 - (relativeValue / range);
        }

        normalizedValue = Math.max(0, Math.min(1, normalizedValue));
        return normalizedValue;
    }

    public static AbilityHolder.AbilityModifiers getModifier(ItemStack itemStack, String abilityLocation, String keys){
        var holder = itemStack.get(WAND_ABILITY_HOLDER.get());
        var ability = holder.abilityProperties().get(abilityLocation);
        return ability.abilityProperties().get(keys);
    }

    public static Component displayRating(ItemStack itemStack, String keys, String abilityLocation) {
         int getRating;
        int chatFormatting;
        boolean isHigherBetter = true;

        if(itemStack.has(WAND_ABILITY_HOLDER.get())){
            var modifier = getModifier(itemStack, abilityLocation, keys);
            getRating = calculateRating(modifier);
            isHigherBetter = modifier.isHigherBetter();

            switch (getRating){
                case 1 -> chatFormatting = FastColor.ARGB32.color(255, 211,211,211);
                case 2 -> chatFormatting = FastColor.ABGR32.color(255, 230,71,71);
                case 3 -> chatFormatting = FastColor.ABGR32.color(255, 224,156,59);
                case 4 -> chatFormatting = FastColor.ABGR32.color(255, 230,226,46);
                default -> chatFormatting= FastColor.ABGR32.color(255, 143,185,53);
            }
        } else {
            getRating = 0;
            chatFormatting = -1;
        }

        return Component.literal("▊".repeat(Math.max(1, getRating)))
            .withStyle(style -> style.withColor(chatFormatting))
            .append(additionalInformation(itemStack, keys, abilityLocation, isHigherBetter));
    }

}
