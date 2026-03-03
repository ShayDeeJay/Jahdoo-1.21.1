package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import org.jahdoo.trial_nexus.attachments.PlayerWallet;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.BRONZE;

public class BronzeCoin extends AbstractCoinStat{

    public static final String ID = BRONZE.getSerializedName();

    @Override
    public PlayerWallet.CoinProperties coinProperties() {
        return BRONZE;
    }

}
