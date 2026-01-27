package org.jahdoo.common.block.mystical_augmenter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MysticalAugmenterRenderer extends GeoBlockRenderer<MysticalAugmenterEntity>{
    public MysticalAugmenterRenderer(BlockEntityRendererProvider.Context context) {
        super(new MysticalAugmenterModel());
    }

    @Override
    public void postRender(PoseStack poseStack, MysticalAugmenterEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

    }
}

