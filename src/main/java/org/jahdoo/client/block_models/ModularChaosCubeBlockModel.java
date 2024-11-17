package org.jahdoo.client.block_models;

import org.jahdoo.items.block_items.ModularChaosCubeItem;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ModularChaosCubeBlockModel extends DefaultedItemGeoModel<ModularChaosCubeItem> {
    public ModularChaosCubeBlockModel() {
        super(ModHelpers.res("modular_chaos_cube"));
    }
}
