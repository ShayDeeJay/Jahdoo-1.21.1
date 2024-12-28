package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.client.block_models.ChallengeAltarModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ChallengeAltarRenderer extends GeoBlockRenderer<ChallengeAltarBlockEntity>{
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ChallengeAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new ChallengeAltarModel());
        this.entityRenderDispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(ChallengeAltarBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.focusedItem(poseStack, animatable, itemRenderer, bufferSource, packedLight, animatable.getInputAndOutputRenderer(), new Vec3(0.5f, 0.7f, 0.5f), 0.3f);
        var roundGen = animatable.getRoundGenerator();
        if(roundGen != null){
            renderTextOverBlock(poseStack, bufferSource, "Round: " + roundGen.getRound(), animatable.getBlockPos(), 0);
            renderTextOverBlock(poseStack, bufferSource, "Remaining " + animatable.totalEntities, animatable.getBlockPos(), 1);
            renderTextOverBlock(poseStack, bufferSource, "Remaining " + animatable.totalEntities, animatable.getBlockPos(), 1);
        }
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

    private static void renderTextOverBlock(PoseStack poseStack, MultiBufferSource buffer, String text, BlockPos pos, int offset) {
        double d0 = pos.getCenter().x + (double)0.5F;
        double d1 = pos.getCenter().y + 4.3 + offset;
        double d2 = pos.getCenter().z + (double)0.5F;

        poseStack.pushPose();
        var scale = 0.03F;
        DebugRenderer.renderFloatingText(poseStack, buffer, text, d0, d1, d2, -1, scale, true, scale, true);
        poseStack.popPose();
    }
}

