package org.jahdoo.common.block.modular_chaos_cube;

import org.jahdoo.common.items.block_items.ModularChaosCubeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ModularChaosCubeBlockRenderer extends GeoItemRenderer<ModularChaosCubeItem> {
    public ModularChaosCubeBlockRenderer() {
        super(new ModularChaosCubeBlockModel());
    }
}
