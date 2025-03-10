package org.jahdoo.common.block.altar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static net.minecraft.client.renderer.blockentity.BeaconRenderer.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;

public class AltarRenderer extends GeoBlockRenderer<AltarBlockEntity>{

    public static final ResourceLocation BEAM_LOCATION = res("textures/entity/beacon_beam.png");

    public AltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new AltarModel());
    }

    private static void activeBeam(
        PoseStack poseStack,
        AltarBlockEntity entity,
        MultiBufferSource source,
        float partialTick,
        long i
    ) {
        var rad =  0.1F;
        var height = 500;
        var colourLight = getColourLight(PERK_GREEN, 1.2);

        renderBeaconBeam(poseStack, source, BEAM_LOCATION, partialTick, 0.2F, i, 0, height, colourLight, rad, rad * 6);
    }

    @Override
    public boolean shouldRenderOffScreen(AltarBlockEntity blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(AltarBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }



    @Override
    public void actuallyRender(
        PoseStack poseStack,
        AltarBlockEntity entity,
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
        var level = entity.getLevel();
        if(level == null) return;
        var i = level.getGameTime();

        poseStack.pushPose();
        poseStack.translate(-0.5, 0, -0.5);
        if(entity.started) activeBeam(poseStack, entity, source, partialTick, i);
        poseStack.popPose();

        super.actuallyRender(poseStack, entity, model, renderType, source, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}

