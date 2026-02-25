package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class ChampionsKilled extends AbstractStatEntry {
    @Override
    public String id() {
        return "champions_killed";
    }

    @Override
    public int colour() {
        return ColourHelpers.getChampionGold();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.CHAMPIONS_CROWN;
    }

    @Override
    public StatCategory category() {
        return StatCategory.MOB;
    }
}
