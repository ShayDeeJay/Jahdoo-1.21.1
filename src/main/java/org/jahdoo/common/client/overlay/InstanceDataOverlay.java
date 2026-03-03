package org.jahdoo.common.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.screens.RunScreen;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jahdoo.common.registers.mod.StatEntryReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static com.mojang.blaze3d.platform.InputConstants.KEY_TAB;
import static java.lang.String.valueOf;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;
import static org.jahdoo.common.client.screens.RunScreen.componentTemplate;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.getFromName;

public class InstanceDataOverlay extends AbstractTimedOverlay {

    private InstanceData instanceData;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        super.render(graphics, deltaTracker);
        var mc = Minecraft.getInstance();
        var level = mc.level;
        var player = mc.player;
        var screen = mc.screen;
        var font = mc.font;

        if(player == null || mc.options.hideGui || level == null || screen instanceof AbstractContainerScreen) return;

        var runData = RunData.getRunData(player);
        if(LevelGenerator.isNexus(level)) {
            overlayTimer(level, screen);
            overlayInstanceModifiers(graphics, mc, player);
            overlayAlwaysOnInfo(graphics, runData, font);
        }

        overlayQuest(graphics, mc, runData, font);
    }

    private void overlayTimer(ClientLevel level, Screen screen) {
        var currentData = level.getData(INSTANCE_DATA);
        if(currentData.getDifficulty().isEmpty()) timer = 0;
        if(currentData != instanceData) timer = 400;
        if(screen instanceof InventoryScreen) timer = 30;

        instanceData = currentData;
    }

    private void overlayAlwaysOnInfo(GuiGraphics graphics, RunData runData, Font font) {

        var x = 8;
        var size = 18;
        var remainingTime = instanceData.getMaxTime() - instanceData.getTicks();
        var startY = 20;
        var barColour = ColourHelpers.colourByPercent(instanceData.getMaxTime(), remainingTime, true);
        var borderColour = ColourHelpers.getNetheriteBox();
        var colour = remainingTime > 400 ? ColourHelpers.getMagnetRangeGreen() : ColourHelpers.getNegativeRed();
        var time = appendStat("", MathHelpers.ticksToTime(valueOf(remainingTime)), colour);

        if(instanceData.getMaxTime() > 0){
            progressBar(graphics, 8, startY, 38, 4, remainingTime, instanceData.getMaxTime(), 2, barColour, borderColour, uiFade());
            alwaysOnEntry(graphics, Icons.CLOCK, x - 2, startY - 18, size, -1, font, time);

            if(!ClientHelpers.isKeyDown(KEY_TAB)){
                var scale = 0.5F;
                graphics.pose().pushPose();
                graphics.pose().scale(scale, scale, scale);
                graphics.pose().translate(-8, 34, 0);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1, 1, 1, 0.5F);
                alwaysOnEntry(
                    graphics,
                    Icons.BLANK, x, startY, size, -1,
                    font,
                    TextHelpers.displaySelectedKey(KEY_TAB)
                );
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
                graphics.pose().popPose();
            }
        }

        var all = StatEntryReg.getAll();
        RunScreen.drawPlayerBoonList(graphics, all, runData,  x, (int) (fadeIn * ((float) all.size() /10)) + 2, 22);

    }

    public static void alwaysOnEntry(
        GuiGraphics graphics,
        ResourceLocation icon,
        int x,
        int y,
        int size,
        int colour,
        Font font,
        Component info
    ){
        graphics.blit(icon, x, y, 0, 0, size, size, size, size);
        graphics.drawString(font, info, x + 18, y + 6, colour, false);
    }

    private void overlayQuest(GuiGraphics graphics, Minecraft mc, RunData runData, Font font) {
        var questId = runData.getCurrentQuestId();
        var getQuest = QuestReg.getQuestByName(questId);

        if(getQuest.isPresent()){
            var baseWidth = 60;
            var offsetX = graphics.guiWidth()/2 - baseWidth;
            var quest = getQuest.get();
            var current = runData.getStat(questId);
            var needed = quest.questQuantity(mc.player);
            var isComplete = current >= needed;
            var display = isComplete ? "Quest Complete" : TextHelpers.stringIdToName(quest.questName()) + " "+ current + "/" + needed;
            var colour = isComplete ? ColourHelpers.getPerkGreen() : ColourHelpers.getOffWhite();
            var startY = 20;
            var height = 4;
            var offset = 3;
            var colourBorder = quest.questColour();
            progressBar(graphics, offsetX, startY, baseWidth, height, current, needed, offset, colourBorder, uiColour(), uiFade());
            graphics.drawCenteredString(font, appendStat("Quest: ", quest.getDisplayName(), quest.questColour()), offsetX + baseWidth, startY - 12, -1);
            graphics.drawCenteredString(font, TextHelpers.withStyleComponent(display, colour), offsetX + baseWidth, startY + (height * 2) + 3, -1);
        }
    }

    private void overlayInstanceModifiers(GuiGraphics graphics, Minecraft mc, LocalPlayer player) {
        var height = graphics.guiHeight();
        var width = graphics.guiWidth();

        if(!instanceData.getDifficulty().isEmpty()) {
            SharedUI.boxMaker(graphics, 0, 0, width, height, 0, SharedUI.fadeBlack(getMax(0.8F)));
        }

        if (!ClientHelpers.isKeyDown(KEY_TAB)) {
            timer = 0;
            return;
        }

        var runData = RunData.getRunData(player);
        var getQuest = QuestReg.getQuestByName(runData.getCurrentQuestId());

        final int spacer = 50;
        final int startY = (int) (fadeIn) + (getQuest.isPresent() ? spacer : 0);
        final var startX = width / 2 - 150 ;

        var difficultyId = TextHelpers.stringIdToName(instanceData.getDifficulty());
        var difficultyName = difficultyId.isEmpty() ? "Unselected" : difficultyId;
        var difficultyColor = difficultyId.isEmpty() ? ColourHelpers.getOffWhite() : getFromName(difficultyName).getColor();
        var header = new RunScreen.StatEntry(componentTemplate("Difficulty", difficultyName, difficultyColor), null);

        graphics.pose().pushPose();
        graphics.pose().scale(2, 2, 2);
        graphics.drawCenteredString(mc.font, header.component(), width / 4 , (int) (fadeIn) + (getQuest.isPresent() ? (spacer/2) : 0), -1);
        graphics.pose().popPose();

        var level = mc.level;
        if(level != null) {
            var data = level.getData(INSTANCE_DATA);
            RunScreen.instanceInfo(graphics, spacer, startX, startY, data);
        }
    }

    private float getMax(float maxAlpha) {
        var fade1 = Math.min(Math.abs(fadeIn/220), 1);
        var v =  Math.min(maxAlpha, 1F - fade1);
        return Math.max(0, v);
    }

    public static void progressBar(GuiGraphics graphics, int offsetX, int startY, int baseWidth, int height, int current, int needed, int offset, int barColour, int containerBorder, int backgroundColour) {
        SharedUI.boxMaker(graphics, offsetX, startY, baseWidth, height, containerBorder, backgroundColour, backgroundColour);
        if (current > 0) {
            var progressRatio = (float) Math.min(current, needed) / (float) needed;
            var barWidth = Math.round((baseWidth - offset) * progressRatio);
            SharedUI.boxMaker(graphics, offsetX + offset, startY + offset, Math.max(barWidth, 1), height - offset, barColour, barColour, barColour);
        }
    }

    private static @NotNull MutableComponent appendStat(String prefix, String value, int colour) {
        return TextHelpers.withStyleComponent(prefix, ColourHelpers.getSubHeaderColour()).copy().append(TextHelpers.withStyleComponent(value, colour));
    }

}
