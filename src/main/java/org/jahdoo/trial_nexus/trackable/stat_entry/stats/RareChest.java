package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

public class RareChest extends AbstractStatEntry {

    public static final String ID = "rare_chest";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public int colour() {
        return JahdooRarity.RARE.getColour();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.CHEST_RARE;
    }

    @Override
    public StatCategory category() {
        return StatCategory.LOOT;
    }
}
