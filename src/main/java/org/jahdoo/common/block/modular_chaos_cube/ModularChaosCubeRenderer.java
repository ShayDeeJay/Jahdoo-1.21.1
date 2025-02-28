package org.jahdoo.common.block.modular_chaos_cube;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ModularChaosCubeRenderer extends GeoBlockRenderer<ModularChaosCubeEntity>{
    public ModularChaosCubeRenderer(BlockEntityRendererProvider.Context context) {
        super(new ModularChaosCubeModel());
    }
}

