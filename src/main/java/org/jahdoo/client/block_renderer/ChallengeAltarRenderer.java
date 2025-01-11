package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.client.block_models.ChallengeAltarModel;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static org.jahdoo.client.RenderHelpers.drawTexture;

public class ChallengeAltarRenderer extends GeoBlockRenderer<ChallengeAltarBlockEntity>{
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ChallengeAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new ChallengeAltarModel());
        this.entityRenderDispatcher = context.getEntityRenderer();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ChallengeAltarBlockEntity animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        var properties = ChallengeAltarData.getProperties(animatable);
        var isComplete = ChallengeAltarData.isCompleted(animatable);
        var i = animatable.getLevel().getGameTime();
        poseStack.pushPose();
        poseStack.translate(-0.5, 0, -0.5);
        var active = ChallengeAltarData.isActive(animatable);
        System.out.println(animatable.privateTicks);
        var isActivated = animatable.privateTicks < 95;
        var rad = isActivated ? ((float) animatable.privateTicks / 1000) : 0.12F;
        if(!isActivated){
            if(animatable.privateTicks < 120){
                animatable.animateTick+=3;
                poseStack.pushPose();
                poseStack.translate(0,-1,0);
                drawTexture(poseStack.last(), bufferSource, 255, (float) animatable.animateTick, ModHelpers.res("textures/entity/shield.png"), FastColor.ARGB32.color(50, ColourStore.PERK_GREEN));
                poseStack.popPose();
            } else animatable.animateTick = 0;
        }else animatable.animateTick = 0;


        BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, active ? 0 : 2 , i, 0, isActivated ?  animatable.privateTicks : 500, ModHelpers.getColourLight(ColourStore.PERK_GREEN, Math.min(Math.max((double) animatable.privateTicks / 70, 0.5), 1.5)), rad, rad * 6);
        poseStack.popPose();
        if(!isComplete){
            renderTextOverBlock(poseStack, bufferSource, "Round: " + properties.round, animatable.getBlockPos(), 0);
            renderTextOverBlock(poseStack, bufferSource, "Allowed Total " + properties.maxMobs(), animatable.getBlockPos(), 0.2);
            renderTextOverBlock(poseStack, bufferSource, "Allowed Map " + properties.maxMobsOnMap(), animatable.getBlockPos(), 0.4);
            renderTextOverBlock(poseStack, bufferSource, "Killed " + properties.killedMobs, animatable.getBlockPos(), 0.6);
            renderTextOverBlock(poseStack, bufferSource, "On Map " + properties.activeMobs.size(), animatable.getBlockPos(), 0.8);
        } else {
            renderTextOverBlock(poseStack, bufferSource, "Complete!", animatable.getBlockPos(), 0.8);
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    private static void renderTextOverBlock(PoseStack poseStack, MultiBufferSource buffer, String text, BlockPos pos, double offset) {
        poseStack.pushPose();
        var scale = 0.02F;
        renderFloatingText(poseStack, buffer, text, pos, -1, scale, true, scale, true, offset);
        poseStack.popPose();
    }


    public static void renderFloatingText(PoseStack poseStack, MultiBufferSource bufferSource, String text, BlockPos pos, int color, float scale, boolean p_270731_, float p_270825_, boolean transparent, double offset) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        if (camera.isInitialized()) {
            minecraft.getEntityRenderDispatcher();
            Font font = minecraft.font;
            double d0 = camera.getPosition().x;
            double d1 = camera.getPosition().y;
            double d2 = camera.getPosition().z;
            poseStack.pushPose();
            poseStack.translate((float) (pos.getX() - d0 + 0.5), (float) (pos.getY() - d1) + 4.5F + offset, (float) (pos.getZ() - d2 + 0.5));
            poseStack.mulPose(camera.rotation());
            poseStack.scale(scale, -scale, scale);
            float f = p_270731_ ? (float) (-font.width(text)) / 2.0F : 0.0F;
            f -= p_270825_ / scale;
            font.drawInBatch(text, f, 0.0F, color, false, poseStack.last().pose(), bufferSource, transparent ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, 15728880);
            poseStack.popPose();
        }

    }
}

