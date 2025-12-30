package org.jahdoo.common.client.overlay;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.MutableComponent;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.screens.RunScreen;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.quests.AbstractQuest;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.valueOf;
import static net.minecraft.world.effect.MobEffects.*;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.Icons.SAFE;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;
import static org.jahdoo.common.client.screens.RunScreen.addChestStats;
import static org.jahdoo.common.client.screens.RunScreen.componentTemplate;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.RunData.*;
import static org.jahdoo.trial_nexus.boon.player_boons.BoonSelection.iconFromEffect;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.*;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.ColourStore.BRONZE_COIN;
import static org.jahdoo.trial_nexus.utils.ColourStore.GOLD_COIN;
import static org.jahdoo.trial_nexus.utils.ColourStore.PLATINUM_COIN;
import static org.jahdoo.trial_nexus.utils.ColourStore.SILVER_COIN;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;
import static org.jahdoo.trial_nexus.utils.Maths.*;

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
        var getIData = level.getData(INSTANCE_DATA);
        var getQuest = allInstanceOverlays(graphics, mc, getIData);

        timer = Math.max(0, timer - 1);
        instanceData = getIData;

        if(getIData.getDifficulty().isEmpty() && getQuest.isEmpty()) timer = 0;
        if(currentData != instanceData) timer = 400;
        if(screen instanceof InventoryScreen) timer = 30;
    }

    private @NotNull Optional<AbstractQuest> allInstanceOverlays(GuiGraphics graphics, Minecraft mc, InstanceData getInstanceData) {
        var player = mc.player;
        if(player == null) return Optional.empty();

        var font = mc.font;
        var getRunData = player.getData(AttachmentReg.RUN_DATA.get());
        var questId = getRunData.getCurrentQuestId();
        var getQuest = QuestReg.getQuestByName(questId);
        var remainingTime = getInstanceData.getMaxTime() - getInstanceData.getTicks();
        var roomsCleared = getInstanceData.getClearedRooms();
        var clockX = 8;
        var size = 18;

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
        var settings = mc.options;
        var window = mc.getWindow().getWindow();

        if(InputConstants.isKeyDown(window, InputConstants.KEY_TAB)){
            var runData = RunData.getRunData(player);
            var spacer = 0;
            var spacer1 = 0;
            var spacer2 = 0;
            var getAllComponents = getComponents(instanceData, runData, PlayerTrialData.getData(player));
            var getAllComponents1 = getComponents1(instanceData, runData, PlayerTrialData.getData(player));
            var getAllComponents2 = getComponents2(instanceData, runData, PlayerTrialData.getData(player));
            var startAllY = 30;
            var startAllX = graphics.guiWidth()/2 - 60;
            var size1 = 14;
            var i1 = 150;
            var i12 = -150;

            SharedUI.boxMaker(graphics, startAllX-170, startAllY - 20, 216, 120, 0, SharedUI.fadeBlack(0.5F));
            for (var component : getAllComponents) {
                var hasIcon = component.icon() != null;
                if (hasIcon) {
                    graphics.blit(component.icon(), startAllX - 4, startAllY + spacer - 3, 0, 0, size1, size1, size1, size1);
                }
                graphics.drawString(mc.font, component.component(), startAllX + (hasIcon ? 10 : 0), startAllY + spacer, -1, true);
                spacer += 14;
            }

            for (var component : getAllComponents1) {
                var hasIcon = component.icon() != null;
                if (hasIcon) {
                    graphics.blit(component.icon(), startAllX - 4 + i1, startAllY + spacer1 - 3, 0, 0, size1, size1, size1, size1);
                }
                graphics.drawString(mc.font, component.component(), startAllX + (hasIcon ? 10 : 0) + i1, startAllY + spacer1, -1, true);
                spacer1 += 14;
            }

            for (var component : getAllComponents2) {
                var hasIcon = component.icon() != null;
                if (hasIcon) {
                    graphics.blit(component.icon(), startAllX - 4 + i12, startAllY + spacer2 - 3, 0, 0, size1, size1, size1, size1);
                }
                graphics.drawString(mc.font, component.component(), startAllX + (hasIcon ? 10 : 0) + i12, startAllY + spacer2, -1, true);
                spacer2 += 14;
            }
        }

        return getQuest;
    }

    public static List<RunScreen.StatEntry> getComponents(@Nullable InstanceData instanceData, RunData runData, PlayerTrialData trialData){
        var allComponents = new ArrayList<RunScreen.StatEntry>();
        if(trialData == null || instanceData == null) return allComponents;

        allComponents.add(new RunScreen.StatEntry(componentTemplate("Difficulty", Helpers.stringIdToName(instanceData.getDifficulty()), uiColour()), null));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Total Exp", runData.getStat(EXPERIENCE) + "XP", COSMIC_PURPLE), TRIAL_EXPERIENCE));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Rooms Cleared", runData.getStat(RunData.ROOMS_CLEARED) + "", AETHER_BLUE), Icons.ROOMS_CLEARED));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Mobs Killed", runData.getStat(MOBS_KILLED) + "", MAGNET_STRENGTH_RED), Icons.HORDE));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Champions Killed", runData.getStat(CHAMPIONS_KILLED) + "", CHAMPION_GOLD), CHAMPIONS_CROWN));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Bronze Coins", runData.getStat(RunData.BRONZE_COIN) + "", BRONZE_COIN), Icons.BRONZE_COIN));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Silver Coins", runData.getStat(RunData.SILVER_COIN) + "", SILVER_COIN), Icons.SILVER_COIN));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Gold Coins", runData.getStat(RunData.GOLD_COIN) + "", GOLD_COIN), Icons.GOLD_COIN));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Platinum Coins", runData.getStat(RunData.PLATINUM_COIN) + "", PLATINUM_COIN), Icons.PLATINUM_COIN));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Quest Loot Multiplier", instanceData.getQuestCrateMultiplier() + "", WALLET_BROWN), QUEST_CRATE));

        return allComponents;
    }

    public static List<RunScreen.StatEntry> getComponents1(@Nullable InstanceData instanceData, RunData runData, PlayerTrialData trialData){
        var allComponents = new ArrayList<RunScreen.StatEntry>();
        if(trialData == null || instanceData == null) return allComponents;

        addChestStats(allComponents, "Common Chest", COMMON.getColour(), CHESTS_COMMON, instanceData.getCommonLootMultiplier(), CHEST_COMMON, runData);
        addChestStats(allComponents, "Rare Chest", RARE.getColour(), CHESTS_RARE, instanceData.getRareLootMultiplier(), CHEST_RARE, runData);
        addChestStats(allComponents, "Legendary Chest", LEGENDARY.getColour(), CHESTS_LEGENDARY, instanceData.getLegendaryLootMultiplier(), CHEST_LEGENDARY, runData);
        addChestStats(allComponents, "Eternal Chest", MYTHIC.getColour(), CHESTS_MYTHIC, instanceData.getMythicLootMultiplier(), CHEST_MYTHIC, runData);

        allComponents.add(new RunScreen.StatEntry(componentTemplate("Safe", "", GOLD_COIN, GOLD_COIN), SAFE));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Opened", runData.getStat(RunData.SAFE) + "", GOLD_COIN), BLANK));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Multiplier", instanceData.getSafeMultiplier() + "", GOLD_COIN), BLANK));
        return allComponents;
    }

    public static List<RunScreen.StatEntry> getComponents2(@Nullable InstanceData instanceData, RunData runData, PlayerTrialData trialData){
        var allComponents = new ArrayList<RunScreen.StatEntry>();
        if(trialData == null || instanceData == null) return allComponents;

        // Mob multipliers
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Mob Health","+" + roundNonWholeString(doubleFormattedDouble(instanceData.getHealth())) + "%", uiColour()), iconFromEffect(HEAL)));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Mob Armor","+" + roundNonWholeString(doubleFormattedDouble(instanceData.getArmor())) + "%", uiColour()), iconFromEffect(DAMAGE_RESISTANCE)));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Mob Damage","+" + roundNonWholeString(doubleFormattedDouble(instanceData.getAttackDamage())) + "%", uiColour()), iconFromEffect(DAMAGE_BOOST)));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Mob Speed","+" + roundNonWholeString(doubleFormattedDouble(instanceData.getSpeed())) + "%", uiColour()), iconFromEffect(MOVEMENT_SPEED)));

        // Mob counts / composition
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Horde Mobs", instanceData.getHorde() + "", AETHER_BLUE), Icons.HORDE));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Skeletons", instanceData.getSkeleton() + "", OFF_WHITE), Icons.SKELETON));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Void Spiders", instanceData.getVoidSpider() + "", COSMIC_PURPLE), Icons.VOID_SPIDER));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Inferno Creepers", instanceData.getInfernoCreeper() + "", SYMPATHISER_ORANGE), Icons.INFERNO_CREEPER));
        allComponents.add(new RunScreen.StatEntry(componentTemplate("Eternal Wizards", instanceData.getEternalWizard() + "", MAGNET_STRENGTH_RED), Icons.ETERNAL_WIZARD));

        return allComponents;
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
