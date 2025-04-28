package org.jahdoo.common.items.runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;

public class BlankRune extends AbstractRune{

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.SCALE;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public String runeId() {
        return "blank_rune";
    }

    @Override
    public int runeColour() {
        return ColourStore.SUB_HEADER_COLOUR;
    }

    @Override
    public String runeDescription() {
        return "";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return 0;
    }

    @Override
    public RuneCategories runeCategory() {
        return RuneCategories.EMPTY;
    }
}
