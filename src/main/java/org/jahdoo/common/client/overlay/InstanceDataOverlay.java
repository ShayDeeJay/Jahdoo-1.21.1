package org.jahdoo.common.client.overlay;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
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
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.quests.AbstractQuest;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.Maths;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.valueOf;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;
import static org.jahdoo.common.client.screens.RunScreen.componentTemplate;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.mod.LevelBoonReg.getAllNegative;
import static org.jahdoo.common.registers.mod.LevelBoonReg.getAllPositive;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.getFromName;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;
import static org.jahdoo.trial_nexus.utils.Maths.roundNonWholeString;
import static org.jahdoo.trial_nexus.utils.Maths.ticksToTime;

public class InstanceDataOverlay implements LayeredDraw.Layer {

    private InstanceData instanceData;
    private int timer;
    private float fade;

    private void slideGuiStats() {
        var maxFadeIn = 10.0F;
        var minFadeIn = -240.0F;
        var easeFactor = 0.15F;

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
        var font = mc.font;

        if(player == null || mc.options.hideGui || level == null) return;
        var runData = RunData.getRunData(player);
        var getQuest =  overlayQuest(graphics, mc, runData, font);

        slideGuiStats();
        if(level.getDescription().getString().contains(LevelGenerator.LEVEL_PREFIX)){
            overlayTimer(getQuest.isEmpty(), level, screen);
            overlayInstanceModifiers(graphics, mc, player);
            overlayAlwaysOnInfo(graphics, runData, font);
        }
    }

    private void overlayTimer(boolean questIsEmpty, ClientLevel level, Screen screen) {
        var currentData = level.getData(INSTANCE_DATA);
        if(currentData.getDifficulty().isEmpty() && questIsEmpty) timer = 0;
        if(currentData != instanceData) timer = 400;
        if(screen instanceof InventoryScreen) timer = 30;

        timer = Math.max(0, timer - 1);
        instanceData = currentData;
    }

    private void overlayAlwaysOnInfo(GuiGraphics graphics, RunData runData, Font font) {
        var x = 8;
        var size = 18;
        var spacer = 20;
        var time = HudEntry.getTime(instanceData);
        alwaysOnEntry(graphics, time.icon(), x, 6, size, -1, font, time.value());

        var listOfEntries = List.of(
            HudEntry.roomsCleared(instanceData),
            HudEntry.chestCommon(runData),
            HudEntry.chestRare(runData),
            HudEntry.chestLegendary(runData),
            HudEntry.chestMystic(runData),
            new HudEntry(Icons.SAFE, HudEntry.getComp(runData.getStat(RunData.SAFE))),
            new HudEntry(Icons.CHAMPIONS_CROWN, HudEntry.getComp(runData.getStat(RunData.CHAMPIONS_KILLED))),
            new HudEntry(Icons.TRIAL_EXPERIENCE, HudEntry.getComp(runData.getStat(RunData.EXPERIENCE))),
            new HudEntry(Icons.HORDE, HudEntry.getComp(runData.getStat(RunData.MOBS_KILLED))),
            HudEntry.bronze(runData),
            HudEntry.silver(runData),
            HudEntry.gold(runData),
            HudEntry.platinum(runData)

        );

        for (var listOfEntry : listOfEntries) {
            alwaysOnEntry(graphics, listOfEntry.icon(), x, (int) fade + spacer, size, -1, font, listOfEntry.value());
            spacer += 16;
        }

    }

    public record HudEntry(ResourceLocation icon, Component value) {

        public static HudEntry getTime(InstanceData instanceData){
            var remainingTime = instanceData.getMaxTime() - instanceData.getTicks();
            var colour = remainingTime > 400 ? MAGNET_RANGE_GREEN : NEGATIVE_RED;
            var time = appendStat("", ticksToTime(valueOf(remainingTime)), colour);
            return new HudEntry(Icons.CLOCK, time);
        }

        public static HudEntry roomsCleared(InstanceData instanceData){
            var roomsCleared = instanceData.getClearedRooms();
            var info = withStyleComponent("" + roomsCleared, -1);
            return new HudEntry(Icons.ROOMS_CLEARED, info);
        }

        public static Component getComp(Object info){
            return Helpers.withStyleComponent(String.valueOf(info), -1);
        }

        public static HudEntry chestCommon(RunData runData){
            return new HudEntry(Icons.CHEST_COMMON, getComp(runData.getCommonChests()));
        }

        public static HudEntry chestRare(RunData runData){
            return new HudEntry(Icons.CHEST_RARE, getComp(runData.getRareChests()));
        }

        public static HudEntry chestLegendary(RunData runData){
            return new HudEntry(Icons.CHEST_LEGENDARY, getComp(runData.getLegendaryChests()));
        }

        public static HudEntry chestMystic(RunData runData){
            return new HudEntry(Icons.CHEST_MYTHIC, getComp(runData.getMythicChests()));
        }

