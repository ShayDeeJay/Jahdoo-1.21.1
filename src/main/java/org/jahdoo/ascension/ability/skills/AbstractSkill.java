package org.jahdoo.ascension.ability.skills;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.List;

abstract public class AbstractSkill {

    public abstract String id();

    public abstract ResourceLocation icon();

    public abstract Holder<MobEffect> skillEffect();

    public abstract List<Component> tooltip();

    public abstract int unlockCost();

    public abstract String dependency();

    public abstract int levelRequirement();

    public abstract String description();

}
