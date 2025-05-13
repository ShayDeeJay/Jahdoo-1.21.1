package org.jahdoo.common.items.runes.sympathiser_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.ETERNAL;
import static org.jahdoo.common.registers.AttributeReg.SKIP_COOLDOWN;

public class SkipCooldownRune extends AbstractSympathiserRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return SKIP_COOLDOWN.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return ETERNAL;
    }

    @Override
    public String runeId() {
        return "skip_cooldown_rune";
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
