package org.jahdoo.challenge.trading_post;

import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.RarityAttributes;
import org.jahdoo.items.runes.rune_data.RuneGenerator;
import org.jahdoo.utils.ModHelpers;

import java.util.List;

import static org.jahdoo.ability.rarity.JahdooRarity.*;
import static org.jahdoo.ability.rarity.JahdooRarity.LEGENDARY;
import static org.jahdoo.items.runes.rune_data.RuneData.NO_DESCRIPTION;
import static org.jahdoo.items.runes.rune_data.RuneData.NO_VALUE;
import static org.jahdoo.items.runes.rune_data.RuneGenerator.*;
import static org.jahdoo.items.runes.rune_data.RuneGenerator.generateSympathiserRune;
import static org.jahdoo.registers.AttributesRegister.*;

public class ShoppingRunes {

    public static RuneGenerator getMidRangeEliteRunes(AbstractElement getElement, RarityAttributes attributes, int id){
        var getRune = List.of(
                generateElementalRune(getElement.getTypeManaReduction(), attributes.getRandomManaReduction(), COMMON, getElement.getTypeId(), id),
                generateElementalRune(getElement.getDamageTypeAmplifier(), attributes.getRandomDamage(), RARE, getElement.getTypeId(), id),
                generatePerkRune(Attributes.MOVEMENT_SPEED, attributes.getRandomDamage(), RARE, NO_DESCRIPTION, id, 0.1),
                generateAetherRune(MANA_REGEN.getDelegate(), attributes.getRandomManaRegen(), id),
                generateAetherRune(MANA_POOL.getDelegate(), attributes.getRandomManaPool(), id)
        );
        return ModHelpers.getRandomListElement(getRune);
    }

    public static RuneGenerator getBetterRangeEliteRunes(RarityAttributes attributes, int id){
        var getRune = List.of(
                generatePerkRune(Attributes.MAX_HEALTH, attributes.getRandomMaxHealth(), LEGENDARY, "Increase max health", id, NO_VALUE),
                generatePerkRune(Attributes.MAX_ABSORPTION, attributes.getRandomMaxAbsorption(), LEGENDARY, "Increase absorption heart capacity", id, NO_VALUE)
        );
        return ModHelpers.getRandomListElement(getRune);
    }

    public static RuneGenerator getLegendaryRangeEliteRunes(RarityAttributes attributes, int id){
        var getRune = List.of(
                generateCosmicRune(MAGIC_DAMAGE_MULTIPLIER.getDelegate(), attributes.getRandomDamage(), id).build(),
                generateCosmicRune(COOLDOWN_REDUCTION.getDelegate(), attributes.getRandomCooldown(), id).build(),
                generateCosmicRune(MANA_COST_REDUCTION.getDelegate(), attributes.getRandomManaReduction(), id).build()
        );
        return ModHelpers.getRandomListElement(getRune);
    }

    public static RuneGenerator getEternalEliteRunes(RarityAttributes attributes, int id){
        var getRune = List.of(
                generateSympathiserRune(CAST_HEAL.getDelegate(), attributes.getRandomHealChance(), id),
                generateSympathiserRune(ABSORPTION_HEARTS.getDelegate(), attributes.getRandomHealChance(), id),
                generateSympathiserRune(SKIP_MANA.getDelegate(), attributes.getRandomHealChance(), id),
                generateSympathiserRune(SKIP_COOLDOWN.getDelegate(), attributes.getRandomHealChance(), id)
        );
        return ModHelpers.getRandomListElement(getRune);
    }

}

