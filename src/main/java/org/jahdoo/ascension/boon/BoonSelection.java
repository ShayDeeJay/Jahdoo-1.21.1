package org.jahdoo.ascension.boon;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.ElementReg;

import java.util.List;
import java.util.UUID;

import static net.minecraft.network.chat.Component.*;
import static org.jahdoo.ascension.utils.Helpers.*;

public class BoonSelection {

    public static Boon getTypeAttributeBoon(Holder<Attribute> attribute, double value, Player player){
        var id = attribute.value().getDescriptionId();
        var string = translatable(id).getString();
        var text = "+" + Maths.doubleFormattedDouble(value) + "% " + string;
        System.out.println(attribute.getRegisteredName());
        var colourBy = RuneHelpers.getColourBy(attribute.getRegisteredName());


        return new Boon(text, colourBy, () -> addTransientAttribute(player, value, UUID.randomUUID().toString(), attribute));
    }

    public static List<Boon> addAttribute(Player player){
        var randomElement = ElementReg.random();
        var attributes = JahdooRarity.getRarity().getAttributes();

        return List.of(
            getTypeAttributeBoon(randomElement.manaReduction(), attributes.getRandomManaReduction(), player),
            getTypeAttributeBoon(randomElement.damageAmplifier(), attributes.getRandomDamage(), player),
            getTypeAttributeBoon(randomElement.cooldownReduction(), attributes.getRandomCooldown(), player),
            getTypeAttributeBoon(AttributeReg.MANA_POOL, attributes.getRandomManaPool(), player),
            getTypeAttributeBoon(AttributeReg.MANA_REGEN, attributes.getRandomManaRegen(), player),
            //Not percentages soo best to move to method or detect and change with if on name
            getTypeAttributeBoon(Attributes.MAX_HEALTH, attributes.getRandomMaxHealth(), player),
            getTypeAttributeBoon(Attributes.MAX_ABSORPTION, attributes.getRandomMaxAbsorption(), player)
        );
    }

}
