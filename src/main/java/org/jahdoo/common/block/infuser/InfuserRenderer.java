package org.jahdoo.common.block.infuser;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static org.jahdoo.common.block.infuser.InfuserBlock.FACING;

public class InfuserRenderer extends GeoBlockRenderer<InfuserBlockEntity>{

    public InfuserRenderer(BlockEntityRendererProvider.Context context) {
        super(new InfuserModel());
    }

    @Override
    public void render(InfuserBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.focusedItem(poseStack, animatable, itemRenderer, bufferSource, packedLight, animatable.getInputAndOutputRenderer(), new Vec3(0.5f, 0.7f, 0.5f), 0.3f);

        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void focusedItem(
        PoseStack pPoseStack,
        InfuserBlockEntity pBlockEntity,
        ItemRenderer itemRenderer,
        MultiBufferSource pBuffer,
        int packedLight,
        ItemStack itemStack,
        Vec3 pos,
        float scaleItem
    ){
        pPoseStack.pushPose();
        pPoseStack.translate(pos.x, pos.y, pos.z);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        var getState = pBlockEntity.getBlockState().getValue(FACING).getOpposite();
        pPoseStack.mulPose(getState.getRotation());

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

