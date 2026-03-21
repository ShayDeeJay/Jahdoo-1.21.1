package org.jahdoo.common.items.runes.resilience_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

public class StrikerRune extends ProtectorRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.ATTACK_DAMAGE;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public String runeId() {
        return "striker_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomHealChance();
    }

}
