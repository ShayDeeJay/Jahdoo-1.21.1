package org.jahdoo.trial_nexus.boon.player_boons.boons;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.trial_nexus.boon.player_boons.AbstractPlayerBoons;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class ManaPoolBoon extends AbstractPlayerBoons {

    @Override
    public @Nullable AbstractElement element() {
        return null;
    }

    @Override
    public String getLabel() {
        return MANA_MOJITO;
    }

    @Override
    public String id() {
        return "mana_pool_boon";
    }

    @Override
    public int colour() {
        return ColourHelpers.getAetherBlue();
    }

    @Override
    public int getTextureId() {
        return 2;
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return AttributeReg.MANA_POOL;
    }

    @Override
    public double getValue(JahdooRarity getRarity) {
        return getRarity.getAttributes().getRandomManaPool();
    }

}
