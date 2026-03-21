package org.jahdoo.common.items.runes.aether_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

import static org.jahdoo.common.registers.AttributeReg.MANA_REGEN;

public class ManaRegenRune extends AbstractAetherRune{

    @Override
    public Holder<Attribute> attributeHolder() {
        return MANA_REGEN.getDelegate();
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public String runeId() {
        return "mana_regen_rune";
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomManaRegen();
    }

    @Override
    public DisplayType displayType() {
        return DisplayType.PERCENT;
    }

}
