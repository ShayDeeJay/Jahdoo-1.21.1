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
//        poseStack.pushPose();
//        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
//        var level = Minecraft.getInstance().level;
//        if(level == null) return;
//
//        var getCurrentTime = level.getGameTime() + partialTick;
//        var block = animatable.getLevel().getBlockState(animatable.getBlockPos().relative(animatable.getBlockState().getValue(FACING))).getBlock();
//        var itemStack = new ItemStack(block.asItem());
//        var scaleItem = 0.5F;
//
//        var scale = Math.sin(((level.getGameTime() + partialTick) / 14.0F)) * 0.02F;
//        poseStack.translate(0, 1f + scale, 0);
//        poseStack.scale(scaleItem, scaleItem, scaleItem);
//        poseStack.mulPose(Axis.YP.rotationDegrees(getCurrentTime));
//
//        itemRenderer.renderStatic(
//            itemStack,
//            ItemDisplayContext.FIXED,
//            packedLight,
//            OverlayTexture.NO_OVERLAY,
//            poseStack,
//            bufferSource,
//            animatable.getLevel(),
//            1
//        );
//
//        poseStack.popPose();
    }
}

