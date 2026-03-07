package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;

import java.util.List;

public class CommonLooter extends AbstractTask {

    @Override
    public ResourceLocation taskIcon() {
        return Icons.CHEST_COMMON;
    }

    @Override
    public String taskName() {
        return "Common Looter";
    }

    @Override
    public String taskDescription() {
        return "Open 500 common chests";
    }

    @Override
    public boolean completionPredicate(Player player) {
        return trackedValue(player) >= countRequired();
    }

    @Override
    public int trackedValue(Player player) {
        var x = PlayerTrialData.getData(player).getPastRuns().stream().mapToInt(RunData::getCommonChests).sum();
        var current = RunData.getRunData(player).getCommonChests();
        return x + current;
    }

    @Override
    public int countRequired() {
        return 500;
    }

    @Override
    public List<ItemStack> rewards(int tick) {
        var x = new ItemStack(ItemReg.AUGMENT_CORE);
        var y = new ItemStack(ItemReg.SKILL_POINT);
        CoreData.setFilled(x);

        return List.of(x, y);
    }

    @Override
    public TriggerType type() {
        return TriggerType.USE;
    }

}
