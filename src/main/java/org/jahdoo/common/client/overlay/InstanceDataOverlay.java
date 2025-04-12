package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.AttachmentReg;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mojang.datafixers.util.Pair.of;
import static java.lang.String.valueOf;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Maths.ticksToTime;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class InstanceDataOverlay implements LayeredDraw.Layer {

    private InstanceData instanceData;
    private int timer;
    private float fade;

    private void slideGuiStats() {
        var maxFadeIn = 10.0F;
        var minFadeIn = -130.0F;
        var easeFactor = 0.1F;

        if (timer > 0) {
            var distanceToMax = maxFadeIn - this.fade;
            var fadeAmount = distanceToMax * easeFactor;
            this.fade = Math.min(this.fade + fadeAmount, maxFadeIn);
        } else {
            var distanceFromMin = this.fade - minFadeIn;
            var fadeAmount = distanceFromMin * easeFactor;
            this.fade = Math.max(this.fade - fadeAmount, minFadeIn);
        }
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        var player = mc.player;
        var screen = mc.screen;

        if(player == null || mc.options.hideGui || level == null) return;

        slideGuiStats();
        if(level.getDescription().getString().contains("ascension")){
            levelData(graphics, level, mc, instanceData, screen);
        }

        if(player.level().getDescription().getString().contains("ascension") && player.tickCount % 10 == 0){
            player.sendSystemMessage(Component.literal(player.getData(AttachmentReg.RUN_DATA).toString()));
        }
    }

    private void levelData(GuiGraphics graphics, ClientLevel level, Minecraft mc, InstanceData currentData, Screen screen) {
        var data = level.getData(INSTANCE_DATA);
        var size = 18;
        var spacer = 0;
        var offsetX = -22 + fade;
        var offsetY = -4;
        var remainingTime = data.getMaxTime() - data.getTicks();
        var pose = graphics.pose();
        var getComps = List.of(
            of(appendStat("Room's Completed: ", valueOf(data.getClearedRooms()), MAGNET_RANGE_GREEN), Icons.UP),
            of(appendStat("Time: ", ticksToTime(valueOf(remainingTime)), remainingTime > 400 ? MAGNET_RANGE_GREEN : NEGATIVE_RED), Icons.CLOCK)
        );

        timer = Math.max(0, timer - 1);
        instanceData = data;

        if(data.getDifficulty().isEmpty()) timer = 0;

        pose.pushPose();
        pose.translate(0, (float) graphics.guiHeight() - 160, 0);
        for (var getComp : getComps) {
            if(!getComp.getFirst().getSiblings().getFirst().getString().isEmpty()){
                graphics.blit(getComp.getSecond(), (int) (12 + offsetX), 94 + offsetY + spacer, 0, 0, size, size, size, size);
                graphics.drawString(mc.font, getComp.getFirst(), (int) (32 + offsetX), 100  + offsetY + spacer, -1, true);
                spacer += 14;
            }
        }
        pose.popPose();

        if(currentData != instanceData) timer = 400;
        if(screen instanceof InventoryScreen) { timer = 30; }
    }

    private static @NotNull MutableComponent appendStat(String prefix, String value, int colour) {
        return withStyleComponent(prefix, SYMPATHISER_ORANGE).copy().append(withStyleComponent(value, colour));
    }

}
