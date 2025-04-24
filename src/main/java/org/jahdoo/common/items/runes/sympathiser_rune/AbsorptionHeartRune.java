package org.jahdoo.common.items.runes.sympathiser_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;

import static org.jahdoo.ascension.rarity.JahdooRarity.ETERNAL;
import static org.jahdoo.common.registers.AttributeReg.ABSORPTION_HEARTS;

public class AbsorptionHeartRune extends AbstractSympathiserRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return ABSORPTION_HEARTS.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return ETERNAL;
    }

    @Override
    public String runeId() {
        return "absorption_hearts_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomHealChance();
    }

}
