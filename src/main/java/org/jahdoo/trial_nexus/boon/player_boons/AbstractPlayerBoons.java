package org.jahdoo.trial_nexus.boon.player_boons;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import javax.annotation.Nullable;

public abstract class AbstractPlayerBoons {

    public static final String ELEMENTAL_ENERGY = "Elemental Energy";
    public static final String MANA_MOJITO = "Mana Mojito";
    public static final String JUGGERNOG = "Juggernog";
    public static final String LEGIONNAIRE_LEMONADE = "Legionnaire Lemonade";

    @Nullable
    public abstract AbstractElement element();

    public abstract String getLabel();

    public abstract String id();

    public abstract int colour();

    public abstract int getTextureId();

    public abstract Holder<Attribute> attributeHolder();

    public abstract boolean isPercentage();

    public abstract double getValue(JahdooRarity rarity);


}
