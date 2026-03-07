package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.resources.ResourceLocation;

abstract public class AbstractSkill {

    public abstract String id();

    public abstract ResourceLocation icon();

    public abstract int unlockCost();

    public abstract int levelRequirement();

    public abstract String description();

}
