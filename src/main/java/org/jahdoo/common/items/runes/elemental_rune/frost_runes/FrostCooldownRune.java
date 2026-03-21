package org.jahdoo.common.items.runes.elemental_rune.frost_runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.EPIC;

public class FrostCooldownRune extends AbstractFrostRune {

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
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomCooldown();
    }

}