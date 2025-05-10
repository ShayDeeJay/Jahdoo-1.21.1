package org.jahdoo.common.entities.safe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static org.jahdoo.common.client.RenderHelpers.drawHealthBar;

public class SafeRenderer extends GeoEntityRenderer<Safe> {

    public SafeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SafeModel());
    }

    @Override
    public boolean shouldShowName(Safe animatable) {
        return true;
    }

    @Override
    public void render(Safe entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void renderNameTag(Safe entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
//        super.renderNameTag(entity, displayName, poseStack, bufferSource, packedLight, partialTick);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, Safe animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
        var x = 0.8F;
        poseStack.scale(x, x, x);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();

        var z = 0.5F;
        poseStack.pushPose();
        poseStack.translate(0, animatable.getBbHeight()+0.5, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.XP.rotation(-1.5f));
        poseStack.scale(z, z, z);
        drawHealthBar(
            poseStack.last(), bufferSource, animatable.getDamageRequired() - animatable.getDamageCounter(), animatable.getDamageRequired()
        );
        poseStack.popPose();
    }
}
