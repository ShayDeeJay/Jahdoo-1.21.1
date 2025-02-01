package org.jahdoo.client.entity_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jahdoo.client.entity_models.FlamingSkullModel;
import org.jahdoo.entities.BurningSkull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.Color;

public class FlamingSkullRenderer extends GeoEntityRenderer<BurningSkull> {
    public FlamingSkullRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FlamingSkullModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void preApplyRenderLayers(PoseStack poseStack, BurningSkull entity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTicks, int packedLight, int packedOverlay) {
        poseStack.translate(0,0.1,0);
        super.preApplyRenderLayers(poseStack, entity, model, renderType, bufferSource, buffer, partialTicks, packedLight, packedOverlay);
    }

    @Override
    protected void applyRotations(BurningSkull animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        var deltaMovement = animatable.getDeltaMovement();

        var travelYaw = (float) Math.atan2(deltaMovement.z, deltaMovement.x);
        travelYaw = (float) Math.toDegrees(travelYaw) + 90;

        var travelPitch = (float) Math.atan2(deltaMovement.y, Math.sqrt(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z));
        travelPitch = (float) Math.toDegrees(travelPitch);

        poseStack.mulPose(Axis.YP.rotationDegrees(-travelYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(travelPitch));
    }

    @Override
    public Color getRenderColor(BurningSkull animatable, float partialTick, int packedLight) {
        packedLight = 255;
        return super.getRenderColor(animatable, partialTick, packedLight);
    }

}
