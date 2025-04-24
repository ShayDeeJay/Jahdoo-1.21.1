package org.jahdoo.common.items.runes.perk_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;

import static org.jahdoo.ascension.rarity.JahdooRarity.EPIC;
import static org.jahdoo.common.items.runes.rune_data.RuneData.NO_VALUE;
import static org.jahdoo.common.registers.AttributeReg.DESTINY_BOND;

public class DestinyBondRune extends AbstractPerkRune{

    @Override
    public double baseValue() {
        return NO_VALUE;
    }

    @Override
    public String description() {
        return "Keep your item on death.";
    }

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
