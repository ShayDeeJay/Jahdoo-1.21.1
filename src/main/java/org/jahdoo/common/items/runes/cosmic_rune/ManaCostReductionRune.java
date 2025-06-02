package org.jahdoo.common.items.runes.cosmic_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.common.registers.AttributeReg.MANA_COST_REDUCTION;

public class ManaCostReductionRune extends AbstractCosmicRune{
    @Override
    public Holder<Attribute> attributeHolder() {
        return MANA_COST_REDUCTION.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public String runeId() {
        return "mana_cost_reduction_rune";
    }

    @Override
    public String runeDescription() {
        return "";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomManaReduction();
    }
}
