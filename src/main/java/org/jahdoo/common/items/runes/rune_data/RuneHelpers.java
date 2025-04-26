package org.jahdoo.common.items.runes.rune_data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.trading_post.ShoppingItems;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.RuneReg;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.LocalLootBeamData.attachLootBeamComponent;
import static org.jahdoo.ascension.utils.Maths.roundNonWholeString;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.fromName;
import static org.jahdoo.common.items.runes.rune_data.RuneData.DEFAULT;
import static org.jahdoo.common.items.runes.rune_data.RuneData.DEFAULT_NAME;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.ComponentReg.RUNE_DATA;
import static org.jahdoo.common.registers.mod.ElementReg.*;

public class RuneHelpers {

    public static Component getDescription(ItemStack itemStack){
        var data = getRuneData(itemStack);
        return Component.literal(data.description());
    }

    public static Component getNameWithStyle(ItemStack stack){
        return withStyleComponent(getName(stack)+ " " + RuneData.SUFFIX, getColourDarker(getRuneData(stack).getTypeColourSecondary(), 1.6));
    }

    public static RuneData getRuneData(ItemStack stack){
        return stack.getOrDefault(RUNE_DATA, DEFAULT);
    }

    public static int getCostFromRune(@NotNull ItemStack itemStack) {
        var name = RuneHelpers.getName(itemStack);
        var tier = RuneHelpers.getTier(itemStack);
        return fromName(name).getCost(tier);
    }

    public static int getTier(ItemStack itemStack){
        var data = getRuneData(itemStack);
        if(!Objects.equals(data.name(), DEFAULT_NAME)) return data.tier();
        return -1;
    }

    public static String getName(ItemStack itemStack){
        var data = getRuneData(itemStack);
        if(!Objects.equals(data.name(), DEFAULT_NAME)) return Helpers.stringIdToName(data.name());
        return DEFAULT_NAME;
    }

    public static void generateFullRune(ItemStack stack, RuneGenerator runGen) {
        var isPercentage = runGen.getPercentage() > 0 ? (runGen.getValue() * runGen.getPercentage()) / 100 : runGen.getValue();

        replaceOrAddAttribute(stack, runGen.getType().getRegisteredName(), runGen.getType(), isPercentage, EquipmentSlot.MAINHAND, true, "rune");
        stack.set(CUSTOM_MODEL_DATA, new CustomModelData(runGen.getModelData()));
        stack.set(RUNE_DATA, new RuneData(runGen.getElementId(), runGen.getName(), runGen.getDescription(), runGen.getColour(), runGen.getRarity().getId(), runGen.getTier()));
    }

    public static boolean hasDestinyBond(ItemStack itemStack){
        var getHolder = itemStack.get(ComponentReg.RUNE_HOLDER);
        var getBonusDestiny = itemStack.getAttributeModifiers().modifiers().stream().toList();
        for (var entry : getBonusDestiny) {
            if(entry.attribute().value() == DESTINY_BOND.get()) return true;
        }

        if(getHolder == null) return false;

        for (var runeSlot : getHolder.runeSlots()) {
            for (var modifier : runeSlot.getAttributeModifiers().modifiers()) {
                if(modifier.attribute().equals(DESTINY_BOND)) return true;
            }
        }

        return false;
    }

    public static int getColourBy(String attributeName){

        if(attributeName.contains("mystic")){
            return mystic().textColourA();
        } else if (attributeName.contains("vitality")) {
            return vitality().textColourA();
        } else if (attributeName.contains("inferno")) {
            return inferno().textColourA();
        } else if (attributeName.contains("frost")) {
            return frost().textColourA();
        } else if (attributeName.contains("mana.mana")) {
            return ColourStore.AETHER_BLUE;
        } else if (attributeName.contains("skills") || attributeName.contains("speed")) {
            return ColourStore.PERK_GREEN;
        }else if (attributeName.contains("max_health")){
            return ColourStore.MAGNET_STRENGTH_RED;
        }else if (attributeName.contains("max_absorption")){
            return ColourStore.ABSORPTION_YELLOW;
        }

        return -1;
    }

