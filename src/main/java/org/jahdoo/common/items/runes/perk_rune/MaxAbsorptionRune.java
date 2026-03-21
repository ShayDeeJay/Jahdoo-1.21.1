package org.jahdoo.common.items.runes.perk_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.RARE;

public class MaxAbsorptionRune extends AbstractPerkRune{

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.MAX_ABSORPTION;
    }

    @Override
    public JahdooRarity runeRarity() {
        return RARE;
    }

    @Override
    public String runeId() {
        return "absorption_heart_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomMaxAbsorption();
    }

    @Override
    public DisplayType displayType() {
        return DisplayType.FIXED;
    }

}
