package org.jahdoo.ascension.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.common.client.Icons;

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
        return InstanceDifficulty.EASY;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 5, 10, 10);
    }

    @Override
    public String getDisplayName() {
        return "Rush";
    }

    @Override
    public String questDescription(Player player) {
        return "Clear " + questQuantity(player) + " Rooms";
    }

}
