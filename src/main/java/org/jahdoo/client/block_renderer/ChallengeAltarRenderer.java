package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.client.block_models.ChallengeAltarModel;
import org.jahdoo.client.block_models.InfuserModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static org.jahdoo.block.infuser.InfuserBlock.FACING;

public class ChallengeAltarRenderer extends GeoBlockRenderer<ChallengeAltarBlockEntity>{

    public ChallengeAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new ChallengeAltarModel());
    }

    @Override
    public void render(ChallengeAltarBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.focusedItem(poseStack, animatable, itemRenderer, bufferSource, packedLight, animatable.getInputAndOutputRenderer(), new Vec3(0.5f, 0.7f, 0.5f), 0.3f);

        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void focusedItem(
        PoseStack pPoseStack,
        ChallengeAltarBlockEntity pBlockEntity,
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

