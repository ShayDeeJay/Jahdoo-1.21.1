package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jahdoo.block.crafter.CreatorEntity;
import org.jahdoo.items.wand.WandItem;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class CreatorRenderer implements BlockEntityRenderer<CreatorEntity> {

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
        creatorEntity.setAnimator();
        creatorEntity.setAnimatedDistance();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        AtomicInteger atomicInteger = new AtomicInteger();
        focusedItem(pPoseStack, creatorEntity, itemRenderer, pBuffer);

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
            pPoseStack.mulPose(Axis.YP.rotationDegrees(itemAngle + ((float) pBlockEntity.getAnimationTicker() + partialTicks)));
        } else {

            float[][] positions = {
                {0.5f, 0.12f},  // Middle top (center of first row)
                {0.22f, 0.40f}, // Middle row left
                {0.78f, 0.40f}, // Middle row right
                {0.5f, 0.68f}, // Middle bottom (center of last row)
                {0.22f, 0.12f},  // Top-left corner
                {0.78f, 0.12f},  // Top-right corner
                {0.22f, 0.68f}, // Bottom-left corner
                {0.78f, 0.68f}  // Bottom-right corner
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

    private void focusedItem(PoseStack pPoseStack, CreatorEntity pBlockEntity, ItemRenderer itemRenderer, MultiBufferSource pBuffer){
        pPoseStack.pushPose();
        var getCurrentTime = (float) pBlockEntity.getAnimationTicker();
        var outputSlot = pBlockEntity.outputItemHandler.getStackInSlot(0);
        var itemStack = outputSlot.isEmpty() ? pBlockEntity.getOutputResult() : outputSlot;
        var isWand = itemStack.getItem() instanceof WandItem;
        var scaleItem = isWand ? 0.7f : 0.4f;
        var maxLightLevel = getLightLevel(Objects.requireNonNull(pBlockEntity.getLevel()), pBlockEntity.getBlockPos());

        pPoseStack.translate(0.5f, isWand ? 1.55f : 1.3f, 0.5f);
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
        float getCurrentTime = (float) pBlockEntity.getAnimationTicker();
        float scaleItem = 0.3f;
        pPoseStack.translate(0, 1.25f, pBlockEntity.animateDistanceIncrement/5);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        if(!pBlockEntity.canCraft()) pPoseStack.mulPose(Axis.YP.rotationDegrees(getCurrentTime));

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