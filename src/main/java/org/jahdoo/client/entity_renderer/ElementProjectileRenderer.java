package org.jahdoo.client.entity_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.client.entity_models.ElementProjectileModel;
import org.jahdoo.entities.ElementProjectile;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.Color;

import javax.annotation.Nullable;

import static org.jahdoo.entities.ProjectileAnimations.SEMTEX;

public class ElementProjectileRenderer extends GeoEntityRenderer<ElementProjectile> {
    public ElementProjectileRenderer(EntityRendererProvider.Context renderManager, ResourceLocation resourceLocation) {
        super(renderManager, new ElementProjectileModel(resourceLocation));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void preApplyRenderLayers(PoseStack poseStack, ElementProjectile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        poseStack.translate(0.0f, -0.55f, 0.0f);
        poseStack.scale(1f,1f,1f);
        if(animatable.isInvisible()) poseStack.scale(0,0,0);
        if(animatable.predicateType() == 1) {
            poseStack.translate(0.0f, -0.35f, 0.0f);
            poseStack.scale(1.5f, 1.5f, 1.5f);
        }
    }

    @Override
    public RenderType getRenderType(ElementProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entitySolid(texture);
    }

    @Override
    public Color getRenderColor(ElementProjectile animatable, float partialTick, int packedLight) {
        packedLight = 255;
        return super.getRenderColor(animatable, partialTick, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ElementProjectile animatable) {
        return super.getTextureLocation(animatable);
    }
}