    public static Component standAloneAttributes(ItemAttributeModifiers.Entry entry) {
        var colourPre = ColourStore.PERK_GREEN;
        var descriptionId = entry.attribute().value().getDescriptionId();
        var amount = entry.modifier().amount();
        var typeColour = getColourBy(descriptionId);
        var compName = withStyleComponentTrans(descriptionId, typeColour);
        var isAbsorption = descriptionId.contains("absorption");
        var isMaxHealth = descriptionId.contains("health");
        var isSpeed = descriptionId.contains("speed");

        if(isSpeed) amount = amount * 1000;
        if(isAbsorption || isMaxHealth) amount = (amount/2);
        return getComponents(amount, descriptionId, isAbsorption, isMaxHealth, colourPre, compName);
    }

    private static @NotNull MutableComponent getComponents(double amount, String descriptionId, boolean isAbsorption, boolean isMaxHealth, int colourPre, Component compName) {
        var value = roundNonWholeString(singleFormattedDouble(amount));

        if(descriptionId.contains(FIXED_VALUE) || isAbsorption || isMaxHealth) {
            var text = "+" + value + " ";
            return withStyleComponent(text, colourPre).copy().append(compName);
        }

        if(descriptionId.contains("skills")){
            return withStyleComponent("", colourPre).copy().append(compName);
        }

        var prefix = descriptionId.contains("reduction") ? "-" : "+";
        return withStyleComponent(prefix + value + "%" + " ", colourPre).copy().append(compName);
    }

    public static Component standAloneAttributes(ItemStack itemStack) {
        var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
        if (attributes.isEmpty()) return Component.empty();

        var data = getRuneData(itemStack);
        var colourPre = ColourStore.CHAMPION_GOLD;
        var entry = attributes.getFirst();
        var descriptionId = entry.attribute().value().getDescriptionId();
        var amount = entry.modifier().amount();
        var typeColour = data.elementId() < 1 ? data.colour() : fromId(data.elementId()).orElseThrow().textColourA();
        var compName = withStyleComponentTrans(descriptionId, typeColour);
        var isAbsorption = descriptionId.contains("absorption");
        var isMaxHealth = descriptionId.contains("health");
        var isSpeed = descriptionId.contains("speed");

        if(isSpeed) amount = amount * 1000;
        if(isAbsorption || isMaxHealth) amount = (amount/2);

        return getComponents(amount, descriptionId, isAbsorption, isMaxHealth, colourPre, compName);
    }

    public static void generateRandomTypAttribute(
        ItemStack stack,
        @Nullable JahdooRarity tierRarity,
        @Nullable JahdooRarity runeRarity,
        int chestTier
    ) {
        if(stack.getAttributeModifiers().modifiers().isEmpty()){
            var getTierRarity = tierRarity != null ? tierRarity : JahdooRarity.getRarity();
            var getRuneRarity = runeRarity != null ? runeRarity : ShoppingItems.getRaritiesByChestRarity(chestTier);

            RuneReg.getRuneWithRarity(getRuneRarity).ifPresent(
                rune -> {
                    attachLootBeamComponent(stack, getTierRarity);
                    generateFullRune(stack, rune.runeGenerator(getTierRarity.getId(), getTierRarity.getAttributes()));
                }
            );
        }
    }

    public static ItemStack generateRandomTypAttribute(
        JahdooRarity tierRarity,
        JahdooRarity...jahdooRarities
    ) {
        var stack = new ItemStack(ItemReg.RUNE);
        if(stack.getAttributeModifiers().modifiers().isEmpty()){
            var rune = Helpers.listRandom(RuneReg.getAllRuneWithRarity(jahdooRarities));

            attachLootBeamComponent(stack, tierRarity);
            generateFullRune(stack, rune.runeGenerator(tierRarity.getId(), tierRarity.getAttributes()));

        }
        return stack;
    }

}