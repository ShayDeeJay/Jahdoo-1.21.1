package org.jahdoo.common.block.augment_modification_station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static org.jahdoo.common.block.infuser.InfuserBlock.FACING;


public class AugmentModificationRenderer implements BlockEntityRenderer<AugmentModificationEntity> {

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public AugmentModificationRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    protected void renderNameTag(AugmentModificationEntity entity, Component name, PoseStack postStack, MultiBufferSource source, int textColour) {
        var getEntity = this.entityRenderDispatcher.camera.getEntity();
        var distance = entity.getBlockPos().getCenter().closerThan(getEntity.position(), 10);

        if (getEntity instanceof Player player && player.isCreative() && distance) {
            postStack.pushPose();
            postStack.translate(0.5, 1.2, 0.2);
            postStack.mulPose(Axis.XP.rotationDegrees(180));
            postStack.scale(0.01f, 0.01f, 0.01f);
            var matrix4f = postStack.last().pose();
            var font = Minecraft.getInstance().font;
            var f1 = (float) -font.width(name) / 2;
            font.drawInBatch(name, f1, 0, textColour, false, matrix4f, source, Font.DisplayMode.NORMAL  , 0, 255);
            postStack.popPose();
        }
    }

    @Override
    public void render(
        AugmentModificationEntity augmentStation,
        float partial,
        PoseStack poseStack,
        MultiBufferSource source,
        int packedLight,
        int packedOverlay
    ){
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
//        var keyFromAugment = DataComponentHelper.getKeyFromAugment(augmentStation.getInteractionSlot());
//        var ability = AbilityReg.getFirstSpellByTypeId(keyFromAugment);
//        if(ability.isPresent()){
//            var getElement = ElementReg.fromId(augmentStation.getInteractionSlot().get(DataComponents.CUSTOM_MODEL_DATA).value());
//            var name = Component.literal(ability.get().getAbilityName());
//            getElement.ifPresent(element -> renderNameTag(augmentStation, name, poseStack, source, element.textColourA()));
//        }
        focusedItem(poseStack, augmentStation, itemRenderer, source, packedLight);
    }

    private void focusedItem(
        PoseStack poseStack,
        AugmentModificationEntity entity,
        ItemRenderer renderer,
        MultiBufferSource source,
        int packedLight
    ){
        poseStack.pushPose();
        var scaleItem = 0.40f;
        var getState = entity.getBlockState().getValue(FACING).getOpposite();
        var N = getState == Direction.NORTH;
        var S = getState == Direction.SOUTH;
        var E = getState == Direction.EAST;
        var W = getState == Direction.WEST;

        poseStack.translate(W ? 0.53f : E ? 0.47f : 0.5f, 0.88f, N ? 0.53f : S ? 0.47 : 0.5f);
        poseStack.scale(scaleItem, scaleItem, scaleItem);
        poseStack.mulPose(getState.getRotation());
        poseStack.mulPose(Axis.XP.rotationDegrees(-23));

        ItemStack itemStack = entity.inputItemHandler.getStackInSlot(0);
        renderer.renderStatic(
            itemStack, ItemDisplayContext.FIXED, packedLight,
            OverlayTexture.NO_OVERLAY, poseStack, source, entity.getLevel(), 1
        );

        poseStack.popPose();
    }

}