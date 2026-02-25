package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class ExperienceStat extends AbstractStatEntry {
    public static final String id = "experience_gained";

    @Override
    public String id() {
        return id;
    }

    @Override
    public int colour() {
        return ColourHelpers.getCosmicPurple();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.TRIAL_EXPERIENCE;
    }

    @Override
    public StatCategory category() {
        return StatCategory.GENERAL;
    }

}
