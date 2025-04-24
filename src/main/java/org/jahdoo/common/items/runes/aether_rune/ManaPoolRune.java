package org.jahdoo.common.items.runes.aether_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;

import static org.jahdoo.common.registers.AttributeReg.MANA_POOL;

public class ManaPoolRune extends AbstractAetherRune {
    @Override
    public Holder<Attribute> attributeHolder() {
        return MANA_POOL.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public String runeId() {
        return "mana_pool_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomManaPool();
    }
}
