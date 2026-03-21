package org.jahdoo.common.items.runes.elemental_rune.vitality_runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.EFFECT;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.UNIQUE;

public class VitalityEffectRune extends AbstractVitalityRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return AttributeReg.VITALITY_EFFECT_HEAL_VALUE;
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
        return ((double) rarityAttributes.getRandomMaxAbsorption() /10) * 2;
    }

    @Override
    public RuneCategories runeCategory() {
        return EFFECT;
    }

    @Override
    public int runeColour() {
        return getElement().partColourB();
    }

    @Override
    public DisplayType displayType() {
        return DisplayType.FIXED;
    }

}