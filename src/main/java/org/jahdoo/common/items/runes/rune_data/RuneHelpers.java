package org.jahdoo.common.items.runes.rune_data;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.registers.ComponentReg;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.LocalLootBeamData.attachLootBeamComponent;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;
import static org.jahdoo.ascension.rarity.JahdooRarity.LEGENDARY;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.Maths.roundNonWholeString;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.fromName;
import static org.jahdoo.common.items.runes.rune_data.RuneData.*;
import static org.jahdoo.common.items.runes.rune_data.RuneGenerator.*;
import static org.jahdoo.common.items.runes.rune_data.RuneGenerator.generateSympathiserRune;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.AttributeReg.CAST_HEAL;
import static org.jahdoo.common.registers.ComponentReg.RUNE_DATA;
import static org.jahdoo.common.registers.ElementReg.*;
import static org.jahdoo.common.registers.ElementReg.random;

public class RuneHelpers {

    public static Component getDescription(ItemStack itemStack){
        var data = getRuneData(itemStack);
        return Component.literal(data.description());
    }

    public static boolean canMageFlight(Player player){
        var attribute = player.getAttribute(MAGE_FLIGHT);
        return attribute != null && attribute.getValue() > 0;
    }

    public static boolean canTripleJump(Player player){
        var attribute = player.getAttribute(TRIPLE_JUMP);
        return attribute != null && attribute.getValue() > 0;
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
        if(!Objects.equals(data.name(), DEFAULT_NAME)) return data.name();
        return DEFAULT_NAME;
    }

    public static void generateFullRune(ItemStack stack, RuneGenerator runGen) {
        var isPercentage = runGen.getPercentage() > 0 ? (runGen.getValue() * runGen.getPercentage()) / 100 : runGen.getValue();
        replaceOrAddAttribute(stack, runGen.getType().getRegisteredName(), runGen.getType(), isPercentage, EquipmentSlot.MAINHAND, true);
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(runGen.getModelData()));

        var value = new RuneData(runGen.getElementId(), runGen.getName(), runGen.getDescription(), runGen.getColour(), runGen.getRarity().getId(), runGen.getTier());
        stack.set(RUNE_DATA, value);
    }

    public static boolean hasDestinyBond(ItemStack itemStack){
        var getHolder = itemStack.get(ComponentReg.RUNE_HOLDER);
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
        } else if (attributeName.contains("skills")) {
            return ColourStore.PERK_GREEN;
        }else if (attributeName.contains("max_health")){
            return ColourStore.MAGNET_STRENGTH_RED;
        }else if (attributeName.contains("max_absorption")){
            return ColourStore.ABSORPTION_YELLOW;
        }

        return -1;
    }

    public static Component standAloneAttributes(ItemAttributeModifiers.Entry entry) {
        var colourPre = color(121, 187, 67);
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

        return withStyleComponent("+" + value + "%" + " ", colourPre).copy().append(compName);
    }

    public static Component standAloneAttributes(ItemStack itemStack) {
        var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
        if (attributes.isEmpty()) return Component.empty();

        var data = getRuneData(itemStack);
        var colourPre = color(121, 187, 67);
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

    public static void generateRandomTypAttribute(ItemStack stack, @Nullable JahdooRarity withRarity) {
        if(stack.getAttributeModifiers().modifiers().isEmpty()){
            var getElement = random();
            var rarity = withRarity != null ? withRarity : JahdooRarity.getRarity();
            var attributes = rarity.getAttributes();
            var id = rarity.getId();

            var getList = switch (getRarity()){
                case COMMON ->
                    List.of(
                        generateElementalRune(getElement.manaReduction(), attributes.getRandomManaReduction(), COMMON, getElement.id(), id),
                        generatePerkRune(DESTINY_BOND.getDelegate(), NO_VALUE, COMMON, "Keep your item on death.", NO_VALUE, NO_VALUE)
                    );
                case RARE ->
                    List.of(
                        generateElementalRune(getElement.damageAmplifier(), attributes.getRandomDamage(), RARE, getElement.id(), id),
                        generatePerkRune(Attributes.MOVEMENT_SPEED, attributes.getRandomDamage(), RARE, NO_DESCRIPTION, id, 0.1)
                    );
                case EPIC ->
                    List.of(
                        generateElementalRune(getElement.cooldownReduction(), attributes.getRandomCooldown(), EPIC, getElement.id(), id),
                        generatePerkRune(TRIPLE_JUMP, 100, RARE, "Allows player to jump up to 3 times", NO_VALUE, 100)
                    );
                case LEGENDARY ->
                    List.of(
                        generateAetherRune(MANA_REGEN.getDelegate(), attributes.getRandomManaRegen(), id),
                        generateAetherRune(MANA_POOL.getDelegate(), attributes.getRandomManaPool(), id),
                        generatePerkRune(Attributes.MAX_HEALTH, attributes.getRandomMaxHealth(), LEGENDARY, "Increase max health", id, NO_VALUE),
                        generatePerkRune(Attributes.MAX_ABSORPTION, attributes.getRandomMaxAbsorption(), LEGENDARY, "Increase absorption heart capacity", id, NO_VALUE),
                        generatePerkRune(MAGE_FLIGHT.getDelegate(), 100, LEGENDARY, "Allows the player to fly, at the cost of mana.", NO_VALUE, 100)
                    );
                case ETERNAL ->
                    List.of(
                        generateCosmicRune(MAGIC_DAMAGE_MULTIPLIER.getDelegate(), attributes.getRandomDamage(), id).build(),
                        generateCosmicRune(COOLDOWN_REDUCTION.getDelegate(), attributes.getRandomCooldown(), id).build(),
                        generateCosmicRune(MANA_COST_REDUCTION.getDelegate(), attributes.getRandomManaReduction(), id).build(),
                        generateSympathiserRune(CAST_HEAL.getDelegate(), attributes.getRandomHealChance(), id),
                        generateSympathiserRune(ABSORPTION_HEARTS.getDelegate(), attributes.getRandomHealChance(), id),
                        generateSympathiserRune(SKIP_MANA.getDelegate(), attributes.getRandomHealChance(), id),
                        generateSympathiserRune(SKIP_COOLDOWN.getDelegate(), attributes.getRandomHealChance(), id)
                    );
                case UNIQUE -> List.of(generateSympathiserRune(CAST_HEAL.getDelegate(), attributes.getRandomHealChance(), id));
            };

            attachLootBeamComponent(stack, rarity);
            generateFullRune(stack, listRandom(getList));
        }
    }

}