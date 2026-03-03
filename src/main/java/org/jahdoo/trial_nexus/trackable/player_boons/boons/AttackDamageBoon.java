package org.jahdoo.trial_nexus.trackable.player_boons.boons;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.trial_nexus.trackable.player_boons.AbstractPlayerBoon;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class AttackDamageBoon extends AbstractPlayerBoon {

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
        return "attack_damage_boon";
    }

    @Override
    public int colour() {
        return ColourHelpers.getMagnetStrengthRed();
    }

    @Override
    public int getTextureId() {
        return 4;
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return Attributes.ATTACK_DAMAGE;
    }

    @Override
    public double getValue(JahdooRarity getRarity) {
        return getRarity.getAttributes().getRandomMaxHealth();
    }

}
