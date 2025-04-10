package org.jahdoo.ascension.ability.skills;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

abstract public class AbstractSkill {

    abstract String name();

    abstract ResourceLocation icon();

    abstract void doOnCall();

    abstract List<Component> tooltip();

    abstract int unlockCost();

    abstract String dependency();

    abstract int levelRequirement();

}
