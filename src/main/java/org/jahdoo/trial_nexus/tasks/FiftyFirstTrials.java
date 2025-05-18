package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;

import java.util.List;

public class FiftyFirstTrials extends AbstractTask {

    @Override
    public ResourceLocation taskIcon() {
        return Icons.CHEST_COMMON;
    }

    @Override
    public String taskName() {
        return "Fifty First Trials";
    }

    @Override
    public String taskDescription() {
        return "Clear 50 rooms in Novice difficulty";
    }

    @Override
    public boolean completionPredicate(Player player) {
        return trackedValue(player) >= countRequired();
    }

    @Override
    public int trackedValue(Player player) {
        var x = PlayerTrialData.getData(player).getInstanceData()
            .stream()
            .filter(s -> s.getDifficulty().equals(InstanceDifficulty.NOVICE.getSerializedName()))
            .filter(s -> s.getClearedRooms() >= 50)
            .toList();
        var current = RunData.getRunData(player).getRoomsCleared();
        return x.size() + current;
    }

    @Override
    public int countRequired() {
        return 1;
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
        return TriggerType.USE;
    }

}
