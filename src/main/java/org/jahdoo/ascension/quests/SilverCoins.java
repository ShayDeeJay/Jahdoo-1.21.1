package org.jahdoo.ascension.quests;

import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.ascension.attachments.RunData;

public class SilverCoins extends CollectCoinQuest {

    @Override
    public PlayerWallet.CoinProperties coinType() {
        return PlayerWallet.CoinProperties.SILVER;
    }

    @Override
    public String questName() {
        return RunData.SILVER_COIN;
    }

}
