package org.jahdoo.ascension.ability;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.client.screens.AugmentScreen;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.AbilityReg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.ability.AbilityRating.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Maths.*;
import static org.jahdoo.common.registers.ComponentReg.ABILITY_HOLDER;

public class AbilityComponentHelper {

    public static void toolTipBase(
        List<Component> toolTips,
        Ability registrar,
        AbilityHolder holder,
        ItemStack itemStack1,
        String keys,
        int colour,
        boolean hide
    ){
        var component = getCurrentModifierRating(registrar, holder, itemStack1, keys);
        var modifier = getModifier(holder, keys);
        if(modifier == null) return;

        if(colour == 0){
            toolTips.add(component);
        } else {
            toolTips.add(component.copy().withStyle(style -> style.withColor(colour)));
        }

        if(modifier.highestValue() != -1){
            if (hide) toolTips.add(displayRating(holder, keys));
        }
    }

    public static Component getModifierContextSingle(String keys, String current, int getComparison){
        return getModifierContext(keys, current, getComparison, false, "", "");
    }

    public static Component getModifierContextRange(String keys, String min, String max){
        return getModifierContext(keys, "", 0, true, min, max);
    }

    public static Component getFormattedModifiers(String keys, Ability ability, String format, int comparison){
        var element = ability.getElemenType();

        return Component.literal(keys)
            .withStyle(style -> style.withColor(element.partColourB()))
            .append(Component.literal(" | ")
                .withStyle(ChatFormatting.GRAY)
                .append(getModifierContextSingle(keys, format, comparison)));
    }

