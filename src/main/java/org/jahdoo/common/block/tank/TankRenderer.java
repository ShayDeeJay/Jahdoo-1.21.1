package org.jahdoo.common.block.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.BlockReg;

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
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();

        var itemStack1 = new ItemStack(BlockReg.NEXITE_POWDER_BLOCK.get());
        var itemStack = entity.getRenderer();
        float number = 0.63f;
        int rotation = 0;

        setGlow(entity);

//        var renderer = mc.getBlockRenderer();
//        stack.pushPose();
//        stack.translate(0,1,0);
//        renderer.renderBatched(BlocksRegister.NEXITE_BLOCK.get().defaultBlockState(), entity.getBlockPos(), mc.level, stack, source.getBuffer(RenderType.solid()), false, RandomSource.create());
//        stack.popPose();

        for (int i = 0; i < itemStack.getCount(); i += entity.getMaxSlotSize()/10) {
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