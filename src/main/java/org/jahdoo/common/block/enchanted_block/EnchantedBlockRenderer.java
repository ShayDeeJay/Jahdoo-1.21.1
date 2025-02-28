package org.jahdoo.common.block.enchanted_block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;

import static org.jahdoo.common.block.enchanted_block.EnchantedBlock.ENCHANTMENT_STAGE;

public class EnchantedBlockRenderer implements BlockEntityRenderer<EnchantedBlockEntity>{

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public EnchantedBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(EnchantedBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        var renderer = mc.getBlockRenderer();
        poseStack.pushPose();

        if(level == null) return;
        var source = bufferSource.getBuffer(RenderType.cutout());

        source.setLight(packedLight);
        source.setOverlay(packedOverlay);

        if(entity.block != null){
            renderer.renderBatched(entity.block.defaultBlockState(), entity.getBlockPos(), level, poseStack, source, false, RandomSource.create());
        }

        var state = entity.getBlockState().setValue(ENCHANTMENT_STAGE, Math.min(entity.stage, EnchantedBlockEntity.MAX_STAGE-1));
        renderer.renderBatched(state, entity.getBlockPos(), level, poseStack, source, false, RandomSource.create());
        poseStack.popPose();
    }

}