package org.jahdoo.common.items.runes.perk_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;

import static org.jahdoo.ascension.rarity.JahdooRarity.LEGENDARY;
import static org.jahdoo.common.items.runes.rune_data.RuneData.NO_VALUE;

public class MaxHealthRune extends AbstractPerkRune{

    @Override
    public double baseValue() {
        return NO_VALUE;
    }

    @Override
    public String description() {
        return "Increase max health capacity";
    }

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
