package org.jahdoo.common.block.chaos_cube;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ChaosCubeRenderer extends GeoBlockRenderer<ChaosCubeEntity>{
    public ChaosCubeRenderer(BlockEntityRendererProvider.Context context) {
        super(new ChaosCubeModel());
    }
}

