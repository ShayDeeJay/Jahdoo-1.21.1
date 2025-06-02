package org.jahdoo.common.items.runes.sympathiser_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.MYTHIC;
import static org.jahdoo.common.registers.AttributeReg.SKIP_MANA;

public class SkipManaRune extends AbstractSympathiserRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return SKIP_MANA.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return MYTHIC;
    }

    @Override
    public String runeId() {
        return "skip_mana_rune";
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
