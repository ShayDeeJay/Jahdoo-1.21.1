package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class Ores extends AbstractStatEntry {
    @Override
    public String id() {
        return "ores_mined";
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
