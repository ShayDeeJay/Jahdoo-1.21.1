package org.jahdoo.common.block.lock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.shopping_table.DisplayDirection;

import static net.minecraft.client.gui.Font.DisplayMode.NORMAL;
import static net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH;
import static org.jahdoo.common.block.challange_altar.ChallengeAltarRenderer.renderFloatingText;
import static org.jahdoo.common.block.challange_altar.ChallengeAltarRenderer.renderTextOverBlock;
import static org.jahdoo.common.block.shopping_table.DisplayDirection.*;
import static org.jahdoo.common.block.shopping_table.DisplayDirection.NORTH;

public class LockRenderer implements BlockEntityRenderer<LockBlockEntity>{

    public LockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(LockBlockEntity lockBlockEntity, float v, PoseStack poseStack, MultiBufferSource source, int i, int i1) {
        renderCostText(Component.literal("Room"), poseStack, source, -1, lockBlockEntity);
    }

    protected void renderCostText(
        Component name,
        PoseStack poseStack,
        MultiBufferSource source,
        int textColour,
        LockBlockEntity lockBlockEntity
    ) {
        var z = 0.04f;
        var direction = lockBlockEntity.getBlockState().getValue(LockBlock.FACING);
        var pos = DisplayDirection.fromMCDirection(direction.getOpposite());
        var instance = Minecraft.getInstance();
        var player = instance.player;
        var font = instance.font;
        var blockPos = lockBlockEntity.getBlockPos().getCenter();
        if(player != null && player.distanceToSqr(blockPos) < 2000){
            poseStack.pushPose();

            poseStack.translate(pos.x(), 1.78, pos.z());
            poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()).invert());
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));

            poseStack.scale(z, z, z);
            var matrix4f = poseStack.last().pose();
            var f1 = (float) -font.width(name) / 2;
            font.drawInBatch(name, f1, 0, textColour, false, matrix4f, source, SEE_THROUGH, 0, 255);
            poseStack.popPose();
        }
    }

}