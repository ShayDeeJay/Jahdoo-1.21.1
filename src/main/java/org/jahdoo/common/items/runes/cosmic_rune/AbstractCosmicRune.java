package org.jahdoo.common.items.runes.cosmic_rune;

import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneGenerator;

import static org.jahdoo.ascension.rarity.JahdooRarity.ETERNAL;
import static org.jahdoo.ascension.utils.ColourStore.COSMIC_PURPLE;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.COSMIC;

public abstract class AbstractCosmicRune extends AbstractRune {

    @Override
    public RuneGenerator runeGenerator(int tier, RarityAttributes rarityAttributes) {
        return new RuneGenerator.Builder(attributeHolder())
            .setValue(this.getAttribute(rarityAttributes))
            .setName(type())
            .setRarity(ETERNAL)
            .setTier(tier)
            .setColour(COSMIC_PURPLE)
            .setModelData(COSMIC.getModel())
            .build();
    }

    @Override
    public String type() {
        return COSMIC.getName();
    }

}
