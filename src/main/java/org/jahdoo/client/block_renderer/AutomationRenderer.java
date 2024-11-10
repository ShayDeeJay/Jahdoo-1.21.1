package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.client.block_models.AutomationModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static org.jahdoo.block.infuser.InfuserBlock.FACING;

public class AutomationRenderer extends GeoBlockRenderer<AutomationBlockEntity>{

    public AutomationRenderer(BlockEntityRendererProvider.Context context) {
        super(new AutomationModel());
    }

    @Override
    public void render(AutomationBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
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

