package org.jahdoo.common.items.runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;

public abstract class AbstractRune {

    public abstract Holder<Attribute> attributeHolder();

    public abstract JahdooRarity runeRarity();

    public abstract String runeId();

    public abstract int runeColour();

    public abstract String runeDescription();

    public abstract double getAttribute(RarityAttributes rarityAttributes);

    public abstract RuneCategories runeCategory();

    public double baseValue(){
        return 0.0;
    };

}
