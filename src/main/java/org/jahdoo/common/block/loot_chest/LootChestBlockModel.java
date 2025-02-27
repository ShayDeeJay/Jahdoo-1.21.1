package org.jahdoo.common.block.loot_chest;

import org.jahdoo.common.items.block_items.LootChestBlockItem;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class LootChestBlockModel extends DefaultedItemGeoModel<LootChestBlockItem> {
    public LootChestBlockModel() {
        super(Helpers.res("loot_chest"));
    }
}
