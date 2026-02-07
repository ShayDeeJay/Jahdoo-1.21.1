package org.jahdoo.common.block.power_up_station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static java.lang.Math.min;
import static net.minecraft.client.Minecraft.getInstance;
import static org.jahdoo.common.block.power_up_station.PowerUpStation.TYPE;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;

public class PowerUpStationRenderer implements BlockEntityRenderer<PowerUpStationEntity>{
    EntityRenderDispatcher dispatcher;

    public PowerUpStationRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public boolean shouldRenderOffScreen(PowerUpStationEntity blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(PowerUpStationEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public void render(PowerUpStationEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight, int packed) {
        var render = entity.getItem();

        if(render.isEmpty()) {
//            var level = entity.getLevel();
//            if(level == null) return;
//            var i = level.getGameTime()
//            var rad =  Math.min(0.06f, (float) level.getGameTime() / 300);
//            var height = 200;
//            var colourLight = getColourLight(ColourStore.RATING_4_YELLOW, 1.2);
//            renderBeaconBeam(stack, source, BEAM_LOCATION, partialTick, 1.1F, i, 0, height, colourLight, rad, 0);
            return;
        }

        var mc = getInstance();
        var itemRenderer = mc.getItemRenderer();
        var required = CoreData.getRequired(render);
        var current = CoreData.getFilled(render);

        var colour = PowerUpStation.colourByState(entity.getBlockState().getValue(TYPE));
        renderName(dispatcher.camera.rotation(), JahdooHelpers.withStyleComponent(current + "/" + required, colour), stack, source, 1.8);

        stack.pushPose();
        var ticks = getInstance().level.getGameTime();
        var scaled = 2;
        stack.translate(0.5, 0, 0.5);
        stack.scale(scaled, scaled, scaled);
        stack.translate(0, 0.101, 0);
        stack.rotateAround(Axis.YN.rotationDegrees((ticks * 1.4f) + partialTick), 0, 0, 0);

        stack.pushPose();
        stack.translate(-0.5, 0, -0.5);
        stack.popPose();

        drawTexture(
            stack.last(),
            source,
            255,
            min(1.4f,  ticks + partialTick),
            JahdooHelpers.res("textures/entity/shield.png"), colour
        );
        stack.popPose();

        if(!render.isEmpty()){
            var rotate = ticks + partialTick;
            var animate = rotate / 4;
            var scale = Math.min(0.5F, animate);
            var bobOff = Math.sin(rotate / 10.0F) * 0.08F + 2.2F - 0.9;

            stack.pushPose();
            stack.translate(0.5f, bobOff, 0.5F);
            stack.scale(scale, scale, scale);
            stack.mulPose(Axis.YP.rotationDegrees(rotate * 2));

            itemRenderer.renderStatic(
                render,
                ItemDisplayContext.FIXED,
                180,
                OverlayTexture.NO_OVERLAY,
                stack,
                source,
                entity.getLevel(),
                1
            );

            stack.popPose();
        }
    }

    public static void renderName(Quaternionf rotation , Component displayName, PoseStack pPoseStack, MultiBufferSource bufferSource, double adjustHeight) {
        var x = 0.020F;
        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, adjustHeight, 0.5);
        pPoseStack.mulPose(rotation);
        pPoseStack.scale(x, -x, x);
        Matrix4f matrix4f = pPoseStack.last().pose();
        font.drawInBatch(displayName, f1, 0, ColourStore.OFF_WHITE, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();

    }

}