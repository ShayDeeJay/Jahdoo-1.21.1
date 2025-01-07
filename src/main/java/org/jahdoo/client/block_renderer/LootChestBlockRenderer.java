package org.jahdoo.client.block_renderer;

import org.jahdoo.client.block_models.ChallengeAltarBlockModel;
import org.jahdoo.client.block_models.LootChestBlockModel;
import org.jahdoo.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.items.block_items.LootChestItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LootChestBlockRenderer extends GeoItemRenderer<LootChestItem> {

    public LootChestBlockRenderer() {
        super(new LootChestBlockModel());
    }

}
