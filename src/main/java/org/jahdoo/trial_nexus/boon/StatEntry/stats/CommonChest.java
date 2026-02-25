package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

public class CommonChest extends AbstractStatEntry {
    @Override
    public String id() {
        return "common_chests_looted";
    }

    @Override
    public int colour() {
        return JahdooRarity.COMMON.getColour();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.CHEST_COMMON;
    }

    @Override
    public StatCategory category() {
        return StatCategory.LOOT;
    }
}
