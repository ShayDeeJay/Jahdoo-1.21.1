package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import org.jahdoo.trial_nexus.attachments.PlayerWallet;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.SILVER;

public class SilverCoin extends AbstractCoinStat{

    public static final String ID = SILVER.getSerializedName() ;

    @Override
    public PlayerWallet.CoinProperties coinProperties() {
        return PlayerWallet.CoinProperties.SILVER;
    }

}
