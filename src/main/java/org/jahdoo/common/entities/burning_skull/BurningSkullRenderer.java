package org.jahdoo.common.entities.burning_skull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.Color;

public class BurningSkullRenderer extends GeoEntityRenderer<BurningSkull> {

    public BurningSkullRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BurningSkullModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void preApplyRenderLayers(
        PoseStack poseStack,
        BurningSkull entity,
        BakedGeoModel model,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTicks,
        int packedLight,
        int packedOverlay
    ) {
        poseStack.translate(0,0.1,0);

        super.preApplyRenderLayers(
            poseStack,
            entity,
            model,
            renderType,
            bufferSource,
            buffer,
            partialTicks,
            packedLight,
            packedOverlay
        );
    }

    @Override
    public Color getRenderColor(BurningSkull entity, float partialTick, int packedLight) {
        return super.getRenderColor(entity, partialTick, 255);
    }

    @Override
    protected void applyRotations(
        BurningSkull entity,
        PoseStack poseStack,
        float ageInTicks,
        float rotationYaw,
        float partialTick,
        float nativeScale
    ) {
        var delta = entity.getDeltaMovement();

        var travelYaw = (float) Math.atan2(delta.z, delta.x);
        travelYaw = (float) Math.toDegrees(travelYaw) + 90;

        var travelPitch = (float) Math.atan2(delta.y, Math.sqrt(delta.x * delta.x + delta.z * delta.z));
        travelPitch = (float) Math.toDegrees(travelPitch);

        poseStack.mulPose(Axis.YP.rotationDegrees(-travelYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(travelPitch));
    }

}
