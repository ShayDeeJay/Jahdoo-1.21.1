package org.jahdoo.trial_nexus.boon.player_boons.boons;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.trial_nexus.boon.player_boons.AbstractPlayerBoons;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jetbrains.annotations.Nullable;

public class MaxAbsorptionBoon extends AbstractPlayerBoons {

    @Override
    public @Nullable AbstractElement element() {
        return null;
    }

    @Override
    public String getLabel() {
        return JUGGERNOG;
    }

    @Override
    public String id() {
        return "max_absorption_boon";
    }

    @Override
    public int colour() {
        return ColourStore.ABSORPTION_TEXT_YELLOW;
    }

    @Override
    public int getTextureId() {
        return 3;
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.MAX_ABSORPTION;
    }

    @Override
    public double getValue(JahdooRarity getRarity) {
        return getRarity.getAttributes().getRandomMaxAbsorption();
    }

}
