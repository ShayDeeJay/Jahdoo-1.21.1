package org.jahdoo.ascension.quests;

import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.ascension.attachments.RunData;

public class BronzeCoins extends CollectCoinQuest {

    @Override
    public PlayerWallet.CoinProperties coinType() {
        return PlayerWallet.CoinProperties.BRONZE;
    }

    @Override
    public String questName() {
        return RunData.BRONZE_COIN;
    }

}
