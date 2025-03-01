package org.jahdoo.common.block.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.BlockReg;

import static net.minecraft.client.Minecraft.getInstance;

public class TankRenderer implements BlockEntityRenderer<TankBlockEntity>{

    public TankRenderer(BlockEntityRendererProvider.Context context) {}

    public void setGlow(TankBlockEntity tank){
        if(tank.usingThisTank.isEmpty()){
            if(tank.glowStrength > 150) tank.glowStrength -= 5;
        } else {
            if(tank.glowStrength < 255) tank.glowStrength += 5;
        }
    }

    @Override
    public void render(TankBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight, int packed) {
        var mc = getInstance();
        var itemRenderer = mc.getItemRenderer();
        var itemStack1 = new ItemStack(BlockReg.NEXITE_POWDER_BLOCK.get());
        var itemStack = entity.getRenderer();
        var number = 0.63F;
        var rotation = 0;

        setGlow(entity);
        for (var i = 0; i < itemStack.getCount(); i += entity.getMaxSlotSize()/10) {
            stack.pushPose();
            stack.translate(0.5f, number, 0.5f);
            stack.scale(0.9f,0.9f,0.9f);
            stack.mulPose(Axis.YP.rotationDegrees(rotation));
            itemRenderer.renderStatic(
                itemStack1,
                ItemDisplayContext.FIXED,
                entity.glowStrength,
                OverlayTexture.NO_OVERLAY,
                stack,
                source,
                entity.getLevel(),
                1
            );
            stack.popPose();
            number += 0.056f;
            rotation += 90;
        }
    }

}