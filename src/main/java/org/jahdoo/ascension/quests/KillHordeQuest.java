package org.jahdoo.ascension.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.client.Icons;

import java.util.List;

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
        return ColourStore.NEGATIVE_RED;
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
