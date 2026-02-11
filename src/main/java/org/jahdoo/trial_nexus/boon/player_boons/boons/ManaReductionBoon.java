package org.jahdoo.trial_nexus.boon.player_boons.boons;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.boon.player_boons.AbstractPlayerBoons;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class ManaReductionBoon extends AbstractPlayerBoons {

    @Override
    public @Nullable AbstractElement element() {
        return ElementReg.random();
    }

    @Override
    public String getLabel() {
        return ELEMENTAL_ENERGY;
    }

    @Override
    public String id() {
        return "mana_reduction_boon";
    }

    @Override
    public int colour() {
        return ColourHelpers.getAetherBlue();
    }

    @Override
    public int getTextureId() {
        return 1;
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return element().manaReduction();
    }

    @Override
    public double getValue(JahdooRarity getRarity) {
        return getRarity.getAttributes().getRandomManaReduction();
    }

}
