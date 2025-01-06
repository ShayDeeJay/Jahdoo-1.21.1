package org.jahdoo.client.block_renderer;

import com.google.common.util.concurrent.AtomicDouble;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.block.wand_block_manager.WandManagerBlock;
import org.jahdoo.block.wand_block_manager.WandManagerEntity;
import org.jahdoo.components.rune_data.RuneHolder;

public class WandManagerTableRenderer implements BlockEntityRenderer<WandManagerEntity> {
    private final BlockEntityRenderDispatcher entityRenderDispatcher;
    public WandManagerTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    private int direction(BlockEntity blockEntity){
        var direction = blockEntity.getBlockState().getValue(WandManagerBlock.FACING);
        if(direction == Direction.SOUTH ) return 90;
        if(direction == Direction.WEST ) return 0;
        if(direction == Direction.NORTH ) return 90;
        return 0;
    }

    @Override
    public void render(
        WandManagerEntity wandManagerTable,
        float pPartialTick,
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pPackedLight,
        int pPackedOverlay
    ){
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var outputSlot = RuneHolder.getRuneholder(wandManagerTable.getWandSlot()).runeSlots();
        var newSlots = outputSlot.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        var spacer = new AtomicDouble();

        focusedItem(pPoseStack, wandManagerTable, itemRenderer, pBuffer, pPackedLight, pPartialTick);
        var size = newSlots.size();
        for (int i = 0; i < size; i++) {
            var slot = newSlots.get(i);
            pPoseStack.pushPose();
            rotateAllItems(pPoseStack, wandManagerTable, () -> rotateItem(pPoseStack, wandManagerTable, itemRenderer, slot, pBuffer, pPartialTick, pPackedLight, size), i, size, pPartialTick);
            pPoseStack.popPose();
            spacer.set(spacer.get() + 0.12);
        }
    }

    private void focusedItem(PoseStack pPoseStack, WandManagerEntity pBlockEntity, ItemRenderer itemRenderer, MultiBufferSource pBuffer, int packedLight, float partialTicks){
        pPoseStack.pushPose();
        var scaleItem = 0.80f;
        var outputSlot = pBlockEntity.getWandSlot();
        var getItem = outputSlot.isEmpty() ? ItemStack.EMPTY : outputSlot;
        var ticks = ((pBlockEntity.privateTicks + partialTicks) / 18) ;
        var animatePlace = Math.max(1.1, 1.4 - ticks) ;
        pPoseStack.translate(0.5f, animatePlace, 0.5f);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction(pBlockEntity)));

        itemRenderer.renderStatic(
            getItem,
            ItemDisplayContext.FIXED,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );

        pPoseStack.popPose();
    }

    private void rotateAllItems(PoseStack pPoseStack, WandManagerEntity pBlockEntity, Runnable stuff, int index, int totalItems, float partialTicks) {
        pPoseStack.pushPose();
        var angleOffset = 360.0f / totalItems;
        var itemAngle = angleOffset * index;
        pPoseStack.translate(0.5, 0.5, 0.5);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(itemAngle + (pBlockEntity.privateTicks + partialTicks) ));
        stuff.run();
        pPoseStack.popPose();
    }

    private void rotateItem(
        PoseStack pPoseStack,
        WandManagerEntity pBlockEntity,
        ItemRenderer itemRenderer,
        ItemStack itemStack,
        MultiBufferSource pBuffer,
        float partialTicks,
        int light,
        int distance
    ){
        pPoseStack.pushPose();
        var getCurrentTime = pBlockEntity.privateTicks + partialTicks;
        var scaleItem = 0.1f;
        var bobOff = Math.sin((pBlockEntity.privateTicks + partialTicks) / 20.0F) * 0.02F + 1.15f - 0.51;
        var animateRunes = Math.min(Math.max((double) distance / 44, 0.1), getCurrentTime / 20);
        pPoseStack.translate(0, bobOff, animateRunes);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180));

        itemRenderer.renderStatic(
            itemStack, ItemDisplayContext.FIXED,
            light,
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );

        pPoseStack.popPose();
    }

}