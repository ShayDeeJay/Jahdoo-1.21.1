package org.jahdoo.common.items.runes.elemental_rune.mystic_runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;

import static org.jahdoo.ascension.rarity.JahdooRarity.COMMON;

public class MysticDamageRune extends AbstractMysticRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return getElement().damageAmplifier();
    }

    @Override
    public JahdooRarity runeRarity() {
        return COMMON;
    }

    @Override
    public String runeId() {
        return prefix()+"_damage_rune";
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