package org.jahdoo.trial_nexus.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.stats.MobsKilled;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import java.util.List;

public class KillHordeQuest extends AbstractQuest{

    @Override
    public String questName() {
        return MobsKilled.ID;
    }

    @Override
    public ResourceLocation questIcon() {
        return Icons.HORDE;
    }

    @Override
    public InstanceDifficulty difficulty() {
        return InstanceDifficulty.NOVICE;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 150, 50, 10);
    }

    @Override
    public String getDisplayName() {
        return "Massacre";
    }

    @Override
    public String questDescription(Player player) {
        return "Kill " + questQuantity(player) + " Mobs";
    }

    @Override
    public int questColour() {
        return ColourHelpers.getNegativeRed();
    }

    @Override
    public int questXp(Player player) {
        return questValueMultiplier(player, 150, 100, 5);
    }

    @Override
    public List<ItemStack> questRewards() {
        return super.questRewards();
    }

}
