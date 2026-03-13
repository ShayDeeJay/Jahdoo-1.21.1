package org.jahdoo.common.entities.aoe_cloud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.magic.abilities_combat.armageddon.Armageddon;
import org.jahdoo.trial_nexus.magic.abilities_combat.life_siphon.LifeSiphonNova;
import org.jahdoo.trial_nexus.magic.abilities_combat.permafrost.Permafrost;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;
import static org.jahdoo.common.registers.mod.ElementReg.*;
import static org.jahdoo.trial_nexus.attachments.player_abilities.PhantomJump.PHANTOM_JUMP;

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
        pose.translate(0,0.1,0);
        pose.mulPose(Axis.XP.rotationDegrees(180));

        type(entity, partialTicks, pose, bufferSource, Armageddon.abilityId.getPath().intern(), color(255, inferno().partColourB()));

        type(entity, partialTicks, pose, bufferSource, LifeSiphonNova.abilityId.getPath().intern(), color(255, vitality().textColourB()));

        type(entity, partialTicks, pose, bufferSource, Permafrost.abilityId.getPath().intern(), color(255, frost().partColourA()));

        fromId(entity.getElementId()).ifPresent(
            s -> {
                type(entity, partialTicks, pose, bufferSource, PHANTOM_JUMP, s.partColourB());
                type(entity, partialTicks, pose, bufferSource, "blink", s.partColourB());
            }
        );

        pose.popPose();

    }

    private void type(AoeCloud entity, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, String name, int color) {

        if(Objects.equals(entity.getEntityType(), name)){
            var isCircle = Objects.equals(entity.getEntityType(), PHANTOM_JUMP);

            pose.pushPose();
            pose.translate(0,0,0);
            drawTexture(
                pose.last(),
                bufferSource,
                255,
                min(entity.getBbWidth() + 0.4f, entity.tickCount + partialTicks),
                getTextureLocation(entity), color(155, color)
            );
            pose.popPose();

            var x = TextureForAOE.getAsMap().get(entity.getEntityType());

            var y = 1;
            var ext = -0.01;
            for (var s : x) {
                pose.pushPose();
                pose.translate(0, ext, 0);
                getMagicCircle(entity, partialTicks, pose, bufferSource, color, y, s);
                y++;
                ext-=0.01;
                pose.popPose();
            }

        }
    }

    public enum TextureForAOE {
        PHANTOM_JUMP_RES(List.of("a", "b", "d"), PHANTOM_JUMP),
        PERMAFROST_RES(List.of("a", "b", "e"), Permafrost.abilityId.getPath()),
        ARMAGEDDON_RES(List.of("a", "b", "c"), Armageddon.abilityId.getPath()),
        LIFE_SIPHON_RES(List.of("b", "c"), LifeSiphonNova.abilityId.getPath());

        final List<String> type;
        final String name;

        TextureForAOE(List<String> type, String name) {
            this.type = type;
            this.name = name;
        }

        public static Map<String, List<String>> getAsMap(){
            return Arrays.stream(TextureForAOE.values()).collect(Collectors.toMap(
                e -> e.name,
                e -> e.type
            ));
        }
    }

    private static void getMagicCircle(AoeCloud entity, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, int color, int index, String type) {
        pose.pushPose();
        var v1 = entity.tickCount + partialTicks;
        var degrees = v1 * 1.2f;
        var isCircle = entity.getEntityType().equals(PHANTOM_JUMP);

        var isType = type.equals("c") || type.equals("b");
        var b = entity.getEntityType().equals(Armageddon.abilityId.getPath().intern()) && isType;
        var i = isCircle ? Math.max(10, 200 - (entity.tickCount * 14)) : 1;
        var v = index % 2 == 0 ? degrees : -degrees;

        pose.rotateAround(Axis.YN.rotationDegrees(v * i), 0, 0, 0);

        if(isCircle){
            drawTexture(
                pose.last(),
                bufferSource,
                255,
                min(entity.getBbWidth() + 0.4f, v1),
                JahdooHelpers.res("textures/entity/shield.png"), color(155, color)
            );
        }

        drawTexture(
            pose.last(),
            bufferSource,
            255,
            min(entity.getBbWidth(), v1),
            JahdooHelpers.res("textures/entity/" + type + ".png"), color
        );

        pose.popPose();
    }

}
