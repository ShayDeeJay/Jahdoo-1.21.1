package org.jahdoo.common.items.runes.resilience_rune;

import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.RESILIENCE;

public abstract class ProtectorRune extends AbstractRune {

    @Override
    public RuneCategories runeCategory() {
        return RESILIENCE;
    }

    @Override
    public int runeColour() {
        return ColourStore.NEGATIVE_RED;
    }

}
