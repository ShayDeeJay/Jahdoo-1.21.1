package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class Pots extends AbstractStatEntry {
    @Override
    public String id() {
        return "loot_pots_destroyed";
    }

    @Override
    public int colour() {
        return ColourHelpers.getWalletBrown();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.LOOT_POT_ICON;
    }

    @Override
    public StatCategory category() {
        return StatCategory.LOOT;
    }
}
