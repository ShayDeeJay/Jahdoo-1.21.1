package org.jahdoo.common.block.dissembler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static net.minecraft.client.renderer.texture.OverlayTexture.*;
import static net.minecraft.world.item.ItemDisplayContext.*;
import static org.jahdoo.common.block.dissembler.DisassemblerBlock.FACING;

public class DisassemblerRenderer extends GeoBlockRenderer<DisassemblerBlockEntity>{

    public DisassemblerRenderer(BlockEntityRendererProvider.Context context) {
        super(new DisassemblerModel());
    }

    @Override
    public void render(
        DisassemblerBlockEntity entity,
        float partial,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var item = entity.getInputAndOutputRenderer();
        var pos = new Vec3(0.5f, 0.7f, 0.5f);
        this.focusedItem(poseStack, entity, itemRenderer, bufferSource, packedLight, item, pos, 0.3f);

        super.render(entity, partial, poseStack, bufferSource, packedLight, packedOverlay);
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
        poseStack.pushPose();
        poseStack.translate(pos.x, pos.y, pos.z);
        poseStack.scale(scaleItem, scaleItem, scaleItem);
        var getState = entity.getBlockState().getValue(FACING).getOpposite();

        poseStack.mulPose(getState.getRotation());
        itemRenderer.renderStatic(itemStack, FIXED, packedLight, NO_OVERLAY, poseStack, source, entity.getLevel(), 1);
        poseStack.popPose();
    }
}

