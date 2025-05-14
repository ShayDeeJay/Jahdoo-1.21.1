package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.button.QuestLogButton;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.tasks.AbstractTask;
import org.jahdoo.trial_nexus.tasks.BabyAssassin;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.effect.MobEffects.*;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.Icons.SAFE;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.trial_nexus.attachments.RunData.*;
import static org.jahdoo.trial_nexus.boon.player_boons.BoonSelection.iconFromEffect;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.*;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.ColourStore.BRONZE_COIN;
import static org.jahdoo.trial_nexus.utils.ColourStore.GOLD_COIN;
import static org.jahdoo.trial_nexus.utils.ColourStore.PLATINUM_COIN;
import static org.jahdoo.trial_nexus.utils.ColourStore.SILVER_COIN;
import static org.jahdoo.trial_nexus.utils.Maths.*;

public class QuestLog extends AbstractPanableScreen {

    public static final int WIDTH_OFFSET = 80;
    private AbstractTask task;
    private RunData runData;
    private InstanceData instanceData;
    private double panYMain;
    private double mouseX;
    private double mouseY;

    @Override
    protected void init() {
        super.init();
        var trialData = List.of(new BabyAssassin());
        var spacer = 0;

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics graphics, int i, int i1, float v) {
                    var start = canScrollSelections(mouseX, mouseY, width) ? fadeBlack(0.8F): uiFade();
                    boxMaker(graphics, 14, 63, WIDTH_OFFSET, height/2 - 38, canScrollSelections(mouseX, mouseY, width) ? color(180, uiColour()) : 0, start, start);
                    graphics.enableScissor(3, 69, width - 3, height - 20);
                }
            }
        );

        for (var pastRun : trialData) {
            this.addRenderableWidget(new QuestLogButton(28, (int) (this.panY + spacer  + 78), 130, 36, false, (button) -> { this.task = pastRun; }, pastRun));
            spacer += 47;
        }

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.disableScissor();
                }
            }
        );

    }

    @Override
    public void onClose() {
        this.runData = null;
        super.onClose();
    }

    private @NotNull PlayerTrialData getPlayerTrialData() {
        return PlayerTrialData.getData(getMinecraft().player);
    }

    private void doOnClick(RunData runData, InstanceData instanceData){
        this.runData = runData;
        this.instanceData = instanceData;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (canScrollSelections(mouseX, mouseY, width)) {
            var zoomScale = this.zoomX + 1;
            panY += Math.round(dragY / zoomScale);
            panY = Math.min(panY, 0); // Clamp to top

            int entryCount = getPlayerTrialData().getPastRuns().size();
            int totalHeight = entryCount * 47;
            int visibleHeight = (height - 13) - 64; // Bottom - Top
            int minPanY = Math.min(0, visibleHeight - totalHeight - 18); // Extra 10 for bottom padding

            panY = Math.max(panY, minPanY); // Clamp to bottom
        }

        if (canScrollDetails(mouseX, mouseY, width)) {
            panYMain += dragY;
            panYMain = Math.min(panYMain, 0); // Top bound

            var componentHeight = getComponents(instanceData, runData, getPlayerTrialData()).size() * 14;
            var visibleHeight = (height - 13) - (62 + 36); // Matches your layout
            var minPanYMain = Math.min(0, visibleHeight - componentHeight - 10); // Add bottom padding

            panYMain = Math.max(panYMain, minPanYMain); // Bottom bound
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var scrollSpeed = scrollY * 14;
        if (canScrollSelections(mouseX, mouseY, width)) {
            var zoomScale = this.zoomX + 1;
            var scrollAmount = Math.round(scrollSpeed / zoomScale);
            panY = Math.min(panY + scrollAmount, 0); // Upper limit still 1 (top)

            int entryCount = getPlayerTrialData().getPastRuns().size();
            int totalHeight = entryCount * 47;
            int visibleHeight = (height - 13) - 64; // Bottom - Top of scroll box
            int minPanY = Math.min(0, visibleHeight - totalHeight - 18); // Extra 10 for bottom padding

            panY = Math.max(panY, minPanY); // Clamp to prevent overscroll
        }

        if (canScrollDetails(mouseX, mouseY, width)) {
            panYMain += scrollSpeed;
            panYMain = Math.min(panYMain, 0); // Top bound

            var componentHeight = getComponents(instanceData, runData, getPlayerTrialData()).size() * 14;
            var visibleHeight = (height - 13) - (62 + 36); // Matches your layout
            var minPanYMain = Math.min(0, visibleHeight - componentHeight - 10);

            panYMain = Math.max(panYMain, minPanYMain); // Bottom bound
        }

        return true;
    }

    private static boolean canScrollSelections(double mouseX, double mouseY, int width) {
        var v = (double) width / 2-5;
        return mouseX > 13 && mouseX < WIDTH_OFFSET * 2 + 14;
    }

    private static boolean canScrollDetails(double mouseX, double mouseY, int width) {
        var v = (double) width / 2;
        var canScrollX = mouseX > v + 4 && mouseX < v + WIDTH_OFFSET * 2;
        var canScrollY = mouseY > v + 4 && mouseY < v + WIDTH_OFFSET * 2;
        return canScrollX;
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
        super.renderBlurredBackground(partialTick);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var startX = this.width/2 + 5;
        var startY = 62;
        var startAllX = startX + 10;
        var startAllY = (int) (startY + 36 + this.panYMain);
        var pose = graphics.pose();
        var scale = 2F;
        var y = (int) Math.round((startY - 24 + (this.panYMain / 2)));
        var spacer = 0;
        var start = canScrollDetails(mouseX, mouseY, width) ? fadeBlack(0.8F): uiFade();

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        super.render(graphics, mouseX, mouseY, partialTick);
        var colourBorder = canScrollDetails(mouseX, mouseY, width) ? color(180, uiColour()) : 0;

        var widthOffset = width / 2 - 99;
        var startX1 = WIDTH_OFFSET * 2 + 24;
        boxMaker(graphics, startX1, startY+1, widthOffset, this.height/2 - 38, colourBorder, start, start);
        graphics.enableScissor(0, startY+8, this.width, this.height - 18);

        if(task != null){
            pose.pushPose();
            pose.scale(scale, scale, scale);
            var size = 24;
            graphics.blit(task.taskIcon(), 94, y - 4, 0, 0, size, size, size, size);
            graphics.drawString(getMinecraft().font, Helpers.withStyleComponentTrans(task.taskName(), uiColour()), 120, y + 6, -1);
            pose.popPose();

            graphics.drawString(getMinecraft().font, Helpers.withStyleComponentTrans(task.taskDescription(), uiColour()), 200, 120, -1);

            graphics.drawString(getMinecraft().font, Helpers.withStyleComponentTrans("Progress", SUB_HEADER_COLOUR), 200, 140, -1);
            graphics.drawString(getMinecraft().font, Helpers.withStyleComponentTrans(task.trackedValue(getMinecraft().player) +"/"+ task.countRequired(), SUB_HEADER_COLOUR), 200, 150, -1);
        }
        graphics.disableScissor();
        this.rebuildWidgets();
    }

    public static List<StatEntry> getComponents(@Nullable InstanceData instanceData, RunData runData, PlayerTrialData trialData){
        var allComponents = new ArrayList<StatEntry>();
        var spacer = new StatEntry(Component.empty().copy(), null);
        if(trialData == null || instanceData == null) return allComponents;

        allComponents.add(new StatEntry(componentTemplate("Trial No", trialData.getPastRuns().indexOf(runData) + 1 + "", uiColour()), null));
        allComponents.add(new StatEntry(componentTemplate("Date", runData.getDateAndTime().split(" ")[0], uiColour()), null));
        allComponents.add(new StatEntry(componentTemplate("Time", runData.getDateAndTime().split(" ")[1], uiColour()), null));
        allComponents.add(new StatEntry(componentTemplate("Difficulty", Helpers.stringIdToName(instanceData.getDifficulty()), uiColour()), null));
        allComponents.add(new StatEntry(componentTemplate("Player Level", String.valueOf(runData.getStat(PLAYER_LEVEL)), uiColour()), null));

        var died = runData.died();
        allComponents.add(new StatEntry(componentTemplate("Fate", died ? "Died!" : "Survived!", died ? RATING_2_RED : RATING_5_GREEN), null));
        allComponents.add(spacer);

        var stat = runData.getCurrentQuestId();
        if(!stat.isEmpty()){
            var completed = runData.isCompletedQuest();
            var statusColour = completed ? MAGNET_RANGE_GREEN : MAGNET_STRENGTH_RED;
            allComponents.add(new StatEntry(componentTemplate("Quest Type", QuestReg.getQuestByName(stat).get().getDisplayName(), RATING_5_GREEN), null));
            allComponents.add(new StatEntry(componentTemplate("Quest Status", (completed ? "Completed" : "Failed"), statusColour), null));
        }

        allComponents.add(new StatEntry(componentTemplate("Run Time", ticksToTime(runData.getStat(TIME_IN_TRIAL) + ""), PERK_GREEN), CLOCK));
        allComponents.add(new StatEntry(componentTemplate("Total Exp", runData.getStat(EXPERIENCE) + "XP", COSMIC_PURPLE), TRIAL_EXPERIENCE));
        allComponents.add(new StatEntry(componentTemplate("Rooms Cleared", runData.getStat(RunData.ROOMS_CLEARED) + "", AETHER_BLUE), Icons.ROOMS_CLEARED));
        allComponents.add(new StatEntry(componentTemplate("Mobs Killed", runData.getStat(MOBS_KILLED) + "", MAGNET_STRENGTH_RED), Icons.HORDE));
        allComponents.add(new StatEntry(componentTemplate("Champions Killed", runData.getStat(CHAMPIONS_KILLED) + "", CHAMPION_GOLD), CHAMPIONS_CROWN));
        allComponents.add(new StatEntry(componentTemplate("Bronze Coins", runData.getStat(RunData.BRONZE_COIN) + "", BRONZE_COIN), Icons.BRONZE_COIN));
        allComponents.add(new StatEntry(componentTemplate("Silver Coins", runData.getStat(RunData.SILVER_COIN) + "", SILVER_COIN), Icons.SILVER_COIN));
        allComponents.add(new StatEntry(componentTemplate("Gold Coins", runData.getStat(RunData.GOLD_COIN) + "", GOLD_COIN), Icons.GOLD_COIN));
        allComponents.add(new StatEntry(componentTemplate("Platinum Coins", runData.getStat(RunData.PLATINUM_COIN) + "", PLATINUM_COIN), Icons.PLATINUM_COIN));
        allComponents.add(new StatEntry(componentTemplate("Quest Loot Multiplier", instanceData.getQuestCrateMultiplier() + "", WALLET_BROWN), QUEST_CRATE));

        addChestStats(allComponents, "Common Chest", COMMON.getColour(), CHESTS_COMMON, instanceData.getCommonLootMultiplier(), CHEST_COMMON, runData);
        addChestStats(allComponents, "Rare Chest", RARE.getColour(), CHESTS_RARE, instanceData.getRareLootMultiplier(), CHEST_RARE, runData);
        addChestStats(allComponents, "Legendary Chest", LEGENDARY.getColour(), CHESTS_LEGENDARY, instanceData.getLegendaryLootMultiplier(), CHEST_LEGENDARY, runData);
        addChestStats(allComponents, "Eternal Chest", ETERNAL.getColour(), CHESTS_ETERNAL, instanceData.getEternalLootMultiplier(), CHEST_ETERNAL, runData);

        allComponents.add(new StatEntry(componentTemplate("Safe", "", GOLD_COIN, GOLD_COIN), SAFE));
        allComponents.add(new StatEntry(componentTemplate("Opened", runData.getStat(RunData.SAFE) + "", GOLD_COIN), BLANK));
        allComponents.add(new StatEntry(componentTemplate("Multiplier", instanceData.getSafeMultiplier() + "", GOLD_COIN), BLANK));

        allComponents.add(spacer);

        // Mob multipliers
        allComponents.add(new StatEntry(componentTemplate("Mob Health", "+" + roundNonWholeString(doubleFormattedDouble(instanceData.getHealth())) + "%", uiColour()), iconFromEffect(HEAL)));
        allComponents.add(new StatEntry(componentTemplate("Mob Armor", "+" + roundNonWholeString(doubleFormattedDouble(instanceData.getArmor())) + "%", uiColour()), iconFromEffect(DAMAGE_RESISTANCE)));
        allComponents.add(new StatEntry(componentTemplate("Mob Damage", "+" + roundNonWholeString(doubleFormattedDouble(instanceData.getAttackDamage())) + "%", uiColour()), iconFromEffect(DAMAGE_BOOST)));
        allComponents.add(new StatEntry(componentTemplate("Mob Speed", "+" + roundNonWholeString(doubleFormattedDouble(instanceData.getSpeed())) + "%", uiColour()), iconFromEffect(MOVEMENT_SPEED)));

        // Mob counts / composition
        allComponents.add(new StatEntry(componentTemplate("Horde Mobs", instanceData.getHorde() + "", AETHER_BLUE), Icons.HORDE));
        allComponents.add(new StatEntry(componentTemplate("Skeletons", instanceData.getSkeleton() + "", OFF_WHITE), Icons.SKELETON));
        allComponents.add(new StatEntry(componentTemplate("Void Spiders", instanceData.getVoidSpider() + "", COSMIC_PURPLE), Icons.VOID_SPIDER));
        allComponents.add(new StatEntry(componentTemplate("Inferno Creepers", instanceData.getInfernoCreeper() + "", SYMPATHISER_ORANGE), Icons.INFERNO_CREEPER));
        allComponents.add(new StatEntry(componentTemplate("Eternal Wizards", instanceData.getEternalWizard() + "", MAGNET_STRENGTH_RED), Icons.ETERNAL_WIZARD));

        return allComponents;
    }

    private static void addChestStats(List<StatEntry> allComponents, String name, int colour, String chestStat, int multiplier, ResourceLocation buttonType, RunData runData) {
        allComponents.add(new StatEntry(componentTemplate(name, "", colour, colour), buttonType));
        allComponents.add(new StatEntry(componentTemplate("Opened", runData.getStat(chestStat) + "", colour), BLANK));
        allComponents.add(new StatEntry(componentTemplate("Multiplier", multiplier + "", colour), BLANK));
    }

    public static MutableComponent componentTemplate(String header, String stat, int statColour){
        return componentTemplate(header, stat, statColour, SUB_HEADER_COLOUR);
    }

    public static MutableComponent componentTemplate(String header, String stat, int statColour, int headerColour){
        var preMob = Helpers.withStyleComponent(header + ": ", headerColour);
        var valueMob = Helpers.withStyleComponent(stat, statColour);
        return preMob.copy().append(valueMob);
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {}

    public record StatEntry(
        MutableComponent component,
        @Nullable ResourceLocation icon
    ){}

}
