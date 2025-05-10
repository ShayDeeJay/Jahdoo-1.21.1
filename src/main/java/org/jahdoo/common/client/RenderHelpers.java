package org.jahdoo.common.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

import java.awt.*;

public class RenderHelpers {

    public static void drawTexture(PoseStack.Pose pose, MultiBufferSource bufferSource, int light, float width, ResourceLocation texture, int colour) {
        var poseMatrix = pose.pose();
        var consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(texture));
        var halfWidth = width * 0.5f;
        consumer.addVertex(poseMatrix, -halfWidth, -.1f, -halfWidth).setColor(colour).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, halfWidth, -.1f, -halfWidth).setColor(colour).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, halfWidth, -.1f, halfWidth).setColor(colour).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, -halfWidth, -.1f, halfWidth).setColor(colour).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
    }

    public static void drawHealthBar(PoseStack.Pose pose, MultiBufferSource bufferSource, float health, float maxHealth) {

        float healthPercent = health / maxHealth;
        var poseMatrix = pose.pose();

        var width = 3;
        float halfWidth = width * 0.5f;
        float halfHeight = 0.5F * 0.5f;

        // --- Draw container background ---
        var containerConsumer = bufferSource.getBuffer(RenderType.entityCutout(Icons.HEALTH_HOLDER));
        float containerExpand = 0.25f; // optional extra padding
        float bgHalfWidth = halfWidth + containerExpand;
        float bgHalfHeight = halfHeight + containerExpand;

        var packedLight = 255;
        containerConsumer.addVertex(poseMatrix, -bgHalfWidth, -.1f, -bgHalfHeight).setColor(1F, 1F, 1F, 1F).setUv(0f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        containerConsumer.addVertex(poseMatrix, bgHalfWidth, -.1f, -bgHalfHeight).setColor(1F, 1F, 1F, 1F).setUv(1f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        containerConsumer.addVertex(poseMatrix, bgHalfWidth, -.1f, bgHalfHeight).setColor(1F, 1F, 1F, 1F).setUv(1f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        containerConsumer.addVertex(poseMatrix, -bgHalfWidth, -.1f, bgHalfHeight).setColor(1F, 1F, 1F, 1F).setUv(0f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);

        // --- Draw health bar ---
        healthPercent = Mth.clamp(healthPercent, 0f, 1f);
        float barWidth = width * healthPercent; // Health bar width based on health percentage

        float left = -halfWidth;
        float right = left + barWidth;

        // Adjust UV mapping based on health percentage
        float uMax = healthPercent;

        var healthConsumer = bufferSource.getBuffer(RenderType.entityCutout(Icons.HEALTH_BAR));

        // Adjust the Y position (height) of the health bar
        var withAdjust = halfHeight + 0.25F;

        // Slight Z-offset (0.01f) to move the health bar forward and resolve Z-fighting
        float zOffset = -0.0001f;

        // Draw health bar without tint (using the original texture color)
        healthConsumer.addVertex(poseMatrix, left, -.1f + zOffset, -withAdjust).setColor(1F, 1F, 1F, 1F).setUv(0f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        healthConsumer.addVertex(poseMatrix, right, -.1f + zOffset, -withAdjust).setColor(1F, 1F, 1F, 1F).setUv(uMax, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        healthConsumer.addVertex(poseMatrix, right, -.1f + zOffset, withAdjust).setColor(1F, 1F, 1F, 1F).setUv(uMax, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        healthConsumer.addVertex(poseMatrix, left, -.1f + zOffset, withAdjust).setColor(1F, 1F, 1F, 1F).setUv(0f, 0f)
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
