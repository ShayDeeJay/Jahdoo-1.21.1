package org.jahdoo.ascension.boon;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.networking.client2server.AttributeC2SP;
import org.jahdoo.common.networking.client2server.EffectC2SP;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.ElementReg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.network.chat.Component.*;
import static net.neoforged.neoforge.network.PacketDistributor.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Maths.*;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.*;

public class BoonSelection {

    public static ResourceLocation iconFromEffect(Holder<MobEffect> effect){
        var name = Arrays.stream(effect.value().getDescriptionId().split("\\.")).toList().getLast();
        var location = ResourceLocation.withDefaultNamespace("textures/mob_effect/" + name + ".png");
//        var location = ResourceLocation.withDefaultNamespace("");

        return location;
    }

    public static Boon attributeBoon(Holder<Attribute> attribute, double value, boolean isPercentage, ResourceLocation icon){
        var id = attribute.value().getDescriptionId();
        var split = translatable(id).getString();
        var string = Arrays.stream(split.split(" ")).toList();
        var getValue = roundNonWholeString(doubleFormattedDouble(value));
        var formattedString = (value < 0 ? "" : "+") + getValue + (isPercentage ? "% " : " ");
        var colourBy = getColourBy(attribute.getRegisteredName());
        var componentList = new ArrayList<Component>();

        componentList.add(withStyleComponent(formattedString, value < 0 ? NEGATIVE_RED : UNIQUE_A));

        if(split.length() > 22){
            for (var i = 0; i < string.size(); i += 2) {
                var s = i + 1 < string.size() ? " " + string.get(i + 1) : "";
                componentList.add(withStyleComponent(string.get(i) + s, colourBy));
            }
        } else {
            componentList.add(withStyleComponent(split, colourBy));
        }

        return new Boon(componentList, colourBy, () -> sendToServer(new AttributeC2SP(attribute, value)), icon);
    }

    public static Boon effectBoon(Holder<MobEffect> effect, int value, int duration){
        var colour = !effect.value().isBeneficial() ? NEGATIVE_RED : UNIQUE_A;
        var name = translatable(effect.value().getDescriptionId()).getString();
        var instance = new JahdooMobEffect(effect, duration, value);
        var durationComp = withStyleComponent("Duration: ", colour);
        var amplifierComp = withStyleComponent("Amplifier: ", colour);

        var list = List.of(
            withStyleComponent(name, OFF_WHITE),
            Component.empty(),
            durationComp.copy().append(withStyleComponent(ticksToTime(String.valueOf(duration)), colour)),
            amplifierComp.copy().append(withStyleComponent(String.valueOf(value), colour))
        );

        return new Boon(list, -1, () -> sendToServer(new EffectC2SP(instance)), iconFromEffect(effect));
    }

    public static List<Boon> positiveBoons(){
        var randomElement = ElementReg.random();
        var attributes = JahdooRarity.getRarity().getAttributes();

        return List.of(
            attributeBoon(randomElement.manaReduction(), attributes.getRandomManaReduction(), true, randomElement.iconTexture()),
            attributeBoon(randomElement.damageAmplifier(), attributes.getRandomDamage(), true, randomElement.iconTexture()),
            attributeBoon(randomElement.cooldownReduction(), attributes.getRandomCooldown(), true, randomElement.iconTexture()),
            attributeBoon(AttributeReg.MANA_POOL, attributes.getRandomManaPool(), true, res("textures/mob_effect/mana_pool.png")),
            attributeBoon(AttributeReg.MANA_REGEN, attributes.getRandomManaRegen(), true, res("textures/mob_effect/mana_regen.png")),
            //Not percentages soo best to move to method or detect and change with if on name
            attributeBoon(Attributes.MAX_HEALTH, attributes.getRandomMaxHealth(), false, iconFromEffect(MobEffects.HEAL)),
            attributeBoon(Attributes.MAX_ABSORPTION, attributes.getRandomMaxAbsorption(), false, iconFromEffect(MobEffects.ABSORPTION)),
            attributeBoon(Attributes.ATTACK_DAMAGE, attributes.getRandomMaxHealth(), false, iconFromEffect(MobEffects.DAMAGE_BOOST)),
            effectBoon(MobEffects.REGENERATION, 1, 200),
            effectBoon(MobEffects.MOVEMENT_SPEED, 1, 200),
            effectBoon(MobEffects.DAMAGE_BOOST, 1, 200),
            effectBoon(MobEffects.DAMAGE_RESISTANCE, 1, 200)
        );
    }

    public static List<Boon> negativeBoons(){
        var randomElement = ElementReg.random();
        var attributes = JahdooRarity.getRarity().getAttributes();

        return List.of(
            attributeBoon(randomElement.manaReduction(), -attributes.getRandomManaReduction(), true, randomElement.iconTexture()),
            attributeBoon(randomElement.damageAmplifier(), -attributes.getRandomDamage(), true, randomElement.iconTexture()),
            attributeBoon(randomElement.cooldownReduction(),-attributes.getRandomCooldown(), true, randomElement.iconTexture()),
            attributeBoon(AttributeReg.MANA_POOL, -attributes.getRandomManaPool(), true, res("textures/mob_effect/mana_pool.png")),
            attributeBoon(AttributeReg.MANA_REGEN, -attributes.getRandomManaRegen(), true, res("textures/mob_effect/mana_regen.png")),
            //Not percentages soo best to move to method or detect and change with if on name
            Boon.EMPTY,
            attributeBoon(Attributes.MAX_HEALTH, -attributes.getRandomMaxHealth(), false, iconFromEffect(MobEffects.HEAL)),
            attributeBoon(Attributes.MAX_ABSORPTION, -attributes.getRandomMaxAbsorption(), false, iconFromEffect(MobEffects.ABSORPTION)),
            attributeBoon(Attributes.ATTACK_DAMAGE, -attributes.getRandomMaxHealth(), false, iconFromEffect(MobEffects.DAMAGE_BOOST)),
            effectBoon(MobEffects.MOVEMENT_SLOWDOWN, 1, 200),
            effectBoon(MobEffects.HUNGER, 1, 200),
            effectBoon(MobEffects.CONFUSION, 1, 200),
            effectBoon(MobEffects.POISON, 1, 200)
        );
    }


}
