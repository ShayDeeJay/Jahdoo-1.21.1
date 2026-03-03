package org.jahdoo.trial_nexus.trackable.stat_entry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

public class LegendaryChest extends AbstractStatEntry {

    public static final String ID = "legendary_chest";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public int colour() {
        return JahdooRarity.LEGENDARY.getColour();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.CHEST_LEGENDARY;
    }

    @Override
    public StatCategory category() {
        return StatCategory.LOOT;
    }
}
