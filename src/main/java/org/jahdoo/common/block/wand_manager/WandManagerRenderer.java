package org.jahdoo.common.block.wand_manager;

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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;

import static net.minecraft.core.Direction.*;

public class WandManagerRenderer implements BlockEntityRenderer<WandManagerEntity> {

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public WandManagerRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    private int direction(BlockEntity blockEntity){
        var direction = blockEntity.getBlockState().getValue(WandManagerBlock.FACING);
        if(direction == SOUTH || direction == NORTH) return 90;
        return 0;
    }

    private void rotateAllItems(PoseStack poseStack, WandManagerEntity entity, Runnable stuff, int index, int totalItems, float partialTicks) {
        poseStack.pushPose();

        var angleOffset = 360.0f / totalItems;
        var itemAngle = angleOffset * index;

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(itemAngle + (entity.privateTicks + partialTicks) ));
        stuff.run();
        poseStack.popPose();
    }

    @Override
    public void render(
        WandManagerEntity wandManagerTable,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource source,
        int packedLight,
        int overlay
    ){
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var outputSlot = JahdooGearData.getRuneholder(wandManagerTable.getWandSlot()).runeSlots();
        var newSlots = outputSlot.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        var spacer = new AtomicDouble();
        var size = newSlots.size();

        focusedItem(poseStack, wandManagerTable, itemRenderer, source, packedLight, partialTick);
        for (int i = 0; i < size; i++) {
            var slot = newSlots.get(i);
            poseStack.pushPose();
            rotateAllItems(poseStack, wandManagerTable, () -> rotateItem(poseStack, wandManagerTable, itemRenderer, slot, source, partialTick, packedLight, size), i, size, partialTick);
            poseStack.popPose();
            spacer.set(spacer.get() + 0.12);
        }
    }

    private void focusedItem(
        PoseStack poseStack,
        WandManagerEntity entity,
        ItemRenderer renderer,
        MultiBufferSource source,
        int packedLight,
        float partialTicks
    ){
        poseStack.pushPose();

        var scaleItem = 0.80f;
        var outputSlot = entity.getWandSlot();
        var getItem = outputSlot.isEmpty() ? ItemStack.EMPTY : outputSlot;
        var ticks = ((entity.privateTicks + partialTicks) / 18) ;
        var animatePlace = Math.max(1.1, 1.4 - ticks) ;

        poseStack.translate(0.5f, animatePlace, 0.5f);
        poseStack.scale(scaleItem, scaleItem, scaleItem);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction(entity)));

        renderer.renderStatic(
            getItem,
            ItemDisplayContext.FIXED,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            source,
            entity.getLevel(),
            1
        );

        poseStack.popPose();
    }

    private void rotateItem(
        PoseStack poseStack,
        WandManagerEntity entity,
        ItemRenderer renderer,
        ItemStack itemStack,
        MultiBufferSource source,
        float partialTicks,
        int light,
        int distance
    ){
        poseStack.pushPose();

        var getCurrentTime = entity.privateTicks + partialTicks;
        var scaleItem = 0.1f;
        var bobOff = Math.sin(getCurrentTime / 20.0F) * 0.02F + 1.15f - 0.51;
        var animateRunes = Math.min(Math.max((double) distance / 44, 0.1), getCurrentTime / 20);

        poseStack.translate(0, bobOff, animateRunes);
        poseStack.scale(scaleItem, scaleItem, scaleItem);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        renderer.renderStatic(
            itemStack, ItemDisplayContext.FIXED,
            light,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            source,
            entity.getLevel(),
            1
        );

        poseStack.popPose();
    }

}