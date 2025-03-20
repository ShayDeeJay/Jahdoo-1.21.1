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
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.shopping_table.DisplayDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static net.minecraft.client.gui.Font.DisplayMode.NORMAL;
import static net.minecraft.core.Direction.*;
import static org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon.SyncableData.EMPTY;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;

public class LockRenderer implements BlockEntityRenderer<LockBlockEntity>{

    private static final Logger log = LoggerFactory.getLogger(LockRenderer.class);

    public LockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(LockBlockEntity entity, float v, PoseStack pose, MultiBufferSource source, int light, int overlay) {
        var adjustY = 0.3F;
        var x = 0.35F - adjustY;
        var facing = entity.getBlockState().getValue(LockBlock.FACING);
        var direction = DisplayDirection.fromMCDirection(facing.getOpposite());
        var mc = Minecraft.getInstance();
        var player = mc.player;
        var font = mc.font;

        if(!entity.isStartingRoom()){
            if (entity.isInitialized() && player != null && player.distanceToSqr(entity.getBlockPos().getCenter()) < 2500) {
                var id = entity.roomId.getString();
                var getIcon = id.contains("Boss") ? "☠" : id.contains("The") ? "⚔" : id.contains("Sanctuary") ? "\uD83E\uDDEA" : "⇵";
                var textColour = entity.roomId.getStyle().getColor().getValue();

                renderName(Helpers.withStyleComponent(getIcon, textColour), pose, source, -1, font, 0.05F, 4F - adjustY, true, facing, direction);
                renderName(entity.roomId, pose, source, -1, font, 0.04F, 3.35F - adjustY, true, facing, direction);
                renderNewLine(font, pose, source, entity.negativeBoon.label(), entity.negativeBoon.icon(), x, facing, direction, 0.2F, light);

                if (!Objects.equals(entity.positiveBoon, EMPTY)) {
                    renderNewLine(font, pose, source, entity.positiveBoon.label(), entity.positiveBoon.icon(), x - 0.6F, facing, direction, 0.2F, light);
                }
            }
        } else {
            renderName(Helpers.withStyleComponent(entity.getDifficulty, ColourStore.PERK_GREEN), pose, source, -1, font, 0.04F, 3.35F - adjustY, true, facing, direction);
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
        var center = (float) -font.width(name) / 2 + 0.6F;

        font.drawInBatch(name, centre ? center : -42, 0, textColour, false, poseStack.last().pose(), source, NORMAL, 0, 255);
        poseStack.popPose();
    }

}