package org.jahdoo.common.items.runes.perk_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.common.registers.AttributeReg.DESTINY_BOND;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.EPIC;

public class DestinyBondRune extends AbstractPerkRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return DESTINY_BOND.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return EPIC;
    }

    @Override
    public String runeId() {
        return "destiny_bond_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return -1;
    }

}
