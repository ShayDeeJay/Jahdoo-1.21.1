package org.jahdoo.common.items.runes.sympathiser_rune;

import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneGenerator;

import static org.jahdoo.ascension.rarity.JahdooRarity.ETERNAL;
import static org.jahdoo.ascension.utils.ColourStore.SYMPATHISER_ORANGE;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.INFINITY;

public abstract class AbstractSympathiserRune extends AbstractRune {

    @Override
    public RuneGenerator runeGenerator(int tier, RarityAttributes rarityAttributes) {
        return new RuneGenerator.Builder(attributeHolder())
            .setValue(getAttribute(rarityAttributes))
            .setName(INFINITY.getName())
            .setRarity(ETERNAL)
            .setTier(tier)
            .setColour(SYMPATHISER_ORANGE)
            .setModelData(INFINITY.getModel())
            .build();
    }

    @Override
    public String type() {
        return INFINITY.getName();
    }
}
