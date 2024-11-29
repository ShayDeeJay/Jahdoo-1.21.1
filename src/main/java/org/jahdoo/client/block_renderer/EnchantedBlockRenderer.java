package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.enchanted_block.EnchantedBlock;
import org.jahdoo.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.block.enchanted_block.EnchantedBlock.ENCHANTMENT_STAGE;

public class EnchantedBlockRenderer implements BlockEntityRenderer<EnchantedBlockEntity>{
    private final BlockEntityRenderDispatcher entityRenderDispatcher;
    public EnchantedBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(@NotNull EnchantedBlockEntity enchantedBlockEntity, float v, PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i, int i1) {
        var mc = Minecraft.getInstance();
        var renderer = mc.getBlockRenderer();
        poseStack.pushPose();
        multiBufferSource.getBuffer(RenderType.cutout());
        renderer.renderBatched(Blocks.STONE.defaultBlockState(), enchantedBlockEntity.getBlockPos(), mc.level, poseStack, multiBufferSource.getBuffer(RenderType.solid()), false, RandomSource.create());

        renderer.renderBatched(enchantedBlockEntity.getBlockState().setValue(ENCHANTMENT_STAGE, Math.min(enchantedBlockEntity.stage, 4)), enchantedBlockEntity.getBlockPos(), mc.level, poseStack, multiBufferSource.getBuffer(RenderType.cutout()).setWhiteAlpha(100), false, RandomSource.create());
        poseStack.popPose();
//        entityRenderDispatcher.getRenderer(enchantedBlockEntity);
    }


}