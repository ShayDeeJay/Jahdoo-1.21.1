package org.jahdoo.common.block.loot_chest;

import org.jahdoo.common.items.block_items.LootChestBlockItem;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class LootChestBlockModel extends DefaultedItemGeoModel<LootChestBlockItem> {
    public LootChestBlockModel() {
        super(JahdooHelpers.res("loot_chest"));
    }
}
