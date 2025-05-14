package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractTask {

    public abstract ResourceLocation taskIcon();

    public abstract String taskName();

    public abstract String taskDescription();

    public abstract boolean completionPredicate(Player player);

    public abstract void triggerAchievement();

    public abstract int trackedValue(Player player);

    public abstract int countRequired();

}
