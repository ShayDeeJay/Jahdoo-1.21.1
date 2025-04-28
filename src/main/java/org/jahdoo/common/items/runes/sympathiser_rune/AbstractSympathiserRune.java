package org.jahdoo.common.items.runes.sympathiser_rune;

import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.INFINITY;

public abstract class AbstractSympathiserRune extends AbstractRune {

    @Override
    public RuneCategories runeCategory() {
        return INFINITY;
    }

    @Override
    public int runeColour() {
        return INFINITY.getColour();
    }

}
