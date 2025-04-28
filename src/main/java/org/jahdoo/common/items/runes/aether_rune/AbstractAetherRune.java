package org.jahdoo.common.items.runes.aether_rune;

import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.AETHER;

public abstract class AbstractAetherRune extends AbstractRune {
    @Override
    public RuneCategories runeCategory() {
        return AETHER;
    }

    @Override
    public int runeColour() {
        return runeCategory().getColour();
    }
}
