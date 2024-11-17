package org.jahdoo.client.block_renderer;

import org.jahdoo.client.block_models.ModularChaosCubeBlockModel;
import org.jahdoo.items.block_items.ModularChaosCubeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ModularChaosCubeBlockRenderer extends GeoItemRenderer<ModularChaosCubeItem> {
    public ModularChaosCubeBlockRenderer() {
        super(new ModularChaosCubeBlockModel());
    }
}
