package org.jahdoo.trial_nexus.boon.StatEntry.stats;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.StatEntry.AbstractStatEntry;
import org.jahdoo.trial_nexus.boon.StatEntry.StatCategory;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

public class LegendaryChest extends AbstractStatEntry {
    @Override
    public String id() {
        return "legenday_chests_looted";
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
