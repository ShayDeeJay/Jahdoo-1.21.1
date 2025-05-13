package org.jahdoo.common.items.runes.resilience_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;
import org.jahdoo.common.registers.AttributeReg;

public class ResilienceRune extends ProtectorRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return AttributeReg.RESILIENCE;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public String runeId() {
        return "resilience_rune";
    }


    @Override
    public String runeDescription() {
        return "";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomHealChance();
    }

}
