package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import org.jahdoo.trial_nexus.attachments.PlayerWallet;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.GOLD;

public class GoldCoin extends AbstractCoinStat{

    public static final String ID = GOLD.getSerializedName();

    @Override
    public PlayerWallet.CoinProperties coinProperties() {
        return PlayerWallet.CoinProperties.GOLD;
    }

}
