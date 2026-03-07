package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

public class MythicChest extends AbstractStatEntry {

    public static final String ID = "mythic_chest";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public int colour() {
        return JahdooRarity.MYTHIC.getColour();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.CHEST_MYTHIC;
    }

    @Override
    public StatCategory category() {
        return StatCategory.LOOT;
    }
}