        public static HudEntry bronze(RunData runData){
            return new HudEntry(Icons.BRONZE_COIN, getComp(runData.getBronzeCoin()));
        }

        public static HudEntry silver(RunData runData){
            return new HudEntry(Icons.SILVER_COIN, getComp(runData.getSilverCoin()));
        }

        public static HudEntry gold(RunData runData){
            return new HudEntry(Icons.GOLD_COIN, getComp(runData.getGoldCoin()));
        }

        public static HudEntry platinum(RunData runData){
            return new HudEntry(Icons.PLATINUM_COIN, getComp(runData.getPlatinumCoin()));
        }
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
        graphics.drawString(font, info, x+18, y + 6, colour, false);
    }

    private @NotNull Optional<AbstractQuest> overlayQuest(GuiGraphics graphics, Minecraft mc, RunData runData, Font font) {
        var questId = runData.getCurrentQuestId();
        var getQuest = QuestReg.getQuestByName(questId);
        if(getQuest.isPresent()){
            var baseWidth = 60;
            var offsetX = graphics.guiWidth()/2 - baseWidth;
            var offsetY = (int) fade - 20;
            var quest = getQuest.get();
            var current = runData.getStat(questId);
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
        return getQuest;
    }

    private void overlayInstanceModifiers(GuiGraphics graphics, Minecraft mc, LocalPlayer player) {
        var window = mc.getWindow().getWindow();
        if (!InputConstants.isKeyDown(window, InputConstants.KEY_TAB)) {
            timer = 0;
            return;
        };

        var trialData = PlayerTrialData.getData(player);
        var positives = getBoons(true, instanceData, trialData);
        var negatives = getBoons(false, instanceData, trialData);
        var width = graphics.guiWidth();
        var height = graphics.guiHeight();

        final int startY = (int) (fade * 5);
        final var startX = width / 2 + 14;
        final var iconSize = 14;
        final var rowHeight = 14;
        final var negativeOffsetX = -150;

        var difficultyId = Helpers.stringIdToName(instanceData.getDifficulty());
        var difficultyName = difficultyId.isEmpty() ? "Unselected" : difficultyId;
        var difficultyColor = difficultyId.isEmpty() ? ColourStore.OFF_WHITE : getFromName(difficultyName).getColor();

        var header = new RunScreen.StatEntry(componentTemplate("Difficulty", difficultyName, difficultyColor), null);

        graphics.pose().pushPose();
        graphics.pose().scale(2, 2, 2);
        graphics.drawCenteredString(mc.font, header.component(), width / 4 - 8, (int) ( fade), -1);
        graphics.pose().popPose();

        SharedUI.boxMaker(graphics, 0, 0, width, height, 0, SharedUI.fadeBlack(0.5F));
        drawBoonList(graphics, positives, startX, startY, 0, iconSize, rowHeight);
        drawBoonList(graphics, negatives, startX + negativeOffsetX, startY, 0, iconSize, rowHeight);
    }

    private void drawBoonList(
        GuiGraphics graphics,
        List<RunScreen.StatEntry> components,
        int startX,
        int startY,
        int startIndex,
        int size,
        int rowHeight
    ) {
        var yOffset = startIndex;
        var iconX = startX - 4;
        var font = Minecraft.getInstance().font;
        for (var component : components) {
            var hasIcon = component.icon() != null;

            if (hasIcon) {
                graphics.blit(component.icon(), iconX, startY + yOffset - 3, 0, 0, size, size, size, size);
            }
            graphics.drawString(font, component.component(), startX + (hasIcon ? 10 : 0), startY + yOffset, -1, true);
            yOffset += rowHeight;
        }
    }

    public static List<RunScreen.StatEntry> getBoons(
        boolean positive,
        @Nullable InstanceData instanceData,
        PlayerTrialData trialData
    ) {
        var components = new ArrayList<RunScreen.StatEntry>();
        if (trialData == null || instanceData == null) return components;

        var boons = positive ? getAllPositive() : getAllNegative();
        for (var boon : boons) {
            var rawValue = instanceData.get(boon.id());
            String displayValue;

            if (positive) {
                displayValue = boon.id().equals(InstanceData.KEY_MAX_TIME) ? Maths.ticksToTime(String.valueOf(rawValue)) : Maths.roundNonWholeString(rawValue);
            } else {
                var v = roundNonWholeString(Maths.singleFormattedDouble(rawValue));
                displayValue = v + (boon.isPercentageOf() ? "%" : "");
            }

            var color = positive ? boon.getHeaderColour() : MAGNET_STRENGTH_RED;
            var component = componentTemplate(
                Helpers.stringIdToName(boon.id()), displayValue, color
            );
            components.add(new RunScreen.StatEntry(component, boon.getIcon()));
        }

        return components;
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
