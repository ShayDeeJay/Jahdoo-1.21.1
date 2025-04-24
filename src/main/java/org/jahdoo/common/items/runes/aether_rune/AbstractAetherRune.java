package org.jahdoo.common.items.runes.aether_rune;

import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneGenerator;

import static org.jahdoo.ascension.utils.ColourStore.AETHER_BLUE;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.AETHER;

public abstract class AbstractAetherRune extends AbstractRune {

    @Override
    public RuneGenerator runeGenerator(int tier, RarityAttributes rarityAttributes) {
        return new RuneGenerator.Builder(attributeHolder())
            .setValue(getAttribute(rarityAttributes))
            .setName(type())
            .setRarity(runeRarity())
            .setTier(tier)
            .setColour(AETHER_BLUE)
            .setModelData(AETHER.getModel())
            .build();
    }

    @Override
    public String type() {
        return AETHER.getName();
    }

}
