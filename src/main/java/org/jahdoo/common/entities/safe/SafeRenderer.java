package org.jahdoo.common.entities.safe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.Maths;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static org.jahdoo.common.client.RenderHelpers.drawHealthBar;

public class SafeRenderer extends GeoEntityRenderer<Safe> {

    public SafeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SafeModel());
    }

    @Override
    public boolean shouldShowName(Safe animatable) {
        return true;
    }

    @Override
    public void render(Safe entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void renderNameTag(Safe entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
//        super.renderNameTag(entity, displayName, poseStack, bufferSource, packedLight, partialTick);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, Safe animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
        var x = 0.8F;
        poseStack.scale(x, x, x);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();

        if(animatable.getCurrentState() < 2){
            var z = 0.5F;
            poseStack.pushPose();
            poseStack.translate(0, animatable.getBbHeight() + 0.5, 0);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Axis.XP.rotation(-1.5f));
            poseStack.scale(z, z, z);
            drawHealthBar(poseStack.last(), bufferSource, animatable.getDamageRequired() - animatable.getDamageCounter(), animatable.getDamageRequired(), Icons.LOOT_POT_ICON);
            poseStack.popPose();

            roomData(animatable, poseStack, bufferSource, entityRenderDispatcher, partialTick);
        }
    }

    public static void roomData(Safe entity, PoseStack pPoseStack, MultiBufferSource bufferSource, EntityRenderDispatcher dispatcher, float partialTicks) {
        var offWhite = ColourStore.NEGATIVE_RED;
        var displayName = Helpers.withStyleComponent(Maths.ticksToTime(String.valueOf(entity.getTimer())), offWhite);
        pPoseStack.pushPose();
        pPoseStack.translate(0, entity.getBbHeight() - 2, 0);

        var scale = Math.sin(((entity.tickCount + partialTicks) / 10.0F)) * 0.2F + 3.5;
        pPoseStack.translate(0, scale, 0);

        var scale1 = (float) scale/1.4F;
        pPoseStack.scale(scale1, scale1, scale1);
        pPoseStack.mulPose(dispatcher.camera.rotation());

        var x = 0.020F;
        pPoseStack.scale(x, -x, x);

        var matrix4f = pPoseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);
        var text = Helpers.withStyleComponent("HURRY!", offWhite);
        var f2 = (float)(-font.width(text) / 2);

        font.drawInBatch(text, f2, -10, offWhite, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        font.drawInBatch(displayName, f1, 0, offWhite, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();

    }
}
