package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class Ores extends AbstractStatEntry {

    public static final String ID = "ore";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public int colour() {
        return ColourHelpers.getRating1Gray();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.ORE_MULTIPLIER;
    }

    @Override
    public StatCategory category() {
        return StatCategory.ORE;
    }
}
