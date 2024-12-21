package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.antlr.v4.runtime.misc.Triple;
import org.jahdoo.block.crafter.CreatorEntity;
import org.jahdoo.block.infuser.InfuserBlock;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.client.block_models.InfuserModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.Objects;

import static org.jahdoo.block.infuser.InfuserBlock.FACING;

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

