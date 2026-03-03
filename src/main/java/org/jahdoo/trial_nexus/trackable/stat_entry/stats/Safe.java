package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class Safe extends AbstractStatEntry {

    public static final String ID = "safe";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public int colour() {
        return ColourHelpers.getRating5Green();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.SAFE;
    }

    @Override
    public StatCategory category() {
        return StatCategory.LOOT;
    }
}
