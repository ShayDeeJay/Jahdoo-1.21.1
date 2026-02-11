package org.jahdoo.common.items.runes.resilience_rune;

import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.RESILIENCE;

public abstract class ProtectorRune extends AbstractRune {

    @Override
    public RuneCategories runeCategory() {
        return RESILIENCE;
    }

    @Override
    public int runeColour() {
        return ColourHelpers.getNegativeRed();
    }

}
