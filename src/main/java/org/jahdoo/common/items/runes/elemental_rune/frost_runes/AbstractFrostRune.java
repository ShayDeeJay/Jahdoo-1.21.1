package org.jahdoo.common.items.runes.elemental_rune.frost_runes;

import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.ELEMENTAL;

public abstract class AbstractFrostRune extends AbstractRune {

    public AbstractElement getElement(){
        return ElementReg.frost();
    }

    @Override
    public RuneCategories runeCategory() {
        return ELEMENTAL;
    }

    public String prefix(){
        return "frost";
    }

    @Override
    public int runeColour() {
        return getElement().textColourA();
    }

}
