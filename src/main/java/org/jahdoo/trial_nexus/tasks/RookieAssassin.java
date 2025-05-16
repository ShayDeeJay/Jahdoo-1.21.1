package org.jahdoo.trial_nexus.tasks;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;

import java.util.List;

public class RookieAssassin extends AbstractTask {

    @Override
    public ResourceLocation taskIcon() {
        return Icons.HORDE;
    }

    @Override
    public String taskName() {
        return "Rookie Assassin";
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

        return x + current;
    }

    @Override
    public int countRequired() {
        return 1000;
    }

    @Override
    public List<ItemStack> rewards() {
        var x = new ItemStack(ItemReg.AUGMENT_CORE);
        var y = new ItemStack(ItemReg.SKILL_POINT);
        var z = new ItemStack(ItemReg.COIN);

        z.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(2));
        CoreData.setFilled(x);

        return List.of(x, y, z);
    }

}
