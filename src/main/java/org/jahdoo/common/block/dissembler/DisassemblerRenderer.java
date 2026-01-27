package org.jahdoo.common.block.dissembler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static net.minecraft.client.renderer.texture.OverlayTexture.*;
import static net.minecraft.world.item.ItemDisplayContext.*;
import static org.jahdoo.common.block.dissembler.DisassemblerBlock.FACING;

public class DisassemblerRenderer extends GeoBlockRenderer<DisassemblerBlockEntity>{

    public DisassemblerRenderer(BlockEntityRendererProvider.Context context) {
        super(new DisassemblerModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, DisassemblerBlockEntity entity, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var item = entity.getInputAndOutputRenderer();
        var pos = new Vec3(0f, 0.7f, 0f);
        var scaleItem = 0.35f;
        this.focusedItem(poseStack, entity, itemRenderer, bufferSource, packedLight, item, pos, scaleItem);
        this.focusedItem(poseStack, entity, itemRenderer, bufferSource, packedLight, entity.outputItemHandler.getStackInSlot(1), pos, scaleItem);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    private void focusedItem(
        PoseStack poseStack,
        DisassemblerBlockEntity entity,
        ItemRenderer itemRenderer,
        MultiBufferSource source,
        int packedLight,
        ItemStack itemStack,
        Vec3 pos,
        float scaleItem
    ){
        var isScrap = itemStack.is(ItemReg.GEAR_SCRAP);
        poseStack.pushPose();
        poseStack.translate(pos.x, isScrap ? pos.y - 0.01 : pos.y + 0.01, pos.z);
        poseStack.scale(scaleItem, scaleItem, scaleItem);
        var getState = entity.getBlockState().getValue(FACING).getOpposite();

        poseStack.mulPose(getState.getRotation());
        itemRenderer.renderStatic(itemStack, FIXED, packedLight, NO_OVERLAY, poseStack, source, entity.getLevel(), 1);
        poseStack.popPose();
    }
}

