package org.jahdoo.trial_nexus.quests;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.boon.StatEntry.stats.GoldCoin;

import java.util.List;

public class GoldCoins extends CollectCoinQuest {

    @Override
    public PlayerWallet.CoinProperties coinType() {
        return PlayerWallet.CoinProperties.GOLD;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 15, 7, 10);
    }

    @Override
    public String questName() {
        return GoldCoin.id;
    }

    @Override
    public int questXp(Player player) {
        return questValueMultiplier(player, 150, 650, 10);
    }

    @Override
    public List<ItemStack> questRewards() {
        return super.questRewards();
    }

}
