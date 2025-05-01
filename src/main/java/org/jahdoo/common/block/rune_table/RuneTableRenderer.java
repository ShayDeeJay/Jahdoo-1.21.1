package org.jahdoo.common.block.rune_table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.item.ItemDisplayContext.FIXED;
import static org.jahdoo.common.block.rune_table.RuneTable.FACING;

public class RuneTableRenderer implements BlockEntityRenderer<RuneTableEntity>{

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public RuneTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(
        RuneTableEntity entity,
        float partial,
        PoseStack poseStack,
        MultiBufferSource source,
        int packedLight,
        int packedOverlay
    ) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();

        renderPrimaryItem(entity, poseStack, source, packedLight, itemRenderer);
    }

    private void renderBlockItems(
            ItemStack renderItem,
            RuneTableEntity entity,
            PoseStack poseStack,
            MultiBufferSource source,
            int packedLight,
            ItemRenderer itemRenderer,
            float adjustX,
            float adjustZ,
            float adjustY,
            float rotateItem
    ) {
        if(!renderItem.isEmpty()){
            var height = 0.76F + adjustY;
            var scale = 0.3f;
            var direction = entity.getBlockState().getValue(FACING);
            var directionAd = direction == EAST || direction == WEST ? 90 : 0;

            poseStack.pushPose();
            poseStack.translate(adjustX, height, adjustZ);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotationDegrees(directionAd));
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotateItem));
            itemRenderer.renderStatic(renderItem, FIXED, packedLight, NO_OVERLAY, poseStack, source, entity.getLevel(), 1);
            poseStack.popPose();
        }
    }

    private void renderPrimaryItem(
        RuneTableEntity entity,
        PoseStack poseStack,
        MultiBufferSource source,
        int packedLight,
        ItemRenderer itemRenderer
    ) {
        var renderItem = entity.getItem().getStackInSlot(0);
        if(!renderItem.isEmpty()){
            var height = 0.67F;
            var scale = 0.5f;
            var direction = entity.getBlockState().getValue(FACING);
            var directionAd = direction == EAST ? 90 : direction == WEST ? 270 : direction == NORTH ? 180 : 0;

            poseStack.pushPose();
            poseStack.translate(0.5, height, 0.5);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotationDegrees(directionAd));
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(0));
            itemRenderer.renderStatic(renderItem, FIXED, packedLight, NO_OVERLAY, poseStack, source, entity.getLevel(), 1);
            poseStack.popPose();
        }
    }
}