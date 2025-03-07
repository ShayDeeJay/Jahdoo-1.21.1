package org.jahdoo.common.block.challange_altar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static java.lang.Math.*;
import static net.minecraft.client.gui.Font.*;
import static net.minecraft.client.gui.Font.DisplayMode.*;
import static net.minecraft.client.renderer.blockentity.BeaconRenderer.*;
import static net.minecraft.util.FastColor.*;
import static net.minecraft.util.FastColor.ARGB32.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;

public class ChallengeAltarRenderer extends GeoBlockRenderer<ChallengeAltarBlockEntity>{

    public static final ResourceLocation BEAM_LOCATION = res("textures/entity/beacon_beam.png");

    public ChallengeAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new ChallengeAltarModel());
    }

    private static void activeBeam(
        PoseStack poseStack,
        ChallengeAltarBlockEntity entity,
        MultiBufferSource source,
        float partialTick,
        long i,
        boolean isActivated
    ) {
        var rad = isActivated ? ((float) entity.privateTicks / 1000) : 0.12F;
        var height = isActivated ? entity.privateTicks : 500;
        var colourLight = getColourLight(PERK_GREEN, min(max((double) entity.privateTicks / 40, 0.5), 1.2));
        var textureScale = isActivated ? 0 : 0.5F;

        renderBeaconBeam(poseStack, source, BEAM_LOCATION, partialTick, textureScale, i, 0, height, colourLight, rad, rad * 6);
    }

    public static void renderTextOverBlock(
        PoseStack poseStack,
        MultiBufferSource buffer,
        String text,
        BlockPos pos,
        double offset,
        float scale
    ) {
        poseStack.pushPose();
        renderFloatingText(poseStack, buffer, text, pos, -1, scale, true, scale, true, offset);
        poseStack.popPose();
    }

    private static void debugRound(
        PoseStack poseStack,
        ChallengeAltarBlockEntity entity,
        MultiBufferSource source,
        boolean isComplete,
        ChallengeLevelData data
    ) {
        var scale = 0.02F;

        if(!isComplete){
            renderTextOverBlock(poseStack, source, "Round: " + data.round, entity.getBlockPos(), 0, scale);
            renderTextOverBlock(poseStack, source, "Allowed Total " + data.maxMobs(), entity.getBlockPos(), 0.2, scale);
            renderTextOverBlock(poseStack, source, "Allowed Map " + data.maxMobsOnMap(), entity.getBlockPos(), 0.4, scale);
            renderTextOverBlock(poseStack, source, "Killed " + data.killedMobs, entity.getBlockPos(), 0.6, scale);
            renderTextOverBlock(poseStack, source, "On Map " + data.activeMobs.size(), entity.getBlockPos(), 0.8, scale);
        } else {
            renderTextOverBlock(poseStack, source, "Complete!", entity.getBlockPos(), 0.8, scale);
        }
    }

    private static void activationNova(PoseStack poseStack, ChallengeAltarBlockEntity entity, MultiBufferSource source, boolean active) {
        if(active) entity.animateTick = 0;
        if(entity.privateTicks >= 120) entity.animateTick = 0;
        var texture = res("textures/entity/shield.png");
        var getColour = color(max(0, (50 - (entity.privateTicks - 94) * 2)), PERK_GREEN);

        entity.animateTick += 3;
        poseStack.pushPose();
        poseStack.translate(0,-1,0);
        drawTexture(poseStack.last(), source, 255, (float) entity.animateTick, texture, getColour);
        poseStack.popPose();
    }

    public static void renderFloatingText(
        PoseStack poseStack,
        MultiBufferSource source,
        String text,
        BlockPos pos,
        int color,
        float scale,
        boolean unknown,
        float scale2,
        boolean transparent,
        double offset
    ) {
        var minecraft = Minecraft.getInstance();
        var camera = minecraft.gameRenderer.getMainCamera();
        var light = 15728880;
        var mode = transparent ? SEE_THROUGH : NORMAL;

        if (camera.isInitialized()) {
            minecraft.getEntityRenderDispatcher();
            Font font = minecraft.font;
            var d0 = camera.getPosition().x;
            var d1 = camera.getPosition().y;
            var d2 = camera.getPosition().z;
            poseStack.pushPose();
            poseStack.translate((float) (pos.getX() - d0 + 0.5), (float) (pos.getY() - d1) + 4.5F + offset, (float) (pos.getZ() - d2 + 0.5));
            poseStack.mulPose(camera.rotation());
            poseStack.scale(scale, -scale, scale);
            var f = unknown ? (float) (-font.width(text)) / 2.0F : 0.0F;
            f -= scale2 / scale;
            font.drawInBatch(text, f, 0.0F, color, false, poseStack.last().pose(), source, mode, 0, light);
            poseStack.popPose();
        }
    }

    @Override
    public void actuallyRender(
        PoseStack poseStack,
        ChallengeAltarBlockEntity entity,
        BakedGeoModel model,
        RenderType renderType,
        MultiBufferSource source,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        var properties = ChallengeLevelData.getProperties(entity);
        var isComplete = ChallengeLevelData.isCompleted(entity);
        var level = entity.getLevel();
        if(level == null) return;
        var i = level.getGameTime();

        poseStack.pushPose();
        poseStack.translate(-0.5, 0, -0.5);
        var isActivated = entity.privateTicks < 95;
        activationNova(poseStack, entity, source, isActivated);
        if(isActivated) activeBeam(poseStack, entity, source, partialTick, i, isActivated);
        poseStack.popPose();

        debugRound(poseStack, entity, source, isComplete, properties);
        super.actuallyRender(poseStack, entity, model, renderType, source, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}

