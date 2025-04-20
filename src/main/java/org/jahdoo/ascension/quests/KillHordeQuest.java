package org.jahdoo.ascension.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.ascension.attachments.RunData.MOBS_KILLED;

public class KillHordeQuest extends AbstractQuest{

    @Override
    public String questName() {
        return MOBS_KILLED;
    }

    @Override
    public ResourceLocation questIcon() {
        return Icons.HORDE;
    }

    @Override
    public InstanceDifficulty difficulty() {
        return InstanceDifficulty.EASY;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 50, 50, 10);
    }

    @Override
    public String getDisplayName() {
        return "Massacre";
    }

    @Override
    public String questDescription(Player player) {
        return "Kill " + questQuantity(player) + " Mobs";
    }

}
