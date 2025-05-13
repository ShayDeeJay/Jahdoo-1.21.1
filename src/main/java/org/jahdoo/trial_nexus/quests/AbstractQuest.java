package org.jahdoo.trial_nexus.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQuest {

    public abstract ResourceLocation questIcon();

    public abstract InstanceDifficulty difficulty();

    public abstract String questName();

    public abstract String getDisplayName();

    public abstract String questDescription(Player player);

    public abstract int questQuantity(Player player);

    public abstract int questColour();

    public abstract int questXp(Player player);

    public List<ItemStack> questRewards(){
        var rewards = new ArrayList<ItemStack>();
        rewards.add(new ItemStack(BlockReg.LOOT_CRATE.get()));
        rewards.add(new ItemStack(ItemReg.EXIT_KEY));
        return rewards;
    }

    /**@param player takes player and get level automatically.
     * @param baseValue the base value to be scaled.
     * @param scaler the amount to be scaled by every level
     * @param scaleLevel the level interval to scale the value by eg. every 10 levels.
     * */
    public static int questValueMultiplier(Player player, int baseValue, int scaler, int scaleLevel) {
        int level = CasterData.getLevel(player);

        // Calculate base quantity: increases by 'scaler' every 'scaleLevel' levels
        int quantity = baseValue + scaler * (level / scaleLevel);

        // Ensure it never goes below the base value
        return Math.max(quantity, baseValue);
    }

}
