package org.jahdoo.common.block.loot_chest;

import org.jahdoo.common.items.block_items.LootChestBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LootChestBlockRenderer extends GeoItemRenderer<LootChestBlockItem> {

    public LootChestBlockRenderer() {
        super(new LootChestBlockModel());
    }



}
