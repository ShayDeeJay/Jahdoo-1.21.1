package org.jahdoo.common.items.runes.elemental_rune.inferno_runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;

import static org.jahdoo.ascension.rarity.JahdooRarity.EPIC;

public class InfernoCooldownRune extends AbstractInfernoRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return getElement().cooldownReduction();
    }

    @Override
    public JahdooRarity runeRarity() {
        return EPIC;
    }

    @Override
    public String runeId() {
        return prefix()+"_cooldown_rune";
    }

    @Override
    public String runeDescription() {
        return "";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomCooldown();
    }

}