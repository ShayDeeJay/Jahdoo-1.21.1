package org.jahdoo.common.entities.inferno_creeper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GlowLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public GlowLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        var vertexconsumer = buffer.getBuffer(this.renderType());
        this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 50, OverlayTexture.NO_OVERLAY);
    }

    public abstract RenderType renderType();
}