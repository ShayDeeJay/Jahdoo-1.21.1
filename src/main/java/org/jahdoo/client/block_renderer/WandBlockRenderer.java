package org.jahdoo.client.block_renderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.block_models.WandBlockModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WandBlockRenderer extends GeoBlockRenderer<WandBlockEntity>{
    public WandBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WandBlockModel());
    }

}

