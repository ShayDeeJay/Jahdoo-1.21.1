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
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.block.crafter.CreatorEntity;
import org.jahdoo.client.SharedUI;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.block.infuser.InfuserBlock.FACING;


public class AugmentModificationStationRenderer implements BlockEntityRenderer<AugmentModificationEntity> {

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public AugmentModificationStationRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(
        AugmentModificationEntity augmentStation,
        float pPartialTick,
        @NotNull PoseStack pPoseStack,
        @NotNull MultiBufferSource pBuffer,
        int pPackedLight,
        int pPackedOverlay
    ){
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var keyFromAugment = DataComponentHelper.getKeyFromAugment(augmentStation.getInteractionSlot());
        var ability = AbilityRegister.getFirstSpellByTypeId(keyFromAugment);
        if(ability.isPresent()){
            var getElement = ElementRegistry.getElementOptional(augmentStation.getInteractionSlot().get(DataComponents.CUSTOM_MODEL_DATA).value());
            var name = Component.literal(ability.get().getAbilityName());
            getElement.ifPresent(element -> renderNameTag(name, pPoseStack, pBuffer, element.textColourPrimary()));
        }
        focusedItem(pPoseStack, augmentStation, itemRenderer, pBuffer, pPackedLight);
    }

    private void focusedItem(PoseStack pPoseStack, AugmentModificationEntity pBlockEntity, ItemRenderer itemRenderer, MultiBufferSource pBuffer, int packedLight){
        pPoseStack.pushPose();
        var scaleItem = 0.40f;
        var getState = pBlockEntity.getBlockState().getValue(FACING).getOpposite();
        var N = getState == Direction.NORTH;
        var S = getState == Direction.SOUTH;
        var E = getState == Direction.EAST;
        var W = getState == Direction.WEST;

        pPoseStack.translate(W ? 0.53f : E ? 0.47f : 0.5f, 0.88f, N ? 0.53f : S ? 0.47 : 0.5f);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        pPoseStack.mulPose(getState.getRotation());
        pPoseStack.mulPose(Axis.XP.rotationDegrees(-23));

        ItemStack itemStack = pBlockEntity.inputItemHandler.getStackInSlot(0);
        itemRenderer.renderStatic(
            itemStack, ItemDisplayContext.FIXED, packedLight,
            OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1
        );

        pPoseStack.popPose();
    }

    protected void renderNameTag(Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int textColour) {
        var entity = this.entityRenderDispatcher.camera.getEntity();
        double d0 = this.entityRenderDispatcher.cameraHitResult.distanceTo(entity);

        if (entity instanceof Player player && player.isCreative() && d0 < 15) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 1.2, 0.2);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
            pPoseStack.scale(0.01f, 0.01f, 0.01f);
            var matrix4f = pPoseStack.last().pose();
            var font = Minecraft.getInstance().font;
            var f1 = (float) -font.width(pDisplayName) / 2;
            font.drawInBatch(pDisplayName, f1, 0, textColour, false, matrix4f, pBuffer, Font.DisplayMode.SEE_THROUGH , 0, 255);
            pPoseStack.popPose();
        }
    }

}