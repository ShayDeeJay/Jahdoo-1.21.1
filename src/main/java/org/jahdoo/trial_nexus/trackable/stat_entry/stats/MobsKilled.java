package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class MobsKilled extends AbstractStatEntry {
    public static final String ID = "mob";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public int colour() {
        return ColourHelpers.getRating2Red();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.HORDE;
    }

    @Override
    public StatCategory category() {
        return StatCategory.MOB;
    }
}
