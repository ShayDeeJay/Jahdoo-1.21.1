package org.jahdoo.client.block_models;

import org.jahdoo.items.block_items.LootChestBlockItem;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class LootChestBlockModel extends DefaultedItemGeoModel<LootChestBlockItem> {
    public LootChestBlockModel() {
        super(ModHelpers.res("loot_chest"));
    }
}
