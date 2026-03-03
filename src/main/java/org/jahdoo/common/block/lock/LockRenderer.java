package org.jahdoo.common.block.lock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.block.shopping_table.DisplayDirection;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.level_manager.RoomData;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static net.minecraft.client.gui.Font.DisplayMode.NORMAL;
import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.effect.MobEffects.*;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;
import static org.jahdoo.trial_nexus.trackable.level_modifiers.AbstractLevelBoon.SyncableData.EMPTY;
import static org.jahdoo.trial_nexus.trackable.player_boons.BoonSelection.iconFromEffect;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.getFromName;

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
            newRoomSelection(entity, pose, source, light, player, font, adjustY, facing, direction, x);
        } else {
            difficultyPick(entity, pose, source, facing, font, adjustY, direction);
        }
    }

    private void difficultyPick(LockBlockEntity entity, PoseStack pose, MultiBufferSource source, Direction facing, Font font, float adjustY, DisplayDirection direction) {
        if(entity.getDifficulty.isEmpty()) return;
        var getDifficulty = getFromName(entity.getDifficulty);
        var formattedName = TextHelpers.stringIdToName(getDifficulty.getSerializedName());
        var directionA = DisplayDirection.fromMCDirection(facing.getOpposite());
        var isLook = facing == EAST || facing == WEST;

        pose.pushPose();
        pose.translate(directionA.x() + (isLook ? 0.2 : 0), 0.5, directionA.z() - (facing == SOUTH ? 0.2 :  0));
        pose.rotateAround(Axis.YP.rotationDegrees(isLook ? -90 : 0) , 0, 0, 0); // Horizontal rotation
        pose.rotateAround(Axis.XP.rotationDegrees(-90), 0,0,0); // Horizontal rotation
//        drawTexture(pose.last(), source, 255, 1F, getFromName(entity.getDifficulty).getIcon(), -1);
        pose.popPose();
        var isInit = entity.hasDifficulty();

        if(isInit){
            renderName(TextHelpers.withStyleComponent("Start with", getDifficulty.getColor()), pose, source, -1, font, 0.025F, 3.5F - adjustY, true, facing, direction, false);
            switch (getDifficulty.getSerializedName()) {
                case "novice" -> noviceRun(pose, source, facing, font, direction, getDifficulty.getColor());
                case "expert" -> expertRun(pose, source, facing, font, direction, getDifficulty.getColor());
                case "master" -> masterRun(pose, source, facing, font, direction, getDifficulty.getColor());
            }
        }

        renderName(TextHelpers.withStyleComponent(isInit ? formattedName : "", getDifficulty.getColor()), pose, source, -1, font, 0.05F, 4F - adjustY, true, facing, direction, false);
    }

    private void noviceRun(PoseStack pose, MultiBufferSource source, Direction facing, Font font, DisplayDirection direction, int colour) {
        pose.pushPose();
        var z = 0.6F;
        pose.scale(z, z, z);
        pose.translate(0.1, 0.9, -0.15);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+5 Horde", colour), Icons.HORDE, 0.5F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("20 Minute Timer", colour), Icons.CLOCK, 0.1F, facing, direction, 0.2F, 255);
        pose.popPose();
    }

    private void expertRun(PoseStack pose, MultiBufferSource source, Direction facing, Font font, DisplayDirection direction, int colour) {
        pose.pushPose();
        var z = 0.6F;
        pose.scale(z, z, z);
        pose.translate(0.75, 1.3, 0.1);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+10 Horde", colour), Icons.HORDE, 0.5F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("15 Minute Timer", colour), Icons.CLOCK, 0.1F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+10% Mob Health", colour), iconFromEffect(REGENERATION), -0.3F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+15% Mob Damage", colour),  iconFromEffect(DAMAGE_BOOST), -0.7F, facing, direction, 0.2F, 255);
        pose.popPose();
    }

    private void masterRun(PoseStack pose, MultiBufferSource source, Direction facing, Font font, DisplayDirection direction, int colour) {
        pose.pushPose();
        var z = 0.6F;
        pose.scale(z, z, z);
        pose.translate(0.5, 1.7, 0.75);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+10 Horde",colour), Icons.HORDE, 0.5F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+10 Skeleton", colour), Icons.SKELETON, 0.1F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("10 Minute Timer", colour), Icons.CLOCK, -0.3F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+100% Mob Health", colour), iconFromEffect(REGENERATION), -0.7F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+100% Mob Damage", colour), iconFromEffect(DAMAGE_BOOST), -1.1F, facing, direction, 0.2F, 255);
        renderNewLine(font, pose, source, TextHelpers.withStyleComponent("+15% Mob Speed", colour), iconFromEffect(MOVEMENT_SPEED), -1.5F, facing, direction, 0.2F, 255);
        pose.popPose();
    }

    private void newRoomSelection(LockBlockEntity entity, PoseStack pose, MultiBufferSource source, int light, LocalPlayer player, Font font, float adjustY, Direction facing, DisplayDirection direction, float x) {
        if (entity.isInitialized() && player != null) {
            var distance = player.distanceToSqr(entity.getBlockPos().getCenter());
            if (distance < 2500) {
                var mainHandItem = player.getMainHandItem();
                var lockKey = RoomData.isLockKey(mainHandItem);

                var id1 = lockKey ? RoomData.getByItem(mainHandItem).getComponent() : entity.roomId ;
                var getIcon = RoomData.roomIcon(id1.getString());
                var textColour = id1.getStyle().getColor().getValue();

                renderName(TextHelpers.withStyleComponent(getIcon, textColour), pose, source, -1, font, 0.05F, 4F - adjustY, true, facing, direction, false);
                renderName(id1, pose, source, -1, font, 0.04F, 3.35F - adjustY, true, facing, direction, false);
                var hasNegative = !Objects.equals(entity.negativeBoon, EMPTY);
                if (hasNegative) {
                    renderNewLine(font, pose, source, entity.negativeBoon.label(), entity.negativeBoon.icon(), x, facing, direction, 0.2F, light);
                }

                if (!Objects.equals(entity.positiveBoon, EMPTY)) {
                    renderNewLine(font, pose, source, entity.positiveBoon.label(), entity.positiveBoon.icon(), x - (hasNegative ? 0.6F : 0), facing, direction, 0.2F, light);
                }
            }
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
        renderName(info, poseStack, source, -1, font, 0.02F,  2.25F + spacer, false, direction, displayDirection, false);
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
        DisplayDirection displayDirection,
        boolean shadow
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

        font.drawInBatch(name, centre ? center : -42, 0, textColour, shadow, poseStack.last().pose(), source, NORMAL, 0, FULL_BRIGHT);
        poseStack.popPose();
    }


}