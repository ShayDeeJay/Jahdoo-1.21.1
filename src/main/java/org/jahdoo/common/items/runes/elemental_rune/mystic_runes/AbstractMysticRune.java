package org.jahdoo.common.items.runes.elemental_rune.mystic_runes;

import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneGenerator;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.ELEMENTAL;

public abstract class AbstractMysticRune extends AbstractRune {

    public AbstractElement getElement(){
        return ElementReg.inferno();
    }

    @Override
    public RuneGenerator runeGenerator(int tier, RarityAttributes rarityAttributes) {
        return new RuneGenerator.Builder(attributeHolder())
            .setValue(getAttribute(rarityAttributes))
            .setName(type())
            .setRarity(runeRarity())
            .setElementId(getElement().id())
            .setTier(tier)
            .setModelData(getElement().id())
            .build();
    }

    @Override
    public String type() {
        return ELEMENTAL.getName();
    }

    public String prefix(){
        return "mystic";
    }
}
