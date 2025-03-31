package org.jahdoo.common.entities.aoe_cloud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.ability.abilities_combat.life_siphon.LifeSiphonNova;
import org.jahdoo.ascension.ability.abilities_combat.mob_abilities.Barrage;
import org.jahdoo.ascension.ability.abilities_combat.armageddon.Armageddon;
import org.jahdoo.ascension.ability.abilities_combat.permafrost.Permafrost;
import org.jahdoo.ascension.utils.Helpers;

import java.util.Objects;

import static java.lang.Math.*;
import static net.minecraft.util.FastColor.ARGB32.*;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;
import static org.jahdoo.common.registers.ElementReg.*;

public class AoeCloudRenderer extends EntityRenderer<AoeCloud> {

    public AoeCloudRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AoeCloud entity) {
        return Helpers.res("textures/entity/shield.png");
    }

    @Override
    public boolean shouldRender(
        AoeCloud livingEntity,
        Frustum camera,
        double camX,
        double camY,
        double camZ
    ) {
        return true;
    }

    @Override
    public void render(AoeCloud entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, int light) {
        super.render(entity, yaw, partialTicks, pose, bufferSource, light);

        pose.scale(entity.getRadius(),0.2f, entity.getRadius());
        pose.pushPose();
        pose.translate(0,0.12,0);
        pose.rotateAround(Axis.YN.rotationDegrees((entity.tickCount * 1.4f) + partialTicks), 0,0,0);

        type(entity, partialTicks, pose, bufferSource, Armageddon.abilityId.getPath().intern(), color(155, inferno().textColourB()));

        type(entity, partialTicks, pose, bufferSource, LifeSiphonNova.abilityId.getPath().intern(), color(155, vitality().textColourB()));

        type(entity, partialTicks, pose, bufferSource, Permafrost.abilityId.getPath().intern(), color(155, frost().textColourB()));

        type(entity, partialTicks, pose, bufferSource, Barrage.abilityId.getPath().intern(), color(155, -1));

        pose.popPose();

    }

    private void type(AoeCloud entity, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, String name, int color) {
        if(Objects.equals(entity.getEntityType(), name)){
            drawTexture(
                pose.last(),
                bufferSource,
                255,
                min(entity.getBbWidth() + 0.4f,  entity.tickCount + partialTicks),
                getTextureLocation(entity), color
            );
        }
    }

}
