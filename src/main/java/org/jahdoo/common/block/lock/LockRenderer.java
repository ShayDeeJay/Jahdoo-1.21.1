package org.jahdoo.common.block.lock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import org.jahdoo.ascension.boon.Boon;
import org.jahdoo.ascension.boon.BoonSelection;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.shopping_table.DisplayDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.min;
import static net.minecraft.client.gui.Font.DisplayMode.NORMAL;
import static net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH;
import static net.minecraft.core.Direction.*;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.WEST;
import static net.minecraft.network.chat.Component.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;

public class LockRenderer implements BlockEntityRenderer<LockBlockEntity>{

    private static final Logger log = LoggerFactory.getLogger(LockRenderer.class);

    public LockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(LockBlockEntity entity, float v, PoseStack pose, MultiBufferSource source, int light, int overlay) {
        var adjustY = -0.2F;
        var x = 0.5F - adjustY;
        var facing = entity.getBlockState().getValue(LockBlock.FACING);
        var direction = DisplayDirection.fromMCDirection(facing.getOpposite());
        var mc = Minecraft.getInstance();
        var player = mc.player;
        var font = mc.font;

        if(player != null && player.distanceToSqr(entity.getBlockPos().getCenter()) < 2500){
            var getBoon = entity.getBoon;
            renderName(entity.roomId, pose, source, -1, font, 0.04F, 3.35F - adjustY, true, facing, direction);
            renderNewLine(font, pose, source, getBoon.label(), getBoon.icon(), x, facing, direction, 0.3F, light);
//            renderNewLine(font, pose, source, Helpers.withStyleComponent("+10% Coin Drops", MAGNET_RANGE_GREEN), SILVER_COIN, -0.4F + x, facing, direction, 0.4F, light);
//            renderNewLine(font, pose, source, literal("+50% Mob"), GOLD_COIN, -0.8F + x, facing, direction);
        }
    }

    private void renderNewLine(
        Font font,
        PoseStack poseStack,
        MultiBufferSource source,
        Component info,
        ResourceLocation location,
        float spacer,
        Direction direction,
        DisplayDirection displayDirection,
        float scale,
        int light
    ) {
        poseStack.pushPose();
        var v1 = 1.1;
        var v2 = 0.055;
        var adjustX = direction == NORTH ? v1 : direction == SOUTH ? -v1 : direction == EAST ? v2 : -v2;
        var adjustY = direction == NORTH ? v2 : direction == SOUTH ? -v2 : direction == WEST ? v1 : -v1;
        poseStack.translate(displayDirection.x() - adjustX, 2.17 + spacer, displayDirection.z() + adjustY);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()).invert());
        poseStack.mulPose(Axis.XP.rotationDegrees(270));
        drawTexture(poseStack.last(), source, light, 2, location, -1);
        poseStack.popPose();
        renderName(info, poseStack, source, -1, font, 0.02F,  2.25F + spacer, false, direction, displayDirection);
    }


    protected void renderName(
        Component name,
        PoseStack poseStack,
        MultiBufferSource source,
        int textColour,
        Font font,
        float scale,
        float height,
        boolean centre,
        Direction direction,
        DisplayDirection displayDirection
    ) {

        var v2 = 0.01;
        var adjustX = direction == EAST ? -v2 : direction == WEST ? v2 : 0;
        var adjustZ = direction == NORTH ? v2 : direction == SOUTH ? -v2 : 0;
        poseStack.pushPose();
        poseStack.translate(displayDirection.x() + adjustX, height, displayDirection.z() + adjustZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()).invert());
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.scale(scale, scale, scale);
        var center = (float) -font.width(name) / 2;
        font.drawInBatch(name, centre ? center : -42, 0, textColour, false, poseStack.last().pose(), source, NORMAL, 0, 255);
        poseStack.popPose();
    }

}