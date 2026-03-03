package org.jahdoo.trial_nexus.ability;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.common.client.screens.AugmentScreen;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.mojang.blaze3d.platform.InputConstants.KEY_LSHIFT;
import static com.mojang.blaze3d.platform.InputConstants.KEY_TAB;
import static org.jahdoo.common.registers.mod.ElementReg.UTILITY;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.ability.AbilityRating.*;

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

    public static boolean holdKey(List<Component> toolTips, boolean spacer){
        var keyI = KEY_TAB;
        if(!ClientHelpers.isKeyDown(keyI)){
            if(spacer) toolTips.add(Component.literal(" "));
            var holdToDiscover = TextHelpers.displaySelectedKey(keyI);
            toolTips.add(holdToDiscover);
            return true;
        }
        return false;
    }

    public static List<Component> holdKey(boolean showSpacer, boolean canUpgrade){
        var comps = new ArrayList<Component>();
        var tab = KEY_TAB;

        if(!ClientHelpers.isKeyDown(tab)){
            if(showSpacer) comps.add(Component.literal(" "));
            comps.add(TextHelpers.displaySelectedKey(tab));

            if(showSpacer){
                if(!canUpgrade){
                    comps.add(TextHelpers.displaySplitText("keys.jahdoo.upgrade", "keys.jahdoo.click"));
                }
                var leftShift = TextHelpers.withStyleComponentTrans(InputConstants.getKey(KEY_LSHIFT, 0).getName(), 0);
                var leftClick = TextHelpers.withStyleComponentTrans("keys.jahdoo.click", 0);
                var prefix = TextHelpers.withStyleComponent("[" + leftShift.getString() + " + " + leftClick.getString() + "]", ColourHelpers.getSubHeaderColour());
                var suffix = TextHelpers.withStyleComponentTrans("keys.jahdoo.add", ColourHelpers.getHeaderColour(), prefix);

                comps.add(suffix);
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
                return TextHelpers.withStyleComponent(getAbility.getAbilityName(), getAbility.getElemenType().textColourA());
            }
        }

        return Component.empty();
    }

    public static Screen getAugmentModificationScreenWand(Player player, Screen previousScreen) {
        var ability = getAbility(player);
        if(ability.isPresent()){
            if (isConfigAbility(player)) {
                return new AugmentScreen(player, previousScreen, ability.get());
            }
        }
        return null;
    }

    private static @NotNull Optional<Ability> getAbility(Player player) {
        var data = player.getData(AttachmentReg.CASTER_DATA);
        return AbilityReg.getFirstSpellByTypeId(data.getSelectedAbility());
    }

    public static boolean isConfigAbility(Player player) {
        var ability = getAbility(player);
        if(ability.isEmpty()) return false;

        var filterOutBase = CasterData.entityHolderWithSelected(player).data().abilityProperties()
            .keySet()
            .stream()
            .filter(name -> !name.equals(MANA_COST) && !name.equals(COOLDOWN));
        return ability.get().getElemenType().equals(UTILITY.get()) && !filterOutBase.toList().isEmpty();
    }

    public static Component getCurrentModifierRating(Ability ability, AbilityHolder holder, ItemStack itemStack1, String keys) {
        if(ability == null) return Component.empty();
        var abilityModifier = holder.data().abilityProperties().get(keys);
        if(abilityModifier == null) return Component.empty();

        // CHECK THIS IF ISSUE
        var format = MathHelpers.roundNonWholeString(MathHelpers.doubleFormattedDouble(abilityModifier.setValue()));

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
        var time = List.of("Duration", "Speed", "Delay", "Time", "Lifetime");
        var probability = List.of("Chance");
        var distance = List.of("Radius", "Distance", "Range");
        var leech = List.of("Leech");
        var multiplier = List.of("Multiplier");
        var by = List.of("Block Size");
        var toggle = List.of("Toggle");

        if (time.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(MathHelpers.ticksToTime(min, true), MathHelpers.ticksToTime(max, true)) : MathHelpers.ticksToTime(current, true);
        }  else if (leech.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(min + "%", max + "%") : current + "%";
        } else if (probability.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(MathHelpers.toPercent(Double.parseDouble(min)), MathHelpers.toPercent(Double.parseDouble(max))) + "%" : MathHelpers.toPercent(Double.parseDouble(current)) + "%";
        } else if (distance.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(min, max) + " Blocks" : current + " Blocks";
        } else if (multiplier.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(min, max) + "x" : current + "x";
        } else if (by.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString(min + "x" + min, max + "x" + max) : current + " x " + current;
        } else if (toggle.stream().anyMatch(keys::contains)) {
            displayValue = isRange ? rangeString("False", "True") : current.equals("1") ? "False" : "True";
        } else {
            displayValue = isRange ? rangeString(min, max) : current;
        }

        var matchesStat = 5987164;
        var betterThanStat = -12988840;
        var worseThanStat = -47032;
        var colour = isRange ? matchesStat : getComparison == 1 ? matchesStat : getComparison == 2 ? betterThanStat : worseThanStat;

        return Component
          .literal(MathHelpers.roundNonWholeString(displayValue))
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
        var data = player.getData(AttachmentReg.CASTER_DATA);
        var unlocked = holder != null && data.hasAbility(holder);

        if(hide || unlocked){
            onlyToolTip(ability, holder, hide, player.level(), toolTips);
        }

        if(!unlocked && showUnlockDetails){
            toolTips.addLast(TextHelpers.withStyleComponent(ability.getAbilityName(), ability.getElemenType().textColourA()));
            toolTips.addLast(Component.empty());
            var prefix = TextHelpers.withStyleComponentTrans("info.jahdoo.cost", ColourHelpers.getSubHeaderColour(), ": ");
            var suffix = TextHelpers.withStyleComponentTrans("info.jahdoo.skill_points", ColourHelpers.getPerkGreen(), "◆" + ability.getAbilityCost() + " ").copy();

            toolTips.addLast(prefix.copy().append(suffix));
            var hasDependency = ability.levelRequirement() <= data.getLevel();

            if(!hasDependency){
                var prefix1 = TextHelpers.withStyleComponentTrans("info.jahdoo.requires_level", ColourHelpers.getSubHeaderColour());
                var suffix1 = TextHelpers.withStyleComponent(ability.levelRequirement() + "", ability.getElemenType().textColourA()).copy();
                toolTips.addLast(prefix1.copy().append(suffix1));
            }
        }

        return toolTips;
    }

    public static void onlyToolTip(Ability ability, AbilityHolder holder, boolean hide, Level level, List<Component> toolTips) {
        var subHeaderColour = -2434342;
        var exceptions = List.of(COOLDOWN, MANA_COST, SET_ELEMENT_TYPE, "index", OFFSET, "buddy");
        var curlyStart = String.valueOf((char) 171);
        var curlyEnd = String.valueOf((char) 187);

        toolTips.add(getAbilityName(holder));
        toolTips.add(JahdooRarity.addRarityTooltip(ability.rarity(), level));
        toolTips.add(Component.empty());

        var filteredSuffix = holder.data()
            .abilityProperties()
            .keySet()
            .stream()
            .filter(abilityModifiers -> !exceptions.contains(abilityModifiers))
            .sorted(
                Comparator.comparing(
                    String::new,
                    Comparator.comparing((String key) -> key.startsWith("Toggle") ? 1 : 0) // Put "Toggle" last
                        .thenComparing(Comparator.naturalOrder()) // Then sort alphabetically
                )
            )
            .toList();

        if (holder.data().abilityProperties().containsKey(MANA_COST)) {
            toolTipBase(toolTips, ability, holder, null, MANA_COST, ColourHelpers.getAetherBlue(), hide);
        }

        if (holder.data().abilityProperties().containsKey(COOLDOWN)) {
            toolTipBase(toolTips, ability, holder, null, COOLDOWN, ColourHelpers.getCooldownGreen(), hide);
        }

        if (!filteredSuffix.isEmpty()) {
            toolTips.add(Component.literal(" "));
            toolTips.add(TextHelpers.withStyleComponentTrans("augmentHelper.jahdoo.attributes", subHeaderColour, curlyStart, curlyEnd));
            filteredSuffix.forEach(keys -> toolTipBase(toolTips, ability, holder, null, keys, 0, hide));
        }
    }

}
