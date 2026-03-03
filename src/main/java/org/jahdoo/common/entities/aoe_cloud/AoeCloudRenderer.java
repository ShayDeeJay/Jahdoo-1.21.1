package org.jahdoo.common.entities.aoe_cloud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.ability.abilities_combat.armageddon.Armageddon;
import org.jahdoo.trial_nexus.ability.abilities_combat.life_siphon.LifeSiphonNova;
import org.jahdoo.trial_nexus.ability.abilities_combat.permafrost.Permafrost;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.min;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;
import static org.jahdoo.common.registers.mod.ElementReg.*;

public class AoeCloudRenderer extends EntityRenderer<AoeCloud> {

    public AoeCloudRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AoeCloud entity) {
        return JahdooHelpers.res("textures/entity/shield.png");
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

        pose.scale(entity.getRadius(), 0.2f, entity.getRadius());
        pose.pushPose();
        pose.translate(0,0.12,0);

        type(entity, partialTicks, pose, bufferSource, Armageddon.abilityId.getPath().intern(), color(255, inferno().partColourB()));

        type(entity, partialTicks, pose, bufferSource, LifeSiphonNova.abilityId.getPath().intern(), color(255, vitality().textColourB()));

        type(entity, partialTicks, pose, bufferSource, Permafrost.abilityId.getPath().intern(), color(255, frost().partColourA()));

        fromId(entity.getElementId()).ifPresent(
            s -> {
                type(entity, partialTicks, pose, bufferSource, "jump_circle", s.partColourB());
                type(entity, partialTicks, pose, bufferSource, "blink", s.partColourB());
            }
        );

        pose.popPose();

    }

    private void type(AoeCloud entity, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, String name, int color) {
        var isCirc = "jump_circle";

        if(Objects.equals(entity.getEntityType(), name)){
            var circ = Objects.equals(entity.getEntityType(), isCirc);
            drawTexture(
                pose.last(),
                bufferSource,
                255,
                min(entity.getBbWidth() + 0.4f, entity.tickCount + partialTicks),
                getTextureLocation(entity), color(155, color)
            );

            var x = new ArrayList<String>();

            if(circ) {
                x.add("a");
                x.add("b");
                x.add("d");
            }

            if(entity.getEntityType().equals(Permafrost.abilityId.getPath().intern())) {
                x.add("a");
                x.add("c");
                x.add("e");
            }

            if(entity.getEntityType().equals(Armageddon.abilityId.getPath().intern())) {
                x.add("a");
                x.add("b");
//                x.add("c");
//                x.add("d");
//                x.add("e");
            }

            if(entity.getEntityType().equals(LifeSiphonNova.abilityId.getPath().intern())) {
//                x.add("a");
                x.add("b");
                x.add("c");
//                x.add("d");
//                x.add("e");
            }

            System.out.println(entity.getEntityType());
            if(Objects.equals(entity.getEntityType(), "blink")){
                x.add("a");
                x.add("b");
//                x.add("c");
//                x.add("d");
                x.add("e");
            }

            var y = 1;
            for (var s : x) {
                getMagicCircle(entity, partialTicks, pose, bufferSource, color, y, s);
                y++;
            }

        }
    }

    private static void getMagicCircle(AoeCloud entity, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, int color, int index, String type) {
        pose.pushPose();
        var degrees = (entity.tickCount * 1.2f) + partialTicks;

        System.out.println(index);
        pose.rotateAround(Axis.YN.rotationDegrees((index%2 == 0) ? degrees : -degrees), 0, 0, 0);
        drawTexture(
            pose.last(),
            bufferSource,
            255,
            min(entity.getBbWidth(),  entity.tickCount + partialTicks),
            JahdooHelpers.res("textures/entity/" + type + ".png"), color
        );
        pose.popPose();
    }

}
