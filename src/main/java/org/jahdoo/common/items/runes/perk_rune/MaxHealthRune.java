package org.jahdoo.common.items.runes.perk_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.LEGENDARY;

public class MaxHealthRune extends AbstractPerkRune{

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.MAX_HEALTH;
    }

    @Override
    public JahdooRarity runeRarity() {
        return LEGENDARY;
    }

    @Override
    public String runeId() {
        return "max_health_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomMaxHealth();
    }

}
