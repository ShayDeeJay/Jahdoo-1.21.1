package org.jahdoo.client.entity_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.utils.ModHelpers;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CustomAoeRenderer extends EntityRenderer<AoeCloud> {

    public CustomAoeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }



    @Override
    public void render(AoeCloud entity, float pEntityYaw, float pPartialTick, PoseStack pose, MultiBufferSource bufferSource, int light) {
        super.render(entity, pEntityYaw, pPartialTick, pose, bufferSource, light);
        pose.scale(entity.getRadius(),0.2f, entity.getRadius());
//        pose.pushPose();
//        pose.translate(0,0.12,0);
//        pose.rotateAround(Axis.YN.rotationDegrees(entity.tickCount + pPartialTick), 0,0,0);
//        drawSlash(pose.last(), entity, bufferSource, light, entity.getBbWidth());
//        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AoeCloud pEntity) {
        return ModHelpers.res("textures/entity/magic_circle_2.png");
    }

    private void drawSlash(PoseStack.Pose pose, AoeCloud entity, MultiBufferSource bufferSource, int light, float width) {
        Matrix4f poseMatrix = pose.pose();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        float halfWidth = width * .5f;
        //old color: 125, 0, 10
        consumer.addVertex(poseMatrix, -halfWidth, -.1f, -halfWidth).setColor(45, 169, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, halfWidth, -.1f, -halfWidth).setColor(45, 169, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, halfWidth, -.1f, halfWidth).setColor(45, 169, 255, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, -halfWidth, -.1f, halfWidth).setColor(45, 169, 255   , 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
    }

    @Override
    public boolean shouldRender(AoeCloud livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
