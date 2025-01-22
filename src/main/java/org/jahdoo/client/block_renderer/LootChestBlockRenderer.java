package org.jahdoo.client.block_renderer;

import org.jahdoo.client.block_models.LootChestBlockModel;
import org.jahdoo.items.block_items.LootChestBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LootChestBlockRenderer extends GeoItemRenderer<LootChestBlockItem> {

    public LootChestBlockRenderer() {
        super(new LootChestBlockModel());
    }



}
