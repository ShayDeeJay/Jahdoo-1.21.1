package org.jahdoo.common.block.ticket_bureau;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import org.jahdoo.common.components.CoreData;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static net.minecraft.client.Minecraft.getInstance;
import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.item.ItemDisplayContext.FIXED;

public class TicketBureauRenderer implements BlockEntityRenderer<TicketBureauBlockEntity>{
    EntityRenderDispatcher dispatcher;

    public TicketBureauRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(TicketBureauBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight, int packed) {
        var mc = getInstance();
        var itemRenderer = mc.getItemRenderer();
        var itemStack1 = entity.getTicketItem();
        var number = 0.693F;
        var direction = entity.getBlockState().getValue(TicketBureauBlock.FACING);
        var x = direction == EAST ? 0.82f : direction == SOUTH || direction == NORTH ? 0.5 : 0.18;
        var z = direction == EAST || direction == WEST ? 0.5f : direction == SOUTH ? 0.82f : 0.18;
        var rotation = direction == SOUTH ? 0 : direction == WEST ? 270 : direction == NORTH ? 180 : 90;
        var scale = 0.50f;
        var render = entity.getTicketItem();
        var required = CoreData.getRequired(render);
        var current = CoreData.getFilled(render);

        if(!itemStack1.isEmpty()){
            var displayName = TextHelpers.withStyleComponent(current + "/" + required, ColourHelpers.colourByPercent(required, current, true));
            var complete = TextHelpers.withStyleComponent("Complete", ColourHelpers.getMagnetRangeGreen());
            stack.pushPose();
            if(dispatcher.camera.getPosition().distanceTo(entity.getBlockPos().getCenter()) < 4){
                renderName(dispatcher.camera.rotation(), CoreData.isFull(itemStack1) ? complete : displayName, stack, source, 1.4);
            }
            stack.popPose();
        }

        var stamp = entity.lastItem;
        if(stamp != null){
            var rotate = (mc.level.getGameTime() + partialTick) * 20;
            stack.pushPose();
            stack.translate(0.5f, 0.8F, 0.5f);

            var xx = Math.max(0, scale - ((float) entity.getPrivateTicks() / 50));
            stack.scale(xx, xx, xx);
            stack.mulPose(Axis.YP.rotationDegrees(rotate));

            itemRenderer.renderStatic(stamp, FIXED, packedLight, NO_OVERLAY, stack, source, entity.getLevel(), 1);
            stack.popPose();
        }

        stack.pushPose();
        stack.translate(x, number, z);
        stack.scale(scale, scale, scale);
        stack.mulPose(Axis.YP.rotationDegrees(rotation));
        stack.mulPose(Axis.XP.rotationDegrees(22));
        itemRenderer.renderStatic(itemStack1, FIXED, packedLight, NO_OVERLAY, stack, source, entity.getLevel(), 1);
        stack.popPose();

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
        font.drawInBatch(displayName, f1, 0, ColourHelpers.getOffWhite(), true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();

    }

}