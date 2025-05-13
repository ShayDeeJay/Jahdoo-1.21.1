package org.jahdoo.common.items.runes.perk_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.LEGENDARY;

public class MovementSpeedRune extends AbstractPerkRune{

    @Override
    public double baseValue() {
        return 0.1;
    }

    @Override
    public String runeDescription() {
        return "Increase absorption heart capacity";
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.MOVEMENT_SPEED;
    }

    @Override
    public JahdooRarity runeRarity() {
        return LEGENDARY;
    }

    @Override
    public String runeId() {
        return "max_absorption_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomMaxAbsorption();
    }

}
