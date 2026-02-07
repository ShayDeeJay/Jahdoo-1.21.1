package org.jahdoo.common.block.chaos_cube;

import org.jahdoo.common.items.block_items.ModularChaosCubeItem;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ChaosCubeBlockModel extends DefaultedItemGeoModel<ModularChaosCubeItem> {
    public ChaosCubeBlockModel() {
        super(JahdooHelpers.res("modular_chaos_cube"));
    }
}
