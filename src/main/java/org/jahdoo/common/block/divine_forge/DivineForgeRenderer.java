package org.jahdoo.common.block.divine_forge;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ArmorItem;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.item.ItemDisplayContext.FIXED;
import static org.jahdoo.common.block.divine_forge.DivineForge.FACING;
import static org.jahdoo.common.block.wand_manager.WandManagerRenderer.rotateAllItems;
import static org.jahdoo.common.block.wand_manager.WandManagerRenderer.rotateItem;

public class DivineForgeRenderer implements BlockEntityRenderer<DivineForgeEntity>{

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public DivineForgeRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(
        DivineForgeEntity entity,
        float partial,
        PoseStack poseStack,
        MultiBufferSource source,
        int packedLight,
        int packedOverlay
    ) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();

        renderPrimaryItem(entity, poseStack, source, packedLight, itemRenderer, partial);

        var outputSlot = JahdooGearData.getGearData(entity.itemSlot()).runeSlots();
        var ticks = entity.getPrivateTicks();

        if(outputSlot != null & ticks+partial > 6){
            var newSlots = outputSlot.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
            var size = newSlots.size();

            for (int i = 0; i < size; i++) {
                var slot = newSlots.get(i);
                poseStack.pushPose();
                rotateAllItems(poseStack, () -> rotateItem(poseStack, itemRenderer, slot, source, partial, packedLight, size + 10, ticks), i, size, partial, ticks);
                poseStack.popPose();
            }
        }
    }

    private void renderPrimaryItem(
        DivineForgeEntity entity,
        PoseStack poseStack,
        MultiBufferSource source,
        int packedLight,
        ItemRenderer itemRenderer,
        float partialTick
    ) {
        var renderItem = entity.getItem().getStackInSlot(0);
        if(!renderItem.isEmpty()){
            var direction = entity.getBlockState().getValue(FACING);
            var directionAd = direction == EAST ? 270 : direction == WEST ? 90 : direction == SOUTH ? 180 : 0;

            var mc = Minecraft.getInstance();
            var rotate = entity.getPrivateTicks()+ partialTick;
            var bobOff = Math.sin((entity.getPrivateTicks() + partialTick )/ 10.0F) * 0.02F + 2F - 0.51;

            if(renderItem.getItem() instanceof ArmorItem armorItem){
                var stand = entity.getStand(mc.level);
                var scale = Math.min(0.4F, rotate / 10);
                stand.setInvisible(true);
                poseStack.pushPose();
                var yOffset = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> 0.95;
                    case CHEST -> 0.6;
                    case LEGS -> 0.25;
                    case FEET -> 0.1;
                    default -> 0;
                };
                poseStack.translate(0.5,  bobOff - yOffset, 0.5);
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(Axis.YP.rotationDegrees(directionAd));
                Lighting.setupForEntityInInventory();
                EntityRenderDispatcher entityrenderdispatcher = mc.getEntityRenderDispatcher();
                Lighting.setupForEntityInInventory(Axis.YP.rotationDegrees(directionAd));
                stand.setItemSlot(armorItem.getEquipmentSlot(), renderItem);
                entityrenderdispatcher.getRenderer(stand).render(stand, 0, 0 , poseStack, source, FULL_BRIGHT);
                poseStack.popPose();
            } else {
                var scale = Math.min(0.7F, rotate / 5);
                poseStack.pushPose();
                poseStack.translate(0.5, bobOff, 0.5);
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(Axis.YP.rotationDegrees(directionAd));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                itemRenderer.renderStatic(renderItem, FIXED, packedLight, NO_OVERLAY, poseStack, source, entity.getLevel(), 1);
                poseStack.popPose();
            }

        }
    }
}