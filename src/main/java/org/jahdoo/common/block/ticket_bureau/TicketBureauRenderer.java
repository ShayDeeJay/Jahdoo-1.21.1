package org.jahdoo.common.block.ticket_bureau;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.jahdoo.common.block.tank.TankBlockEntity;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;

import static net.minecraft.client.Minecraft.getInstance;
import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.item.ItemDisplayContext.FIXED;
import static org.jahdoo.common.block.power_up_station.PowerUpStationRenderer.renderName;

public class TicketBureauRenderer implements BlockEntityRenderer<TicketBureauBlockEntity>{
    EntityRenderDispatcher dispatcher;
    public TicketBureauRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getEntityRenderer();
    }

    public void setGlow(TankBlockEntity tank){
        if(tank.usingThisTank.isEmpty()){
            if(tank.glowStrength > 150) tank.glowStrength -= 5;
        } else {
            if(tank.glowStrength < 255) tank.glowStrength += 5;
        }
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
            var displayName = Helpers.withStyleComponent(current + "/" + required, Helpers.colourByPercent(required, current, true));
            var complete = Helpers.withStyleComponent("Complete", ColourStore.MAGNET_RANGE_GREEN);
            renderName(dispatcher.camera.rotation(), CoreData.isFull(itemStack1) ? complete : displayName, stack, source, 1.4);
        }

        stack.pushPose();
        stack.translate(x, number, z);
        stack.scale(scale, scale, scale);
        stack.mulPose(Axis.YP.rotationDegrees(rotation));
        stack.mulPose(Axis.XP.rotationDegrees(22));
        itemRenderer.renderStatic(itemStack1, FIXED, packedLight, NO_OVERLAY, stack, source, entity.getLevel(), 1);
        stack.popPose();

    }

}