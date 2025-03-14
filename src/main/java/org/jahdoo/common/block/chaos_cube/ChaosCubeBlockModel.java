package org.jahdoo.common.block.chaos_cube;

import org.jahdoo.common.items.block_items.ModularChaosCubeItem;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ChaosCubeBlockModel extends DefaultedItemGeoModel<ModularChaosCubeItem> {
    public ChaosCubeBlockModel() {
        super(Helpers.res("modular_chaos_cube"));
    }
}
