package org.jahdoo.trial_nexus.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;

import static org.jahdoo.trial_nexus.ability.AbilityComponentHelper.getModifierContextRange;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Maths.FORMAT;

public class AbilityRating {


    public static String rangeString(Object min, Object max){
        return  min + "-" + max;
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
        return (int)(normalizedValue * 4) + 1;
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
                case 1 -> chatFormatting = RATING_1_GRAY;
                case 2 -> chatFormatting = RATING_2_RED;
                case 3 -> chatFormatting = RATING_3_ORANGE;
                case 4 -> chatFormatting = RATING_4_YELLOW;
                default -> chatFormatting = RATING_5_GREEN;
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
