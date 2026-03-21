package org.jahdoo.common.items.runes.elemental_rune.frost_runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.EFFECT;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.UNIQUE;

public class FrostEffectRune extends AbstractFrostRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return AttributeReg.FROST_DOUBLED_DAMAGE_CHANCE;
    }

    @Override
    public JahdooRarity runeRarity() {
        return UNIQUE;
    }

    @Override
    public String runeId() {
        return prefix()+"_effect_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomManaPool();
    }

    @Override
    public RuneCategories runeCategory() {
        return EFFECT;
    }

    @Override
    public int runeColour() {
        return getElement().partColourB();
    }

}