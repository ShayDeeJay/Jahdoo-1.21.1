package org.jahdoo.common.items.runes.rune_data;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.runes.AbstractRune;
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

public class RuneHelpers {

    public static Component getDescription(ItemStack itemStack){
        var data = getRuneData(itemStack);
        var fromCategory = RuneReg.getRuneFromId(data.name());
        return Component.literal(fromCategory.runeDescription());
    }

    public static Component getNameWithStyle(ItemStack stack){
        var name = getRuneData(stack);
        if(!name.name().contains("blank")){
            var fromCategory = RuneReg.getRuneFromId(name.name());
            return withStyleComponent(stringIdToName(fromCategory.runeCategory().getName()) + " " + RuneData.SUFFIX, getColourDarker(fromCategory.runeColour(), 1.6));
        }
        return Helpers.withStyleComponent("Blank Rune", -1);
    }

    public static RuneData getRuneData(ItemStack stack){
        return stack.getOrDefault(RUNE_DATA, DEFAULT);
    }

    public static int getCostFromRune(@NotNull ItemStack itemStack) {
        var name = RuneReg.getRuneFromId(RuneHelpers.getRuneData(itemStack).name());
        var tier = RuneHelpers.getTier(itemStack);
        return fromName(name.runeCategory().getName()).getCost(tier);
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

    public static void generateFullRune(ItemStack stack, JahdooRarity tierRarity, AbstractRune rune) {
        var value = rune.getAttribute(tierRarity.getAttributes());
        var isPercentage = rune.baseValue() > 0 ? (value * rune.baseValue()) / 100 : value;

        replaceOrAddAttribute(stack, rune.attributeHolder().getRegisteredName(), rune.attributeHolder(), isPercentage, EquipmentSlot.MAINHAND, true, "rune");
        stack.set(CUSTOM_MODEL_DATA, new CustomModelData(rune.runeCategory().getModel()));
        stack.set(RUNE_DATA, new RuneData(rune.runeId(), tierRarity.getId()));
    }

    public static boolean hasDestinyBond(ItemStack itemStack){
        var getHolder = itemStack.get(ComponentReg.JAHDOO_GEAR_DATA);
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

    public static int getColourBy(Holder<Attribute> attributeHolder){
        var runeFromAttribute = RuneReg.getRuneFromAttribute(attributeHolder.getDelegate());
        return runeFromAttribute == null ? -1 : runeFromAttribute.runeColour();
    }

    private static @NotNull MutableComponent getComponents(double amount, String descriptionId, boolean isAbsorption, boolean isMaxHealth, int colourPre, Component compName) {
        var number = singleFormattedDouble(amount);
        var value = roundNonWholeString(number);

        if(descriptionId.contains(FIXED_VALUE) || isAbsorption || isMaxHealth || descriptionId.contains("armor") || descriptionId.contains("attack_damage")) {
            var text = "+" + value + " ";
            return withStyleComponent(text, colourPre).copy().append(compName);
        }

        if(descriptionId.contains("skills")){
            return withStyleComponent("", colourPre).copy().append(compName);
        }

        var prefix = number < 0 ? "" : descriptionId.contains("reduction") ? "-" : "+";
        return withStyleComponent(prefix + value + "%" + " ", colourPre).copy().append(compName);
    }

    public static Component standAloneAttributes(ItemAttributeModifiers.Entry entry) {
        var attribute = entry.attribute();
        var typeColour = getColourBy(attribute);

        return sharedAttributes(entry, typeColour);
    }

    public static Component standAloneAttributes(ItemStack itemStack) {
        var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
        if (attributes.isEmpty()) return Component.empty();

        var data = getRuneData(itemStack);
        var abstractRune = RuneReg.getRuneFromId(data.name());
        var entry = attributes.getFirst();

        return sharedAttributes(entry, abstractRune.runeColour());
    }

    private static @NotNull MutableComponent sharedAttributes(ItemAttributeModifiers.Entry entry, int colour) {
        var descriptionId = entry.attribute().value().getDescriptionId();
        var amount = entry.modifier().amount();
        var compName = withStyleComponentTrans(descriptionId, colour);
        var isAbsorption = descriptionId.contains("absorption");
        var isMaxHealth = descriptionId.contains("health");
        var isSpeed = descriptionId.contains("speed");
        var isAttackSpeed = descriptionId.contains("attack_speed");

        if(isAttackSpeed) amount = amount * 25;
        if(isSpeed && !isAttackSpeed) amount = amount * 1000;
        if(isAbsorption || isMaxHealth) amount = (amount/2);

        return getComponents(amount, descriptionId, isAbsorption, isMaxHealth, colour, compName);
    }

    public static ItemStack generateRandomTypAttribute(
        @Nullable ItemStack stack,
        @Nullable JahdooRarity tierRarity,
        @Nullable JahdooRarity runeRarity
    ) {
        var correctStack = stack == null ? new ItemStack(ItemReg.RUNE) : stack;
        if(correctStack.getAttributeModifiers().modifiers().isEmpty()){
            var getTierRarity = tierRarity != null ? tierRarity : JahdooRarity.getRarity();
            var getRuneRarity = runeRarity != null ? runeRarity : JahdooRarity.getRarity();

            RuneReg.getRuneWithRarity(getRuneRarity).ifPresent(
                rune -> {
                    attachLootBeamComponent(correctStack, getTierRarity);
                    generateFullRune(correctStack, getTierRarity, rune);
                }
            );
        }
        return correctStack;
    }

    public static ItemStack generateRandomTypAttribute(
        JahdooRarity tierRarity,
        JahdooRarity...jahdooRarities
    ) {
        var stack = new ItemStack(ItemReg.RUNE);
        if(stack.getAttributeModifiers().modifiers().isEmpty()){
            var rune = Helpers.listRandom(RuneReg.getAllRuneWithRarity(jahdooRarities));

            attachLootBeamComponent(stack, tierRarity);
            generateFullRune(stack, tierRarity , rune);

        }
        return stack;
    }

    public static ItemStack generateBlankRune() {
        var stack = new ItemStack(ItemReg.RUNE);
        var tierRarity = JahdooRarity.COMMON;

        if(stack.getAttributeModifiers().modifiers().isEmpty()){
            var rune = RuneReg.BLANK_RUNE.get();
            generateFullRune(stack, tierRarity, rune);

        }

        return stack;
    }

}