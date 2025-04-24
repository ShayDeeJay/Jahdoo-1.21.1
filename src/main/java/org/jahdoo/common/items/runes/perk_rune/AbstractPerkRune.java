package org.jahdoo.common.items.runes.perk_rune;

import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneGenerator;

import static org.jahdoo.ascension.utils.ColourStore.PERK_GREEN;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.PERK;

public abstract class AbstractPerkRune extends AbstractRune {

    public abstract double baseValue();

    public abstract String description();

    @Override
    public RuneGenerator runeGenerator(int tier, RarityAttributes rarityAttributes) {
        return  new RuneGenerator.Builder(attributeHolder())
            .setValue(getAttribute(rarityAttributes))
            .setName(PERK.getName())
            .setRarity(runeRarity())
            .setDescription(description())
            .setTier(tier)
            .setColour(PERK_GREEN)
            .setModelData(PERK.getModel())
            .setConvertPercentage(baseValue())
            .build();
    }

    @Override
    public String type() {
        return PERK.getName();
    }
}
