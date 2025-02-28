package org.jahdoo.common.block.wand;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WandBlockRenderer extends GeoBlockRenderer<WandBlockEntity>{
    public WandBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WandBlockModel());
    }
}

