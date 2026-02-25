package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import org.jahdoo.trial_nexus.attachments.PlayerWallet;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.GOLD;

public class GoldCoin extends AbstractCoinStat{

    public static final String id = GOLD.getSerializedName() + "_collected";

    @Override
    public String id() {
        return GOLD.getSerializedName() + "_collected";
    }


    @Override
    public PlayerWallet.CoinProperties coinProperties() {
        return PlayerWallet.CoinProperties.GOLD;
    }

}
