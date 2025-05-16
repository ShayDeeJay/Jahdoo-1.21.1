package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.MutableComponent;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.quests.AbstractQuest;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static java.lang.String.valueOf;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;
import static org.jahdoo.trial_nexus.utils.Maths.ticksToTime;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;
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
        if(level.getDescription().getString().contains(LevelGenerator.LEVEL_PREFIX)){
            levelData(graphics, level, mc, instanceData, screen);
        }
    }

    private void levelData(GuiGraphics graphics, ClientLevel level, Minecraft mc, InstanceData currentData, Screen screen) {
        var getInstanceData = level.getData(INSTANCE_DATA);
        var getQuest = allInstanceOverlays(graphics, mc, getInstanceData);


        timer = Math.max(0, timer - 1);
        instanceData = getInstanceData;

        if(getInstanceData.getDifficulty().isEmpty() && getQuest.isEmpty()) {
            timer = 0;
        }

        if(currentData != instanceData) timer = 400;
        if(screen instanceof InventoryScreen) { timer = 30; }
    }

    private @NotNull Optional<AbstractQuest> allInstanceOverlays(GuiGraphics graphics, Minecraft mc, InstanceData getInstanceData) {
        var getRunData = mc.player.getData(AttachmentReg.RUN_DATA.get());
        var questId = getRunData.getCurrentQuestId();
        var getQuest = QuestReg.getQuestByName(questId);
        var size = 18;
        var remainingTime = getInstanceData.getMaxTime() - getInstanceData.getTicks();
        var roomsCleared = getInstanceData.getClearedRooms();
        var clockX = 8;
        var font = mc.font;

        if(getQuest.isPresent()){
            var baseWidth = 60;
            var offsetX = graphics.guiWidth()/2 - baseWidth;
            var offsetY = (int) fade - 20;
            var quest = getQuest.get();
            var current = getRunData.getStat(questId);
            var needed = quest.questQuantity(mc.player);
            var isComplete = current >= needed;
            var display = isComplete ? "Quest Complete" : current + "/" + needed;
            var colour = isComplete ? PERK_GREEN : OFF_WHITE;
            var startY = offsetY + 30;
            var height = 4;
            var offset = 3;
            var colourBorder = quest.questColour();

            progressBar(graphics, offsetX, startY, baseWidth, height, current, needed, offset, colourBorder, uiColour());

            graphics.drawCenteredString(font, appendStat("Quest: ", quest.getDisplayName(), quest.questColour()), offsetX + baseWidth, startY - 12, -1);
            graphics.drawCenteredString(font, withStyleComponent(display, colour), offsetX + baseWidth, startY + (height * 2) + 3, -1);
        }

        graphics.blit(Icons.CLOCK, clockX - 3, (int) (fade), 0, 0, size, size, size, size);
        graphics.drawString(font, appendStat("", ticksToTime(valueOf(remainingTime)), remainingTime > 400 ? MAGNET_RANGE_GREEN : NEGATIVE_RED), clockX + 16, (int) (6 + fade), -1, false);

        var i = 65;
        graphics.blit(Icons.ROOMS_CLEARED, clockX - 3 + i, (int) (fade), 0, 0, size, size, size, size);
        graphics.drawString(font, "" + roomsCleared, clockX + 16 + i, (int) (6 + fade), -1, false);
        return getQuest;
    }

    public static void progressBar(GuiGraphics graphics, int offsetX, int startY, int baseWidth, int height, int current, int needed, int offset, int barColour, int containerBorder) {
        SharedUI.boxMaker(graphics, offsetX, startY, baseWidth, height, containerBorder, uiFade(), uiFade());
        if (current > 0) {
            var progressRatio = (float) Math.min(current, needed) / (float) needed;
            var barWidth = (baseWidth - offset) * progressRatio;
            SharedUI.boxMaker(graphics, offsetX + offset, startY + offset, Math.max((int) barWidth, 1), height - offset, barColour, barColour, barColour);
        }
    }

    private static @NotNull MutableComponent appendStat(String prefix, String value, int colour) {
        return withStyleComponent(prefix, SUB_HEADER_COLOUR).copy().append(withStyleComponent(value, colour));
    }

}
