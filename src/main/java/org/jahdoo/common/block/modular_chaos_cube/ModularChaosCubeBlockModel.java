package org.jahdoo.common.block.modular_chaos_cube;

import org.jahdoo.common.items.block_items.ModularChaosCubeItem;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ModularChaosCubeBlockModel extends DefaultedItemGeoModel<ModularChaosCubeItem> {
    public ModularChaosCubeBlockModel() {
        super(Helpers.res("modular_chaos_cube"));
    }
}
