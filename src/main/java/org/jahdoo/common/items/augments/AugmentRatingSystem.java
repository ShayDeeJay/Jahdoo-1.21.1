package org.jahdoo.common.items.augments;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.ItemReg;

import java.text.DecimalFormat;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.items.augments.AugmentItemHelper.getModifierContextRange;

public class AugmentRatingSystem {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public static String rangeString(Object min, Object max){
        return  min + "-" + max;
    }

    public static double convertToPercentage(double max) {
        if (max == 0) return 100.0;
        if (max <= 0) return 0;
        return Double.parseDouble(FORMAT.format((1.0 / max) * 100));
    }

    public static AbilityData.AbilityModifiers getModifier(AbilityHolder holder, String keys){
       return holder.data().abilityProperties().get(keys);
    }

    public static Component additionalInformation(AbilityHolder holder, String keys, boolean isHigherBetter){
        return Component.literal(" (")
            .append(hoverTextHelper(holder, keys, isHigherBetter))
            .append(")")
            .withStyle(ChatFormatting.DARK_GRAY);
    }

    public static Component hoverTextHelper(AbilityHolder holder, String keys, boolean isHigherBetter) {
        if (holder != null) {
            var modifiers = getModifier(holder, keys);
            var max = FORMAT.format(modifiers.highestValue());
            var min = FORMAT.format(modifiers.lowestValue());
            var getLowest = isHigherBetter ? min : max;
            var getHighest = isHigherBetter ? max : min;

            return getModifierContextRange(keys, getLowest, getHighest);
        }
        return Component.empty();
    }

    public static Item calculateRatingNext(AbilityData.AbilityModifiers mod) {
        double normalizedValue = getNormalizedValue(mod);
        var rating = (int)(normalizedValue * 4) + 1;
        var core = ItemReg.AUGMENT_CORE.get();
        var advanced = ItemReg.ADVANCED_AUGMENT_CORE.get();
        var hyperCore = ItemReg.AUGMENT_HYPER_CORE.get();

        if(rating == 1) return core;
        if(rating == 2 || rating == 3) return advanced;
        return hyperCore;
    }

    public static int calculateRating(AbilityData.AbilityModifiers mod) {
        if(mod == null) return -1;

        boolean higherIsBetter = mod.isHigherBetter();

        double value = mod.actualValue();
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
        return (int)(normalizedValue * 4) + 1; // Map to 1-5 rating
    }

    private static double getNormalizedValue(AbilityData.AbilityModifiers mod) {
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

    public static Component displayRating(AbilityHolder abilityHolder, String keys) {
         int getRating;
        int chatFormatting;
        boolean isHigherBetter = true;

        if(abilityHolder != null){
            var modifier = getModifier(abilityHolder, keys);
            getRating = calculateRating(modifier);
            isHigherBetter = modifier.isHigherBetter();

            switch (getRating){
                case 1 -> chatFormatting = color(255, 211,211,211);
                case 2 -> chatFormatting = color(255, 230,71,71);
                case 3 -> chatFormatting = color(255, 224,156,59);
                case 4 -> chatFormatting = color(255, 230,226,46);
                default -> chatFormatting= color(255, 143,185,53);
            }
        } else {
            getRating = 0;
            chatFormatting = -1;
        }

        return Component.literal("▊".repeat(Math.max(1, getRating)))
            .withStyle(style -> style.withColor(chatFormatting))
            .append(additionalInformation(abilityHolder, keys, isHigherBetter));
    }

}
