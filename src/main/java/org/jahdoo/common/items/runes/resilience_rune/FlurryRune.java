package org.jahdoo.common.items.runes.resilience_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;

public class FlurryRune extends ProtectorRune {

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.ATTACK_SPEED;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public String runeId() {
        return "flurry_rune";
    }

    @Override
    public double baseValue() {
        return 4;
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
