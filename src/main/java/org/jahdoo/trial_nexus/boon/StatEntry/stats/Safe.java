package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class Safe extends AbstractStatEntry {
    @Override
    public String id() {
        return "safe_opened";
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
