package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

abstract public class AbstractSkill {

    public abstract String id();

    public abstract ResourceLocation icon();

    public abstract int unlockCost();

    public abstract int levelRequirement();

    public abstract String description();

    public Holder<Attribute> attributeModifier() {
        return null;
    }

}
