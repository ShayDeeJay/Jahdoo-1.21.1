package org.jahdoo.ascension.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.client.Icons;

import java.util.List;

import static org.jahdoo.ascension.attachments.RunData.ROOMS_CLEARED;

public class ClearRoomsQuest extends AbstractQuest{

    @Override
    public String questName() {
        return ROOMS_CLEARED;
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
        return ColourStore.PERK_GREEN;
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
