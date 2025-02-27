package org.jahdoo.common.block.wand;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WandBlockRenderer extends GeoBlockRenderer<WandBlockEntity>{
    public WandBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WandBlockModel());
    }

    @Override
    public void render(WandBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}

