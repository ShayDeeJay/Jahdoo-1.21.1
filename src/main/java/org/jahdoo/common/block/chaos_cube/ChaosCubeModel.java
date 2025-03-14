package org.jahdoo.common.block.chaos_cube;

import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ChaosCubeModel extends DefaultedBlockGeoModel<ChaosCubeEntity> {
    public ChaosCubeModel() {
        super(Helpers.res("modular_chaos_cube"));
    }
}
