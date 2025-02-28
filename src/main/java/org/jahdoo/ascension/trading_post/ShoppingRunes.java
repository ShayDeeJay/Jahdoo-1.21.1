package org.jahdoo.ascension.trading_post;

import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.runes.rune_data.RuneGenerator;

import java.util.List;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;
import static org.jahdoo.common.items.runes.rune_data.RuneData.NO_DESCRIPTION;
import static org.jahdoo.common.items.runes.rune_data.RuneData.NO_VALUE;
import static org.jahdoo.common.items.runes.rune_data.RuneGenerator.*;
import static org.jahdoo.common.registers.AttributeReg.*;

public class ShoppingRunes {

    public static RuneGenerator getMidRangeEliteRunes(AbstractElement getElement, RarityAttributes attributes, int id){
        var getRune = getGetRune(getElement, attributes, id);
        return Helpers.listRandom(getRune);
    }

    public static RuneGenerator getLegendaryRangeEliteRunes(RarityAttributes attributes, int id){
        var getRune = List.of(
            generateCosmicRune(MAGIC_DAMAGE_MULTIPLIER.getDelegate(), attributes.getRandomDamage(), id).build(),
            generateCosmicRune(COOLDOWN_REDUCTION.getDelegate(), attributes.getRandomCooldown(), id).build(),
            generateCosmicRune(MANA_COST_REDUCTION.getDelegate(), attributes.getRandomManaReduction(), id).build()
        );
        return Helpers.listRandom(getRune);
    }

    public static RuneGenerator getBetterRangeEliteRunes(RarityAttributes attributes, int id){
        var maxHealth = "Increase max health";
        var absorption = "Increase absorption heart capacity";
        var getRune = List.of(
            generatePerkRune(MAX_HEALTH, attributes.getRandomMaxHealth(), LEGENDARY, maxHealth, id, NO_VALUE),
            generatePerkRune(MAX_ABSORPTION, attributes.getRandomMaxAbsorption(), LEGENDARY, absorption, id, NO_VALUE)
        );
        return Helpers.listRandom(getRune);
    }

    public static RuneGenerator getEternalEliteRunes(RarityAttributes attributes, int id){
        var getRune = List.of(
            generateSympathiserRune(CAST_HEAL.getDelegate(), attributes.getRandomHealChance(), id),
            generateSympathiserRune(ABSORPTION_HEARTS.getDelegate(), attributes.getRandomHealChance(), id),
            generateSympathiserRune(SKIP_MANA.getDelegate(), attributes.getRandomHealChance(), id),
            generateSympathiserRune(SKIP_COOLDOWN.getDelegate(), attributes.getRandomHealChance(), id)
        );
        return Helpers.listRandom(getRune);
    }

    public static List<RuneGenerator> getGetRune(AbstractElement getElement, RarityAttributes attributes, int id) {
        return List.of(
            generateElementalRune(getElement.manaReduction(), attributes.getRandomManaReduction(), COMMON, getElement.id(), id),
            generateElementalRune(getElement.damageAmplifier(), attributes.getRandomDamage(), RARE, getElement.id(), id),
            generatePerkRune(MOVEMENT_SPEED, attributes.getRandomDamage(), RARE, NO_DESCRIPTION, id, 0.1),
            generateAetherRune(MANA_REGEN.getDelegate(), attributes.getRandomManaRegen(), id),
            generateAetherRune(MANA_POOL.getDelegate(), attributes.getRandomManaPool(), id)
        );
    }

}

