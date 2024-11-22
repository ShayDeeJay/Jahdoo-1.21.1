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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.block.crafter.CreatorEntity;
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
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pPackedLight,
        int pPackedOverlay
    ){
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        focusedItem(pPoseStack, augmentStation, itemRenderer, pBuffer, pPackedLight);
    }

    private void focusedItem(PoseStack pPoseStack, AugmentModificationEntity pBlockEntity, ItemRenderer itemRenderer, MultiBufferSource pBuffer, int packedLight){
        pPoseStack.pushPose();
        float scaleItem = 0.40f;
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
            itemStack,
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

}