package org.jahdoo.ascension.boon.player_boons;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.networking.client2server.AttributeC2SP;
import org.jahdoo.common.networking.client2server.EffectC2SP;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.mod.ElementReg;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.ascension.rarity.JahdooRarity.getRarity;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Maths.*;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.getColourBy;

public class BoonSelection {

    public static double isNegativeValue(double value, boolean isNegative){
        return isNegative ? -value : value;
    }

    public static ResourceLocation iconFromEffect(Holder<MobEffect> effect){
        var name = stream(effect.value().getDescriptionId().split("\\.")).toList().getLast();
        return withDefaultNamespace("textures/mob_effect/" + name + ".png");
    }

    public static Boon effectBoon(Holder<MobEffect> effect, int value){
        var colour = !effect.value().isBeneficial() ? NEGATIVE_RED : UNIQUE_A;
        var name = translatable(effect.value().getDescriptionId()).getString();
        var duration = Random.nextInt(1200, 3600);
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

    public static Boon getPositiveBoon(){
        var boonCollection = new ArrayList<Boon>();
        var level = getRarity().getId();

        sharedBoons(boonCollection, false);
        if(Random.nextInt(10) == 0){
            boonCollection.add(effectBoon(EffectReg.REPLENISH_MANA, Math.min(level, 2)));
            boonCollection.add(effectBoon(MobEffects.REGENERATION, Math.min(level, 2)));
            boonCollection.add(effectBoon(MobEffects.MOVEMENT_SPEED, Math.min(level, 2)));
            boonCollection.add(effectBoon(MobEffects.DAMAGE_BOOST, level));
            boonCollection.add(effectBoon(MobEffects.DAMAGE_RESISTANCE, level));
//            boonCollection.add(effectBoon(MobEffects.HEAL, level));
            boonCollection.add(effectBoon(MobEffects.ABSORPTION, level));
        }

        return Helpers.listRandom(boonCollection);
    }

    public static Boon getNegativeBoon(){
        var boonCollection = new ArrayList<Boon>();
        var level = getRarity().getId();

        sharedBoons(boonCollection, true);
        if(Random.nextInt(10) == 0){
            boonCollection.add(effectBoon(MobEffects.MOVEMENT_SLOWDOWN, level));
            boonCollection.add(effectBoon(MobEffects.POISON, level));
            boonCollection.add(effectBoon(MobEffects.WITHER, level));
            boonCollection.add(effectBoon(MobEffects.HUNGER, level));
        }

        for (int i = 0; i < 4; i++) boonCollection.add(Boon.EMPTY);

        return Helpers.listRandom(boonCollection);
    }

    private static void sharedBoons(ArrayList<Boon> boonCollection, boolean isNegative) {
        var randomElement = ElementReg.random();
        var attributes = getRarity().getAttributes();

        boonCollection.add(attributeBoon(randomElement.manaReduction(), isNegativeValue(attributes.getRandomManaReduction(), isNegative), true, randomElement.iconTexture()));
        boonCollection.add(attributeBoon(randomElement.damageAmplifier(), isNegativeValue(attributes.getRandomDamage(), isNegative), true, randomElement.iconTexture()));
        boonCollection.add(attributeBoon(randomElement.cooldownReduction(), isNegativeValue(attributes.getRandomCooldown(), isNegative), true, randomElement.iconTexture()));

        boonCollection.add(attributeBoon(AttributeReg.MANA_POOL, isNegativeValue(attributes.getRandomManaPool(), isNegative), true, Icons.MANA));
        boonCollection.add(attributeBoon(AttributeReg.MANA_REGEN, isNegativeValue(attributes.getRandomManaRegen(), isNegative), true, Icons.MANA_REGEN));

        boonCollection.add(attributeBoon(Attributes.MAX_HEALTH, isNegativeValue(attributes.getRandomMaxHealth(), isNegative), false, iconFromEffect(MobEffects.HEAL)));
        boonCollection.add(attributeBoon(Attributes.MAX_ABSORPTION, isNegativeValue(attributes.getRandomMaxAbsorption(), isNegative), false, iconFromEffect(MobEffects.ABSORPTION)));
        boonCollection.add(attributeBoon(Attributes.ATTACK_DAMAGE, isNegativeValue(attributes.getRandomMaxHealth(), isNegative), false, iconFromEffect(MobEffects.DAMAGE_BOOST)));
        boonCollection.add(attributeBoon(Attributes.ATTACK_SPEED, isNegativeValue(attributes.getRandomCooldown() / 10, isNegative), false, Icons.ATTACK_SPEED));
    }

    public static Boon attributeBoon(Holder<Attribute> attribute, double value, boolean isPercentage, ResourceLocation icon){
        var id = attribute.value().getDescriptionId();
        var split = translatable(id).getString();
        var string = stream(split.split(" ")).toList();
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

}
