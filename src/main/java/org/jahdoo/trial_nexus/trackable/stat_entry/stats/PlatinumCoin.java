package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import org.jahdoo.trial_nexus.attachments.PlayerWallet;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.PLATINUM;

public class PlatinumCoin extends AbstractCoinStat{

    public static final String ID = PLATINUM.getSerializedName();

    @Override
    public PlayerWallet.CoinProperties coinProperties() {
        return PlayerWallet.CoinProperties.PLATINUM;
    }

}
