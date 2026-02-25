package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public class RoomsCleared extends AbstractStatEntry {

    public static final String id = "rooms_cleared";

    @Override
    public String id() {
        return id;
    }

    @Override
    public int colour() {
        return ColourHelpers.getWalletBrown();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.ROOMS_CLEARED;
    }

    @Override
    public StatCategory category() {
        return StatCategory.GENERAL;
    }

}
