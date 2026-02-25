package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;

public abstract class AbstractCoinStat extends AbstractStatEntry {

    abstract public PlayerWallet.CoinProperties coinProperties();

    abstract public String id();

    @Override
    public int colour() {
        return coinProperties().getTextColour();
    }

    @Override
    public ResourceLocation icon() {
        return coinProperties().getLocation();
    }

    @Override
    public StatCategory category() {
        return StatCategory.COIN;
    }
}
