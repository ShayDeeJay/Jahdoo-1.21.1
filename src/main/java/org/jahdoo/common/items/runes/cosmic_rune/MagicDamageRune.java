package org.jahdoo.common.items.runes.cosmic_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;

public class MagicDamageRune extends AbstractCosmicRune{
    @Override
    public Holder<Attribute> attributeHolder() {
        return MAGIC_DAMAGE_MULTIPLIER.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.ETERNAL;
    }

    @Override
    public String runeId() {
        return "magic_damage_rune";
    }

    @Override
    public String runeDescription() {
        return "";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomDamage();
    }
}
