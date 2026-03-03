package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;

public abstract class AbstractCoinStat extends AbstractStatEntry {

    abstract public PlayerWallet.CoinProperties coinProperties();

    public String id() {
        return coinProperties().getSerializedName();
    }

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
