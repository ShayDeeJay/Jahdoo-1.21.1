package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;

import java.util.List;

public class DeeperDungeon extends AbstractTask {

    @Override
    public ResourceLocation taskIcon() {
        return Icons.ROOMS_CLEARED;
    }

    @Override
    public String taskName() {
        return "Deepest Dungeon";
    }

    @Override
    public String taskDescription() {
        return "Clear 500 rooms";
    }

    @Override
    public boolean completionPredicate(Player player) {
        return trackedValue(player) >= countRequired();
    }

    @Override
    public int trackedValue(Player player) {
        var x = PlayerTrialData.getData(player).getPastRuns().stream().mapToInt(RunData::getRoomsCleared).sum();
        var current = RunData.getStat(player, RunData.ROOMS_CLEARED);
        return x + current;
    }

    @Override
    public int countRequired() {
        return 500;
    }

    @Override
    public List<ItemStack> rewards() {
        var x = new ItemStack(ItemReg.AUGMENT_CORE);
        var y = new ItemStack(ItemReg.SKILL_POINT);

        CoreData.setFilled(x);
        return List.of(x, y);
    }

    @Override
    public TriggerType type() {
        return TriggerType.ROOM_CLEAR;
    }

}
