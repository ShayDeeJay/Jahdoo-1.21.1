package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.block.rune_table.RuneTableEntity;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.ItemsRegister;

import static org.jahdoo.block.rune_table.RuneTable.FACING;

public class RuneTableRenderer implements BlockEntityRenderer<RuneTableEntity>{
    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public RuneTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(RuneTableEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();

        renderPrimaryItem(pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer);
//        renderBlockItems(new ItemStack(ItemsRegister.RUNE.get()), pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer, 0.8F, 0.8F, 0.005F, 34);
//        renderBlockItems(new ItemStack(ItemsRegister.RUNE.get()), pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer, 0.7F, 0.82F, 0F, 12);
//        renderBlockItems(new ItemStack(ItemsRegister.RUNE.get()), pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer, 0.8F, 0.7F, -0.005F, 65);
    }

    private void renderPrimaryItem(
            RuneTableEntity pBlockEntity,
            PoseStack pPoseStack,
            MultiBufferSource pBuffer,
            int pPackedLight,
            ItemRenderer itemRenderer
    ) {
        var renderItem = pBlockEntity.getItem().getStackInSlot(0);
        if(!renderItem.isEmpty()){
            var height = 0.83F;
            var scale = 0.5f;
            var direction = pBlockEntity.getBlockState().getValue(FACING);
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, height, 0.5);
            pPoseStack.scale(scale, scale, scale);
            var directionAd = direction == Direction.EAST ? 90 : direction == Direction.WEST ? 270 : direction == Direction.NORTH ? 180 : 0;
            pPoseStack.mulPose(Axis.YP.rotationDegrees(directionAd));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(0));
            itemRenderer.renderStatic(renderItem, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }
    }

    private void renderBlockItems(
            ItemStack renderItem,
            RuneTableEntity pBlockEntity,
            PoseStack pPoseStack,
            MultiBufferSource pBuffer,
            int pPackedLight,
            ItemRenderer itemRenderer,
            float adjustX,
            float adjustZ,
            float adjustY,
            float rotateItem
    ) {
        if(!renderItem.isEmpty()){
            var height = 0.76F + adjustY;
            var scale = 0.3f;
            var direction = pBlockEntity.getBlockState().getValue(FACING);
            pPoseStack.pushPose();
            pPoseStack.translate(adjustX, height, adjustZ);
            pPoseStack.scale(scale, scale, scale);
            var directionAd = direction == Direction.EAST || direction == Direction.WEST ? 90 : 0;
            pPoseStack.mulPose(Axis.YP.rotationDegrees(directionAd));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(rotateItem));
            itemRenderer.renderStatic(renderItem, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }
    }
}