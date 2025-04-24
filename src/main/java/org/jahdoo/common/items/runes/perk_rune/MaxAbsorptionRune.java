package org.jahdoo.common.items.runes.perk_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;

import static org.jahdoo.ascension.rarity.JahdooRarity.RARE;
import static org.jahdoo.common.items.runes.rune_data.RuneData.NO_DESCRIPTION;

public class MaxAbsorptionRune extends AbstractPerkRune{

    @Override
    public double baseValue() {
        return 0.1;
    }

    @Override
    public String description() {
        return NO_DESCRIPTION;
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.MOVEMENT_SPEED;
    }

    @Override
    public JahdooRarity runeRarity() {
        return RARE;
    }

    @Override
    public String runeId() {
        return "movement_speed_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomDamage();
    }

}
