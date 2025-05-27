package org.jahdoo.trial_nexus.boon.player_boons.boons;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.trial_nexus.boon.player_boons.AbstractPlayerBoons;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jetbrains.annotations.Nullable;

public class AttackSpeedBoon extends AbstractPlayerBoons {

    @Override
    public @Nullable AbstractElement element() {
        return null;
    }

    @Override
    public String getLabel() {
        return LEGIONNAIRE_LEMONADE;
    }

    @Override
    public String id() {
        return "attack_speed_boon";
    }

    @Override
    public int colour() {
        return ColourStore.RATING_5_GREEN;
    }

    @Override
    public int getTextureId() {
        return 4;
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.ATTACK_SPEED;
    }

    @Override
    public double getValue(JahdooRarity getRarity) {
        return getRarity.getAttributes().getRandomCooldown() / 10;
    }

}
