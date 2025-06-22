package org.jahdoo.common.block.creator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jahdoo.common.registers.ItemReg;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class CreatorRenderer implements BlockEntityRenderer<org.jahdoo.common.block.creator.CreatorEntity> {

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public CreatorRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(
        CreatorEntity creatorEntity,
        float pPartialTick,
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pPackedLight,
        int pPackedOverlay
    ){
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        AtomicInteger atomicInteger = new AtomicInteger();
        top(pPoseStack, creatorEntity, itemRenderer, pBuffer, pPartialTick, pPackedLight);
        focusedItem(pPoseStack, creatorEntity, itemRenderer, pBuffer, pPartialTick);

        for(int i = 0; i < creatorEntity.inputItemHandler.getSlots(); i++){
            ItemStack itemStack = creatorEntity.inputItemHandler.getStackInSlot(i);
            if(!itemStack.isEmpty()) atomicInteger.set(atomicInteger.get() + 1);
        }

        for(int i = 0; i < creatorEntity.inputItemHandler.getSlots(); i++){
            ItemStack itemStack = creatorEntity.inputItemHandler.getStackInSlot(i);
            rotateAllItems(pPoseStack, creatorEntity, () -> rotateItem(pPoseStack, creatorEntity, itemRenderer, itemStack, pBuffer, pPartialTick), i, atomicInteger.get(), pPartialTick);
        }
    }

    private void rotateAllItems(PoseStack pPoseStack, CreatorEntity pBlockEntity, Runnable stuff, int index, int totalItems, float partialTicks) {
        pPoseStack.pushPose();

        if (pBlockEntity.canCraft()) {
            float angleOffset = 360.0f / totalItems;
            float itemAngle = angleOffset * index;
            pPoseStack.translate(0.5, -0.1, 0.5);
            var frameTimeNs = Minecraft.getInstance().getFrameTimeNs() ;
            pPoseStack.mulPose(Axis.YP.rotationDegrees(itemAngle + (frameTimeNs + partialTicks)));
        } else {

            float[][] positions = {
                {0.5f, 0.04f},  // Middle top (center of first row)
                {0.16f, 0.40f}, // Middle row left
                {0.86f, 0.40f}, // Middle row right
                {0.5f, 0.76f}, // Middle bottom (center of last row)
                {0.18f, 0.06f},  // Top-left corner
                {0.84f, 0.06f},  // Top-right corner
                {0.16f, 0.74f}, // Bottom-left corner
                {0.84f, 0.74f}  // Bottom-right corner
            };

            if (index < positions.length) {
                float xTranslation = positions[index][0];
                float zTranslation = positions[index][1];
                pPoseStack.translate(xTranslation, -0.1, zTranslation);
            }
        }

        stuff.run();
        pPoseStack.popPose();
    }

    private void top(PoseStack stack, CreatorEntity creator, ItemRenderer iRenderer, MultiBufferSource pBuffer, float partialTicks, int packedLight){
        stack.pushPose();
        var progress = creator.getProgress();
        var level = Minecraft.getInstance().level;
        if(level == null) return;

        var itemStack = new ItemStack(ItemReg.CREATOR_TOP.get());
        var scaleItem = 1.2f;

        var scale = Math.sin(((level.getGameTime() + partialTicks) / 14.0F)) * 0.03F + 1;
        stack.translate(0.5, scale, 0.5);
        stack.scale(scaleItem, scaleItem, scaleItem);

        if(progress > 0) stack.mulPose(Axis.YP.rotationDegrees(creator.getProgress() + partialTicks));

        iRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            stack,
            pBuffer,
            creator.getLevel(),
            1
        );

        stack.popPose();
    }

    private void focusedItem(PoseStack pPoseStack, CreatorEntity pBlockEntity, ItemRenderer itemRenderer, MultiBufferSource pBuffer, float partialTicks){
        pPoseStack.pushPose();
        var level = Minecraft.getInstance().level;
        var getCurrentTime = level.getGameTime() + partialTicks;
        var outputSlot = pBlockEntity.outputItemHandler.getStackInSlot(0);
        var itemStack = outputSlot.isEmpty() ? pBlockEntity.getOutputResult() : outputSlot;
        var scaleItem = 0.4f;
        var maxLightLevel = getLightLevel(Objects.requireNonNull(pBlockEntity.getLevel()), pBlockEntity.getBlockPos());

        var scale = Math.sin(((level.getGameTime() + partialTicks) / 14.0F)) * 0.02F;
        pPoseStack.translate(0, scale, 0);
        pPoseStack.translate(0.5f, 1.3f + ((float) pBlockEntity.getProgress() / 1000) + scale, 0.5f);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(getCurrentTime));

        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            outputSlot.isEmpty() ? maxLightLevel / 2 : 200,
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );

        pPoseStack.popPose();
    }

    private void rotateItem(PoseStack pPoseStack, CreatorEntity pBlockEntity, ItemRenderer itemRenderer, ItemStack itemStack, MultiBufferSource pBuffer, float partialTicks){
        pPoseStack.pushPose();
        var level = Minecraft.getInstance().level;
        float getCurrentTime = level.getGameTime() + partialTicks;
        float scaleItem = 0.2f;
        pPoseStack.translate(0, 1.25f, pBlockEntity.animateDistanceIncrement/5);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        if(!pBlockEntity.canCraft()) pPoseStack.mulPose(Axis.YP.rotationDegrees(getCurrentTime));
        var scale = Math.sin(((level.getGameTime() + partialTicks) / 14.0F)) * 0.15F;
        pPoseStack.translate(0, scale - 0.4, 0);

        itemRenderer.renderStatic(
            itemStack, ItemDisplayContext.FIXED,
            getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );

        pPoseStack.popPose();
    }


    private int getLightLevel(Level level, BlockPos blockPos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, blockPos);
        int sLight = level.getBrightness(LightLayer.SKY, blockPos);
        return LightTexture.pack(bLight, sLight);
    }
}