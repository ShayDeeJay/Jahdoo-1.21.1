package org.jahdoo.trial_nexus.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.stats.RoomsCleared;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import java.util.List;

public class ClearRoomsQuest extends AbstractQuest{

    @Override
    public String questName() {
        return RoomsCleared.ID;
    }

    @Override
    public ResourceLocation questIcon() {
        return Icons.UP;
    }

    @Override
    public InstanceDifficulty difficulty() {
        return InstanceDifficulty.NOVICE;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 10, 10, 10);
    }

    @Override
    public String getDisplayName() {
        return "Rush";
    }

    @Override
    public String questDescription(Player player) {
        return "Clear " + questQuantity(player) + " Rooms";
    }

    @Override
    public int questColour() {
        return ColourHelpers.getPerkGreen();
    }

    @Override
    public int questXp(Player player) {
        return questValueMultiplier(player, 140, 125, 5);
    }

    @Override
    public List<ItemStack> questRewards() {
        return super.questRewards();
    }


}
