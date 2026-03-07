package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

public class CommonChest extends AbstractStatEntry {

    public static final String ID = "common_chest";

    @Override
    public String id() {
        return ID;
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
