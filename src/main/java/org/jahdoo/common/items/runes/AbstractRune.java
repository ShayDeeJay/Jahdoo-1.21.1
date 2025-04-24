package org.jahdoo.common.items.runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.common.items.runes.rune_data.RuneGenerator;

public abstract class AbstractRune {

    public abstract RuneGenerator runeGenerator(int tier, RarityAttributes rarityAttributes);

    public abstract Holder<Attribute> attributeHolder();

    public abstract JahdooRarity runeRarity();

    public abstract String type();

    public abstract String runeId();

    public abstract double getAttribute(RarityAttributes rarityAttributes);

}
