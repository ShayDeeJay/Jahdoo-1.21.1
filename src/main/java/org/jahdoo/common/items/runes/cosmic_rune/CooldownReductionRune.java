package org.jahdoo.common.items.runes.cosmic_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.common.registers.AttributeReg.COOLDOWN_REDUCTION;

public class CooldownReductionRune extends AbstractCosmicRune{

    @Override
    public Holder<Attribute> attributeHolder() {
        return COOLDOWN_REDUCTION.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public String runeId() {
        return "cooldown_reduction_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomCooldown();
    }

}
