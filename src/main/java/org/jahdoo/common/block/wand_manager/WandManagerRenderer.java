package org.jahdoo.common.block.wand_manager;

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

import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;

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

    public static void rotateAllItems(PoseStack poseStack, Runnable stuff, int index, int totalItems, float partialTicks) {
        poseStack.pushPose();

        var minecraft = Minecraft.getInstance().level.getGameTime();
        var angleOffset = 360.0f / totalItems;
        var itemAngle = angleOffset * index;

        poseStack.translate(0.5, 0.8F, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(itemAngle + (minecraft + partialTicks) ));
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
        var outputSlot = JahdooGearData.getGearData(wandManagerTable.getWandSlot()).runeSlots();
        var newSlots = outputSlot.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        focusedItem(poseStack, wandManagerTable, itemRenderer, source, packedLight, partialTick);

        var size = newSlots.size();
        for (int i = 0; i < size; i++) {
            var slot = newSlots.get(i);
            poseStack.pushPose();
            rotateAllItems(poseStack, () -> rotateItem(poseStack, itemRenderer, slot, source, partialTick, packedLight, size), i, size, partialTick);
            poseStack.popPose();
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

    public static void rotateItem(
        PoseStack poseStack,
        ItemRenderer renderer,
        ItemStack itemStack,
        MultiBufferSource source,
        float partialTicks,
        int light,
        int distance
    ){
        poseStack.pushPose();
        var mc = Minecraft.getInstance();
        var level = mc.level;
        var getCurrentTime = level.getGameTime() + partialTicks;
        var scaleItem = 0.2f;
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
            level,
            1
        );

        poseStack.popPose();
    }

}