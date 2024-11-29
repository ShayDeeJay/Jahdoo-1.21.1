package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.block.tank.NexiteTankBlockEntity;
import org.jahdoo.registers.BlocksRegister;

public class NexiteTankRenderer implements BlockEntityRenderer<NexiteTankBlockEntity>{

    public NexiteTankRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(NexiteTankBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();

        var itemStack1 = new ItemStack(BlocksRegister.NEXITE_POWDER_BLOCK.get());
        var itemStack = pBlockEntity.getRenderer();
        float number = 0.63f;
        int rotation = 0;

        setGlow(pBlockEntity);

//        var renderer = mc.getBlockRenderer();
//        pPoseStack.pushPose();
//        pPoseStack.translate(0,1,0);
//        renderer.renderBatched(BlocksRegister.NEXITE_BLOCK.get().defaultBlockState(), pBlockEntity.getBlockPos(), mc.level, pPoseStack, pBuffer.getBuffer(RenderType.solid()), false, RandomSource.create());
//        pPoseStack.popPose();

        for (int i = 0; i < itemStack.getCount(); i += pBlockEntity.getMaxSlotSize()/10) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, number, 0.5f);
            pPoseStack.scale(0.9f,0.9f,0.9f);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            itemRenderer.renderStatic(
                itemStack1,
                ItemDisplayContext.FIXED,
                pBlockEntity.glowStrength,
                OverlayTexture.NO_OVERLAY,
                pPoseStack,
                pBuffer,
                pBlockEntity.getLevel(),
                1
            );
            pPoseStack.popPose();
            number += 0.056f;
            rotation += 90;
        }
    }

    public void setGlow(NexiteTankBlockEntity tank){
        if(tank.usingThisTank.isEmpty()){
            if(tank.glowStrength > 150) tank.glowStrength -= 5;
        } else {
            if(tank.glowStrength < 255) tank.glowStrength += 5;
        }
    }

}