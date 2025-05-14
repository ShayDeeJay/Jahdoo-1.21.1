package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;

public class BabyAssassin extends AbstractTask {

    @Override
    public ResourceLocation taskIcon() {
        return Icons.HORDE;
    }

    @Override
    public String taskName() {
        return "Baby Assassin";
    }

    @Override
    public String taskDescription() {
        return "Kill 1000 mobs in the trial dimension";
    }

    @Override
    public boolean completionPredicate(Player player) {
        var x = PlayerTrialData.getData(player).getPastRuns().stream().mapToInt(RunData::getMobsKilled).sum();
        var current = RunData.getStat(player, RunData.MOBS_KILLED);
        return x + current >= 1000;
    }

    @Override
    public void triggerAchievement() {

    }

    @Override
    public int trackedValue(Player player) {
        var x = PlayerTrialData.getData(player).getPastRuns().stream().mapToInt(RunData::getMobsKilled).sum();
        var current = RunData.getStat(player, RunData.MOBS_KILLED);
        System.out.println(x);
//        System.out.println("dfdf");
        return x + current;
    }

    @Override
    public int countRequired() {
        return 1000;
    }

}
