package org.jahdoo.common.items.runes.cosmic_rune;

import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.COSMIC;

public abstract class AbstractCosmicRune extends AbstractRune {

    @Override
    public RuneCategories runeCategory() {
        return COSMIC;
    }

    @Override
    public int runeColour() {
        return runeCategory().getColour();
    }

}
