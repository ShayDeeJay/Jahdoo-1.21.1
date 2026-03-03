package org.jahdoo.trial_nexus.quests;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.trackable.stat_entry.stats.BronzeCoin;

import java.util.List;

public class BronzeCoins extends CollectCoinQuest {

    @Override
    public PlayerWallet.CoinProperties coinType() {
        return PlayerWallet.CoinProperties.BRONZE;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 150, 350, 10);
    }

    @Override
    public int questXp(Player player) {
        return questValueMultiplier(player, 150, 150, 5);
    }

    @Override
    public String questName() {
        return BronzeCoin.ID;
    }

    @Override
    public List<ItemStack> questRewards() {
        return super.questRewards();

    }
}
