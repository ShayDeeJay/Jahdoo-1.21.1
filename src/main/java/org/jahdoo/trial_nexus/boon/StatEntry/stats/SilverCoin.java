package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import org.jahdoo.trial_nexus.attachments.PlayerWallet;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.SILVER;

public class SilverCoin extends AbstractCoinStat{

    public static final String id = SILVER.getSerializedName() + "_collected";

    @Override
    public String id() {
        return SILVER.getSerializedName() + "_collected";
    }

    @Override
    public PlayerWallet.CoinProperties coinProperties() {
        return PlayerWallet.CoinProperties.SILVER;
    }

}
