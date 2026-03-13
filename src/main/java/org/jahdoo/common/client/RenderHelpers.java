package org.jahdoo.common.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jahdoo.trial_nexus.utils.Icons;

import java.awt.*;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;

public class RenderHelpers {

    public static void drawTexture(PoseStack.Pose pose, MultiBufferSource bufferSource, int light, float width, ResourceLocation texture, int colour) {
        var poseMatrix = pose.pose();
//        var consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(texture, false));
        var consumer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(texture));
//        var consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(texture));
        var halfWidth = width * 0.5f;

        consumer.addVertex(poseMatrix, -halfWidth, 0.05f, -halfWidth).setColor(colour).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, halfWidth, 0.05f, -halfWidth).setColor(colour).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, halfWidth, 0.05f, halfWidth).setColor(colour).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, -halfWidth, 0.05f, halfWidth).setColor(colour).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
    }

    public static void drawHealthBar(PoseStack.Pose pose, MultiBufferSource bufferSource, float health, float maxHealth, ResourceLocation holder) {

        var healthPercent = Mth.clamp(health / maxHealth, 0f, 1f);
        var poseMatrix = pose.pose();
        var packedLight = FULL_BRIGHT;

        // --- Define logical sizes (in world units) ---
        var containerWidth = 3.5f;
        var containerHeight = 0.5f;
        var barWidth = containerWidth - 0.25F;
        var barHeight = containerHeight/2;

        var containerHalfWidth = containerWidth / 2f;
        var containerHalfHeight = containerHeight / 2f;

        // --- Draw container background ---
        var containerConsumer = bufferSource.getBuffer(RenderType.entityCutout(holder));
        containerConsumer.addVertex(poseMatrix, -containerHalfWidth, -.1f, -containerHalfHeight)
            .setColor(1F, 1F, 1F, 1F).setUv(0f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        containerConsumer.addVertex(poseMatrix, containerHalfWidth, -.1f, -containerHalfHeight)
            .setColor(1F, 1F, 1F, 1F).setUv(1f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        containerConsumer.addVertex(poseMatrix, containerHalfWidth, -.1f, containerHalfHeight)
            .setColor(1F, 1F, 1F, 1F).setUv(1f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        containerConsumer.addVertex(poseMatrix, -containerHalfWidth, -.1f, containerHalfHeight)
            .setColor(1F, 1F, 1F, 1F).setUv(0f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);

        // --- Draw health bar ---
        var barConsumer = bufferSource.getBuffer(RenderType.entityCutout(Icons.HEALTH_BAR));
        var barHalfHeight = barHeight / 2f;
        var barActualWidth = barWidth * healthPercent;
        var barLeft = -barWidth / 2f;
        var barRight = barLeft + barActualWidth;
        var zOffset = -0.0005f;
        var yBase = -.1f;

        barConsumer.addVertex(poseMatrix, barLeft, yBase + zOffset, -barHalfHeight)
            .setColor(1F, 1F, 1F, 1F).setUv(0f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        barConsumer.addVertex(poseMatrix, barRight, yBase + zOffset, -barHalfHeight)
            .setColor(1F, 1F, 1F, 1F).setUv(healthPercent, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        barConsumer.addVertex(poseMatrix, barRight, yBase + zOffset, barHalfHeight)
            .setColor(1F, 1F, 1F, 1F).setUv(healthPercent, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        barConsumer.addVertex(poseMatrix, barLeft, yBase + zOffset, barHalfHeight)
            .setColor(1F, 1F, 1F, 1F).setUv(0f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
    }


    public static void renderLines(PoseStack matrix, AABB aabb, Color color, MultiBufferSource buffer) {
        var x = (float) aabb.minX;
        var y = (float) aabb.minY;
        var z = (float) aabb.minZ;
        var dx = (float) aabb.maxX;
        var dy = (float) aabb.maxY;
        var dz = (float) aabb.maxZ;
        var builder = buffer.getBuffer(RenderType.lines());
        matrix.pushPose();
        var matrix4f = matrix.last().pose();
        var matrix3f = matrix.last();
        int colorRGB = color.getRGB();

        builder.addVertex(matrix4f, x, y, z).setColor(colorRGB).setNormal(matrix3f, 1.0F, 0.0F, 0.0F);
        builder.addVertex(matrix4f, dx, y, z).setColor(colorRGB).setNormal(matrix3f, 1.0F, 0.0F, 0.0F);
        builder.addVertex(matrix4f, x, y, z).setColor(colorRGB).setNormal(matrix3f, 0.0F, 1.0F, 0.0F);
        builder.addVertex(matrix4f, x, dy, z).setColor(colorRGB).setNormal(matrix3f, 0.0F, 1.0F, 0.0F);

        builder.addVertex(matrix4f, x, y, z).setColor(colorRGB).setNormal(matrix3f, 0.0F, 0.0F, 1.0F);
        builder.addVertex(matrix4f, x, y, dz).setColor(colorRGB).setNormal(matrix3f, 0.0F, 0.0F, 1.0F);
        builder.addVertex(matrix4f, dx, y, z).setColor(colorRGB).setNormal(matrix3f, 0.0F, 1.0F, 0.0F);
        builder.addVertex(matrix4f, dx, dy, z).setColor(colorRGB).setNormal(matrix3f, 0.0F, 1.0F, 0.0F);

        builder.addVertex(matrix4f, dx, dy, z).setColor(colorRGB).setNormal(matrix3f, -1.0F, 0.0F, 0.0F);
        builder.addVertex(matrix4f, x, dy, z).setColor(colorRGB).setNormal(matrix3f, -1.0F, 0.0F, 0.0F);
        builder.addVertex(matrix4f, x, dy, z).setColor(colorRGB).setNormal(matrix3f, 0.0F, 0.0F, 1.0F);
        builder.addVertex(matrix4f, x, dy, dz).setColor(colorRGB).setNormal(matrix3f, 0.0F, 0.0F, 1.0F);

        builder.addVertex(matrix4f, x, dy, dz).setColor(colorRGB).setNormal(matrix3f, 0.0F, -1.0F, 0.0F);
        builder.addVertex(matrix4f, x, y, dz).setColor(colorRGB).setNormal(matrix3f, 0.0F, -1.0F, 0.0F);
        builder.addVertex(matrix4f, x, y, dz).setColor(colorRGB).setNormal(matrix3f, 1.0F, 0.0F, 0.0F);
        builder.addVertex(matrix4f, dx, y, dz).setColor(colorRGB).setNormal(matrix3f, 1.0F, 0.0F, 0.0F);

        builder.addVertex(matrix4f, dx, y, dz).setColor(colorRGB).setNormal(matrix3f, 0.0F, 0.0F, -1.0F);
        builder.addVertex(matrix4f, dx, y, z).setColor(colorRGB).setNormal(matrix3f, 0.0F, 0.0F, -1.0F);
        builder.addVertex(matrix4f, x, dy, dz).setColor(colorRGB).setNormal(matrix3f, 1.0F, 0.0F, 0.0F);
        builder.addVertex(matrix4f, dx, dy, dz).setColor(colorRGB).setNormal(matrix3f, 1.0F, 0.0F, 0.0F);

        builder.addVertex(matrix4f, dx, y, dz).setColor(colorRGB).setNormal(matrix3f, 0.0F, 1.0F, 0.0F);
        builder.addVertex(matrix4f, dx, dy, dz).setColor(colorRGB).setNormal(matrix3f, 0.0F, 1.0F, 0.0F);
        builder.addVertex(matrix4f, dx, dy, z).setColor(colorRGB).setNormal(matrix3f, 0.0F, 0.0F, 1.0F);
        builder.addVertex(matrix4f, dx, dy, dz).setColor(colorRGB).setNormal(matrix3f, 0.0F, 0.0F, 1.0F);

        matrix.popPose();
    }

}
