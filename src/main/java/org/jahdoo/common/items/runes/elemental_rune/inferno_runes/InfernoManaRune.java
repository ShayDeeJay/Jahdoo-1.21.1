package org.jahdoo.common.items.runes.elemental_rune.inferno_runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.COMMON;

public class InfernoManaRune extends AbstractInfernoRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return getElement().manaReduction();
    }

    @Override
    public JahdooRarity runeRarity() {
        return COMMON;
    }

    @Override
    public String runeId() {
        return prefix()+"_mana_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomManaReduction();
    }

}