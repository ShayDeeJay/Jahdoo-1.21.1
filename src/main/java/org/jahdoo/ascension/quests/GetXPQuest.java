package org.jahdoo.ascension.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.client.Icons;

import java.util.List;

import static org.jahdoo.ascension.attachments.RunData.EXPERIENCE;

public class GetXPQuest extends AbstractQuest{

    @Override
    public String questName() {
        return EXPERIENCE;
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
        return questValueMultiplier(player, 200, 350, 10);
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
        return ColourStore.COSMIC_PURPLE;
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
