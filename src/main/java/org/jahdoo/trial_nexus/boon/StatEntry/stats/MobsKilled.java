package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class MobsKilled extends AbstractStatEntry {
    public static final String id = "mobs_killed";

    @Override
    public String id() {
        return id;
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
