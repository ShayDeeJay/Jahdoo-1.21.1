package org.jahdoo.common.items.runes.elemental_rune.vitality_runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.COMMON;

public class VitalityDamageRune extends AbstractVitalityRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return getElement().damageAmplifier();
    }

    @Override
    public JahdooRarity runeRarity() {
        return COMMON;
    }

    @Override
    public String runeId() {
        return prefix()+"_damage_rune";
    }

    @Override
    public String runeDescription() {
        return "";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomDamage();
    }

}