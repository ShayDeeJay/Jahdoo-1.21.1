package org.jahdoo.client.entity_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.entities.AoeCloud;

public class CustomAoeRenderer extends EntityRenderer<AoeCloud> {


    public CustomAoeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(AoeCloud pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.scale(pEntity.getRadius(),0.2f,pEntity.getRadius());
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AoeCloud pEntity) {
        return null;
    }
}
