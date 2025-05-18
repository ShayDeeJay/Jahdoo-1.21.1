package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.List;

import static org.jahdoo.JahdooMod.MOD_ID;

public abstract class AbstractTask {

    public abstract ResourceLocation taskIcon();

    public abstract String taskName();

    public abstract String taskDescription();

    public abstract boolean completionPredicate(Player player);

    public abstract int trackedValue(Player player);

    public abstract int countRequired();

    public abstract TriggerType type();

    public abstract List<ItemStack> rewards();

    public String taskId() {
        return MOD_ID +"-"+ Helpers.nameToId(taskName()) + "-task";
    }

    public enum TriggerType {
        KIll, USE, ROOM_CLEAR, QUEST_COMPLETION
    }

}
