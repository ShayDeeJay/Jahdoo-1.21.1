package org.jahdoo.client.block_models;

import org.jahdoo.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.items.block_items.LootChestItem;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class LootChestBlockModel extends DefaultedItemGeoModel<LootChestItem> {
    public LootChestBlockModel() {
        super(ModHelpers.res("loot_chest"));
    }
}
