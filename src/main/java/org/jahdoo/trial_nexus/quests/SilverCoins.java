package org.jahdoo.trial_nexus.quests;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.boon.StatEntry.stats.SilverCoin;

import java.util.List;

public class SilverCoins extends CollectCoinQuest {

    @Override
    public PlayerWallet.CoinProperties coinType() {
        return PlayerWallet.CoinProperties.SILVER;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 10, 5, 10);
    }

    @Override
    public String questName() {
        return SilverCoin.id;
    }

    @Override
    public int questXp(Player player) {
        return questValueMultiplier(player, 100, 150, 5);
    }

    @Override
    public List<ItemStack> questRewards() {
        return super.questRewards();
    }

}
