package org.jahdoo.common.items.runes.perk_rune;

import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.PERK;

public abstract class AbstractPerkRune extends AbstractRune {

    @Override
    public RuneCategories runeCategory() {
        return PERK;
    }

    @Override
    public int runeColour() {
        return PERK.getColour();
    }

}
