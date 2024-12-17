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
import net.minecraft.util.FastColor;
import org.jahdoo.ability.all_abilities.abilities.raw_abilities.Armageddon;
import org.jahdoo.ability.all_abilities.abilities.raw_abilities.Permafrost;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.joml.Matrix4f;

import java.util.Objects;

import static org.jahdoo.client.RenderHelpers.drawTexture;

public class CustomAoeRenderer extends EntityRenderer<AoeCloud> {

    public CustomAoeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(AoeCloud entity, float pEntityYaw, float pPartialTick, PoseStack pose, MultiBufferSource bufferSource, int light) {
        super.render(entity, pEntityYaw, pPartialTick, pose, bufferSource, light);
        pose.scale(entity.getRadius(),0.2f, entity.getRadius());
        pose.pushPose();
        pose.translate(0,0.12,0);
        pose.rotateAround(Axis.YN.rotationDegrees((entity.tickCount * 1.4f) + pPartialTick), 0,0,0);

        if(Objects.equals(entity.getEntityType(), Armageddon.abilityId.getPath().intern())){
            int color = ElementRegistry.INFERNO.get().textColourSecondary();
            drawTexture(pose.last(),bufferSource, 255, Math.min(entity.getBbWidth() + 0.4f, entity.tickCount + pPartialTick), getTextureLocation(entity), FastColor.ARGB32.color(155, color));
        }

        if(Objects.equals(entity.getEntityType(), Permafrost.abilityId.getPath().intern())){
            int color = ElementRegistry.FROST.get().textColourPrimary();
            drawTexture(pose.last(),bufferSource, 255, Math.min(entity.getBbWidth() + 0.4f,  entity.tickCount + pPartialTick), getTextureLocation(entity), FastColor.ARGB32.color(90, color));
        }

//        drawSlash(pose.last(), bufferSource, 255, Math.min(entity.getBbWidth() + 0.2f, entity.tickCount + pPartialTick), ModHelpers.res("textures/entity/magic_circle_2.png"), FastColor.ARGB32.color(155, color));
        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AoeCloud pEntity) {
        return ModHelpers.res("textures/entity/shield.png");
    }

    @Override
    public boolean shouldRender(AoeCloud livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
