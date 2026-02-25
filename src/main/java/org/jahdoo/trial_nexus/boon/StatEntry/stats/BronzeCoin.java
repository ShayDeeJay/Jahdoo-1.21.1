package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import org.jahdoo.trial_nexus.attachments.PlayerWallet;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.BRONZE;

public class BronzeCoin extends AbstractCoinStat{

    public static final String id = BRONZE.getSerializedName() + "_collected";

    @Override
    public PlayerWallet.CoinProperties coinProperties() {
        return BRONZE;
    }

    @Override
    public String id() {
        return BRONZE.getSerializedName() + "_collected";
    }

}
