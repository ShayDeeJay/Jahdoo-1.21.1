package org.jahdoo.ascension.quests;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.ascension.attachments.RunData;

import java.util.List;

public class GoldCoins extends CollectCoinQuest {

    @Override
    public PlayerWallet.CoinProperties coinType() {
        return PlayerWallet.CoinProperties.GOLD;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 20, 5, 5);
    }

    @Override
    public String questName() {
        return RunData.GOLD_COIN;
    }

    @Override
    public int questXp(Player player) {
        return questValueMultiplier(player, 50, 150, 5);
    }

    @Override
    public List<ItemStack> questRewards() {
        return super.questRewards();
    }

}
