package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.MutableComponent;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.client.Icons;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mojang.datafixers.util.Pair.of;
import static java.lang.String.valueOf;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
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

        if(player == null || mc.options.hideGui || level == null) return;

        slideGuiStats();
        if(level.getDescription().getString().contains("ascension")){
            levelData(graphics, level, mc, instanceData, mc.screen);
        }
    }

    private void levelData(GuiGraphics graphics, ClientLevel level, Minecraft mc, InstanceData currentData, Screen screen) {
        var data = level.getData(INSTANCE_DATA);
        var size = 18;
        var spacer = 0;
        var offsetX = -14 + fade;
        var offsetY = 24;
        var sizeB = 1.8F;
        var remainingTime = data.getMaxTime() - data.getTicks();
        var pose = graphics.pose();
        var prefix = "textures/item/";

        timer = Math.max(0, timer - 1);
        instanceData = data;

        pose.pushPose();
        pose.scale(sizeB, sizeB, sizeB);
        graphics.drawString(mc.font, withStyleComponent("Current Run", COSMIC_PURPLE), (int) (10 + offsetX), 33 + offsetY, -1, true);
        pose.popPose();

        var getComps = List.of(
//            of(appendStat("Horde Killed: ", valueOf(data.getHorde()), MAGNET_RANGE_GREEN), HORDE),
//            of(appendStat("Skeletons Killed: ", valueOf(data.getSkeleton()), MAGNET_RANGE_GREEN), SKELETON),
//            of(appendStat("Wizards Killed: ", valueOf(data.getEternalWizard()), MAGNET_RANGE_GREEN), ETERNAL_WIZARD),
//            of(appendStat("Spiders Killed: ", valueOf(data.getVoidSpider()), MAGNET_RANGE_GREEN), VOID_SPIDER),
            of(appendStat("Rooms Completed: ", valueOf(data.getClearedRooms()), MAGNET_RANGE_GREEN), Icons.UP),
            of(appendStat("Time: ", Maths.ticksToTime(valueOf(remainingTime)), remainingTime > 400 ? MAGNET_RANGE_GREEN : NEGATIVE_RED), Icons.CLOCK)
        );

        for (var getComp : getComps) {
            graphics.blit(getComp.getSecond(), (int) (12 + offsetX), 94 + spacer + offsetY, 0, 0, size, size, size, size);
            graphics.drawString(mc.font, getComp.getFirst(), (int) (28 + offsetX), 100 + spacer + offsetY, -1, true);
            spacer += 18;
        }

        if(currentData != instanceData) timer = 400;
        if(screen instanceof InventoryScreen) { timer = 30; }
    }

    private static @NotNull MutableComponent appendStat(String prefix, String value, int colour) {
        return withStyleComponent(prefix, SYMPATHISER_ORANGE).copy().append(withStyleComponent(value, colour));
    }

}
