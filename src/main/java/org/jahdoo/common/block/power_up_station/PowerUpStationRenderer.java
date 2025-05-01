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
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.components.CoreData;
import org.joml.Matrix4f;

import static net.minecraft.client.Minecraft.getInstance;

public class PowerUpStationRenderer implements BlockEntityRenderer<PowerUpStationEntity>{
    EntityRenderDispatcher dispatcher;

    public PowerUpStationRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getEntityRenderer();
    }


    @Override
    public void render(PowerUpStationEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight, int packed) {
        var render = entity.getItem();
        if(render.isEmpty()) return;

        var mc = getInstance();
        var itemRenderer = mc.getItemRenderer();
        var required = CoreData.getRequired(render);
        var current = CoreData.getFilled(render);

        renderName(entity, Helpers.withStyleComponent(current + "/" + required, ColourStore.PERK_GREEN), stack, source);

        if(!render.isEmpty()){
            var rotate = getInstance().level.getGameTime() + partialTick;
            var animate = rotate / 4;
            var scale = Math.min(0.5F, animate);
            var bobOff = Math.sin(rotate / 10.0F) * 0.08F + 2F - 0.9;

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

    protected void renderName(AbstractBEInventory pos, Component displayName, PoseStack pPoseStack, MultiBufferSource bufferSource) {
        pPoseStack.pushPose();

        pPoseStack.translate(0.5, 1.6, 0.5);
        pPoseStack.mulPose(dispatcher.camera.rotation());

        var x = 0.020F;
        pPoseStack.scale(x, -x, x);
        Matrix4f matrix4f = pPoseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);
        font.drawInBatch(displayName, f1, 0, ColourStore.OFF_WHITE, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();

    }

}