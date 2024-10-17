package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.block.tank.TankBlockEntity;
import org.jahdoo.registers.BlocksRegister;
import org.lwjgl.openal.SOFTDeferredUpdates;

public class TankRenderer implements BlockEntityRenderer<TankBlockEntity>{

    public TankRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TankBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        ItemStack itemStack1 = new ItemStack(BlocksRegister.JIDE_POWDER_BLOCk.get());
        ItemStack itemStack = pBlockEntity.getRenderer();
        float number = 0.63f;
        int rotation = 0;

        setGlow(pBlockEntity);

        for (int i = 0; i < itemStack.getCount(); i+=pBlockEntity.getMaxSlotSize()/10) {
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

    public void setGlow(TankBlockEntity tank){
        if(tank.usingThisTank.isEmpty()){
            if(tank.glowStrength > 150) tank.glowStrength -= 5;
        } else {
            if(tank.glowStrength < 255) tank.glowStrength += 5;
        }
    }

}