package org.jahdoo.common.block.chaos_cube;

import org.jahdoo.common.items.block_items.ModularChaosCubeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ChaosCubeBlockRenderer extends GeoItemRenderer<ModularChaosCubeItem> {
    public ChaosCubeBlockRenderer() {
        super(new ChaosCubeBlockModel());
    }
}