    public static boolean shiftForDetails(List<Component> toolTips, boolean spacer){
        if(!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 73)){
            if(spacer) toolTips.add(Component.literal(" "));
            var hotkey = Helpers.withStyleComponentTrans("augmentHelper.jahdoo.hotkey", OFF_WHITE);
            var holdToDiscover = Helpers.withStyleComponentTrans("augmentHelper.jahdoo.hold_details", HEADER_COLOUR, hotkey);
            toolTips.add(holdToDiscover);
            return true;
        }
        return false;
    }

    public static List<Component> shiftForDetails(boolean showSpacer, boolean canUpgrade){
        var comps = new ArrayList<Component>();

        if(!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 73)){
            if(showSpacer) comps.add(Component.literal(" "));
            var hotkey = Helpers.withStyleComponentTrans("augmentHelper.jahdoo.hotkey", OFF_WHITE);
            var holdToDiscover = Helpers.withStyleComponentTrans("augmentHelper.jahdoo.hold_details",HEADER_COLOUR, hotkey);
            comps.add(holdToDiscover);

            if(showSpacer){
                if(!canUpgrade){
                    var component = withStyleComponent("[Click] ", OFF_WHITE);
                    var sibling = withStyleComponent("To Upgrade", HEADER_COLOUR);
                    comps.add(component.copy().append(sibling));
                }

                var component = withStyleComponent("[Shift + Click] ", OFF_WHITE);
                var sibling = withStyleComponent("To Add", HEADER_COLOUR);
                comps.add(component.copy().append(sibling));
            }

            return comps;
        }

        return comps;
    }

    public static Component getAbilityName(AbilityHolder holder){
        if(holder != null){
            var ability = AbilityReg.getFirstSpellByTypeId(holder.abilityName());
            if (ability.isPresent()) {
                var getAbility = ability.get();
                return withStyleComponent(getAbility.getAbilityName(), getAbility.getElemenType().textColourA());
            }
        }

        return Component.empty();
    }

    public static Screen getAugmentModificationScreenWand(Player player, Screen previousScreen) {
        var data = player.getData(AttachmentReg.CASTER_DATA);
        var ability = AbilityReg.getFirstSpellByTypeId(data.getSelectedAbility());
        if(ability.isPresent()){
            if (isConfigAbility(player)) return new AugmentScreen(player, previousScreen, ability.get());
        }
        return null;
    }

    public static Optional<String> isValidAugmentUtil(ItemStack itemStack) {
        var itemStacks = itemStack.get(ComponentReg.ABILITY_HOLDER.get());
        if(itemStacks == null) return Optional.empty();

        var typeId = itemStacks.abilityName();
        var ability = AbilityReg.getFirstSpellByTypeId(typeId);
        if(ability.isPresent()){
            if (isConfigAbility(ability.get(), typeId, itemStack)) {
                return Optional.of(typeId);
            }
        }
        return Optional.empty();
    }

    public static boolean isConfigAbility(Ability selectedAbility, String ability, ItemStack itemStack) {
        var wandAbilityHolder = itemStack.get(ABILITY_HOLDER);
        if(wandAbilityHolder == null) return false;
        var filterOutBase = wandAbilityHolder.data().abilityProperties()
            .keySet()
            .stream()
            .filter(name -> !name.equals(MANA_COST) && !name.equals(COOLDOWN));
        return /*selectedAbility.getElemenType() == ElementRegistry.UTILITY.get() && */!filterOutBase.toList().isEmpty();
//        return selectedAbility.getElemenType() == ElementRegistry.UTILITY.get() && !filterOutBase.toList().isEmpty();
    }

    public static boolean isConfigAbility(Player player) {
        var filterOutBase = CasterData.entityHolderWithSelected(player).data().abilityProperties()
            .keySet()
            .stream()
            .filter(name -> !name.equals(MANA_COST) && !name.equals(COOLDOWN));
        return /*selectedAbility.getElemenType() == ElementRegistry.UTILITY.get() && */!filterOutBase.toList().isEmpty();
//        return selectedAbility.getElemenType() == ElementRegistry.UTILITY.get() && !filterOutBase.toList().isEmpty();
    }


    public static Component getCurrentModifierRating(Ability ability, AbilityHolder holder, ItemStack itemStack1, String keys) {
        if(ability == null) return Component.empty();
        var abilityModifier = holder.data().abilityProperties().get(keys);
        if(abilityModifier == null) return Component.empty();

        // CHECK THIS IF ISSUE
        var format = Maths.roundNonWholeString(Maths.doubleFormattedDouble(abilityModifier.actualValue()));

        if (itemStack1 != null) {
            int comparisonResult;
            var matchedTag = itemStack1.get(ComponentReg.ABILITY_HOLDER.get());
            if (matchedTag != null) {
                var matchedModifier = matchedTag.data().abilityProperties().get(keys);
                if (matchedModifier != null) {
                    var getMatchedEntry = matchedModifier.actualValue();
                    var getHoveredEntry = abilityModifier.actualValue();
                    var isHigherBetter = abilityModifier.isHigherBetter();

                    var isEven = getHoveredEntry == getMatchedEntry;
                    var isBetter = getHoveredEntry > getMatchedEntry;
                    var isWorse = getHoveredEntry < getMatchedEntry;

                    var higherNumber = isBetter ? 2 : isEven ? 1 : 3;
                    var lowerNumber = isWorse ? 2 : isEven ? 1 : 3;

                    comparisonResult = isHigherBetter ? higherNumber : lowerNumber;

                    return getFormattedModifiers(keys, ability, format, comparisonResult);
                }
            }
        } else {
            return getFormattedModifiers(keys, ability, format, 1);
        }

        return Component.empty();
    }

    public static Component getModifierContext(String keys, String current, int getComparison, boolean isRange, String min, String max) {
        String displayValue;
        var time = List.of("Duration", "Speed", "Delay", "Time");
        var probability = List.of("Chance");
        var distance = List.of("Radius", "Distance", "Range");
        var leech = List.of("Leech");
        var multiplier = List.of("Multiplier");
        var by = List.of("Block Size");

        if (time.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(ticksToTime(min, true), ticksToTime(max, true)) : ticksToTime(current, true);
        }  else if (leech.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(min + "%", max + "%") : current + "%";
        } else if (probability.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(toPercent(Double.parseDouble(min)), toPercent(Double.parseDouble(max))) + "%" : toPercent(Double.parseDouble(current)) + "%";
        } else if (distance.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(min, max) + " Blocks" : current + " Blocks";
        } else if (multiplier.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(min, max) + "x" : current + "x";
        } else if (by.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(min + "x" + min, max + "x" + max) : current + " x " + current;
        } else {
            displayValue = isRange ? rangeString(min, max) : current;
        }

        var matchesStat = 5987164;
        var betterThanStat = -12988840;
        var worseThanStat = -47032;
        var colour = isRange ? matchesStat : getComparison == 1 ? matchesStat : getComparison == 2 ? betterThanStat : worseThanStat;

        return Component
          .literal(roundNonWholeString(displayValue))
          .withStyle(style -> style.withColor(colour));
    }

    public static List<Component> getAllAbilityModifiers(
          Ability ability,
          AbilityHolder holder,
          boolean hide,
          boolean showUnlockDetails,
          Player player
    ){
        var toolTips = new ArrayList<Component>();
        var exceptions = List.of(COOLDOWN, MANA_COST, SET_ELEMENT_TYPE, "index", OFFSET, "buddy");
        var data = player.getData(AttachmentReg.CASTER_DATA);
        var unlocked = holder != null && data.hasAbility(holder);
        var subHeaderColour = -2434342;
        var curlyStart = String.valueOf((char) 171);
        var curlyEnd = String.valueOf((char) 187);

        toolTips.add(getAbilityName(holder));

        if(hide || unlocked){
            toolTips.add(JahdooRarity.addRarityTooltip(ability.rarity(), player.level()));
            toolTips.add(Component.empty());

            var filteredSuffix = holder.data().abilityProperties().keySet()
                .stream()
                .filter(abilityModifiers -> !exceptions.contains(abilityModifiers))
                .toList();

            if (holder.data().abilityProperties().containsKey(MANA_COST)) {
                toolTipBase(toolTips, ability, holder, null, MANA_COST, ColourStore.AETHER_BLUE, hide);
            }

            if (holder.data().abilityProperties().containsKey(COOLDOWN)) {
                toolTipBase(toolTips, ability, holder, null, COOLDOWN, ColourStore.COOLDOWN_GREEN, hide);
            }

            if (!filteredSuffix.isEmpty()) {
                toolTips.add(Component.literal(" "));
                toolTips.add(Helpers.withStyleComponentTrans("augmentHelper.jahdoo.attributes", subHeaderColour, curlyStart, curlyEnd));
                filteredSuffix.forEach(keys -> toolTipBase(toolTips, ability, holder, null, keys, 0, hide));
            }
        }

        if(!unlocked && showUnlockDetails){
            toolTips.addLast(Component.empty());
            var prefix = withStyleComponent("Cost: ", SUB_HEADER_COLOUR);
            var suffix = withStyleComponent("◆ " + ability.getAbilityCost() + " Skill Points", PERK_GREEN).copy();
            toolTips.addLast(prefix.copy().append(suffix));
            var hasDependency = ability.levelRequirement() <= data.getLevel();

            if(!hasDependency){
                var prefix1 = withStyleComponent("Requires Level: ", SUB_HEADER_COLOUR);
                var suffix1 = withStyleComponent(ability.levelRequirement() + "", ability.getElemenType().textColourA()).copy();
                toolTips.addLast(prefix1.copy().append(suffix1));
            }
        }

        return toolTips;
    }

}
