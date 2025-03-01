package org.jahdoo.common.block.wand;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WandBlockRenderer extends GeoBlockRenderer<WandBlockEntity>{

    public WandBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WandBlockModel());
    }

}

