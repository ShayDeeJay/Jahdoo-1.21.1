package org.jahdoo.trial_nexus.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.stats.ExperienceStat;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import java.util.List;

public class GetXPQuest extends AbstractQuest {

    @Override
    public String questName() {
        return ExperienceStat.ID;
    }

    @Override
    public ResourceLocation questIcon() {
        return Icons.TRIAL_EXPERIENCE;
    }

    @Override
    public InstanceDifficulty difficulty() {
        return InstanceDifficulty.NOVICE;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 200, 400, 5);
    }

    @Override
    public String getDisplayName() {
        return "Experienced";
    }

    @Override
    public String questDescription(Player player) {
        return "Gather " + questQuantity(player) + " Xp";
    }

    @Override
    public int questColour() {
        return ColourHelpers.getCosmicPurple();
    }

    @Override
    public int questXp(Player player) {
        return questValueMultiplier(player, 150, 160, 5);
    }

    @Override
    public List<ItemStack> questRewards() {
        return super.questRewards();
    }
}
