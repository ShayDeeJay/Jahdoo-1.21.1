package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.block.tank.NexiteTankBlockEntity;
import org.jahdoo.registers.ItemsRegister;

public class ShoppingTableRenderer implements BlockEntityRenderer<ShoppingTableEntity>{
    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public ShoppingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(ShoppingTableEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();

        renderPrice(pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer);
        renderSaleItem(pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer);
    }

    private void renderPrice(ShoppingTableEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ItemRenderer itemRenderer) {
        var itemStack1 = new ItemStack(ItemsRegister.GOLD_COIN.get());
        float number = 0.65f;
        int rotation = 0;


        pPoseStack.pushPose();
        renderNameTag(pBlockEntity, Component.literal("24"), pPoseStack, pBuffer, -1);
        pPoseStack.translate(0.5f, number, 0f);
        var x = 0.2f;
        pPoseStack.scale(x, x, x);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        itemRenderer.renderStatic(
            itemStack1,
            ItemDisplayContext.FIXED,
            pPackedLight,
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );
        pPoseStack.popPose();
    }

    private void renderSaleItem(ShoppingTableEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ItemRenderer itemRenderer) {
        var itemStack1 = pBlockEntity.inputItemHandler.getStackInSlot(0);
        float number = 1.2f;
        int rotation = 0;

        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, number, 0.5f);
        var x = 0.5f;
        pPoseStack.scale(x, x, x);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        itemRenderer.renderStatic(
            itemStack1,
            ItemDisplayContext.FIXED,
            pPackedLight,
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );
        pPoseStack.popPose();
    }

    protected void renderNameTag(BlockEntity bEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int textColour) {
        var entity = this.entityRenderDispatcher.camera.getEntity();
        var canRender = bEntity.getBlockPos().getCenter().closerThan(entity.position(), 10);
        var z = 0.01f;

        if (canRender) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 0.85, 0);
//            pPoseStack.mulPose(this.entityRenderDispatcher.camera.rotation());
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
            pPoseStack.scale(z, z, z);
            var matrix4f = pPoseStack.last().pose();
            var font = Minecraft.getInstance().font;
            var f1 = (float) -font.width(pDisplayName) / 2;
            font.drawInBatch(pDisplayName, f1, 0, textColour, false, matrix4f, pBuffer, Font.DisplayMode.SEE_THROUGH , 0, 255);
            pPoseStack.popPose();
        }
    }


}