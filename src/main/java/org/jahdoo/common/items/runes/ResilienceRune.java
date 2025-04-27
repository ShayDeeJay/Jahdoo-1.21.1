package org.jahdoo.common.items.runes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.rarity.RarityAttributes;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.items.runes.rune_data.RuneGenerator;
import org.jahdoo.common.registers.AttributeReg;

import static org.jahdoo.common.items.runes.rune_data.RuneCategories.RESILIENCE;

public class ResilienceRune extends AbstractRune {

    @Override
    public RuneGenerator runeGenerator(int tier, RarityAttributes rarityAttributes) {
        return new RuneGenerator.Builder(attributeHolder())
            .setValue(getAttribute(rarityAttributes))
            .setName(type())
            .setRarity(runeRarity())
            .setTier(tier)
            .setColour(ColourStore.NEGATIVE_RED)
            .setModelData(RESILIENCE.getModel())
            .build();
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return AttributeReg.RESILIENCE;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public String type() {
        return RESILIENCE.getName();
    }

    @Override
    public String runeId() {
        return "resilience_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomHealChance();
    }

}
