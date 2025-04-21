package org.jahdoo.common.block.perk_table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.client.Minecraft.getInstance;
import static org.jahdoo.common.block.perk_table.PerkTable.TEXTURE;
import static org.jahdoo.common.registers.ItemReg.*;

public class PerkTableRenderer implements BlockEntityRenderer<PerkTableEntity>{

    public PerkTableRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(PerkTableEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight, int packed) {
        var mc = getInstance();
        var itemRenderer = mc.getItemRenderer();
        int state = entity.getBlockState().getValue(TEXTURE);
        var render = new ItemStack(state == 0 ? HEALTH_CONTAINER : state == 1 ? MANA_CONTAINER : state == 2 ? QUEST_CONTAINER : BOON_CONTAINER);
        var rotate = entity.counter + partialTick;
        var animate = rotate / 12;
        var scale = Math.min(1.2F, animate);
        var bobOff = Math.sin(rotate / 10.0F) * 0.08F + 2F - 0.51;

        if(!entity.getUsed()){
            stack.pushPose();
            stack.translate(0.5f, Math.min(bobOff, animate), 0.5F);
            stack.scale(scale, scale, scale);
            stack.mulPose(Axis.YP.rotationDegrees(rotate * 2));
            itemRenderer.renderStatic(
                render,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                stack,
                source,
                entity.getLevel(),
                1
            );
            stack.popPose();
        }
    }

}