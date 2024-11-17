package org.jahdoo.client.block_models;

import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ModularChaosCubeModel extends DefaultedBlockGeoModel<ModularChaosCubeEntity> {
    public ModularChaosCubeModel() {
        super(ModHelpers.res("modular_chaos_cube"));
    }
}
