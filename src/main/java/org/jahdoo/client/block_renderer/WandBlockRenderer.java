package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.block_models.WandBlockModel;
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

