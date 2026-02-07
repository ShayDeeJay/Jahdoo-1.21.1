package org.jahdoo.common.block.chaos_cube;

import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ChaosCubeModel extends DefaultedBlockGeoModel<ChaosCubeEntity> {
    public ChaosCubeModel() {
        super(JahdooHelpers.res("modular_chaos_cube"));
    }
}
