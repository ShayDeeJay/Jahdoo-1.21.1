package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import org.jahdoo.trial_nexus.attachments.PlayerWallet;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties.PLATINUM;

public class PlatinumCoin extends AbstractCoinStat{

    public static final String id = PLATINUM.getSerializedName() + "_collected";

    @Override
    public String id() {
        return PLATINUM.getSerializedName() + "_collected";
    }

    @Override
    public PlayerWallet.CoinProperties coinProperties() {
        return PlayerWallet.CoinProperties.PLATINUM;
    }

}
