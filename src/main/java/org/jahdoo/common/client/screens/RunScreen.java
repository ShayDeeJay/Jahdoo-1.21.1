package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.button.FlexiButton;
import org.jahdoo.common.registers.mod.LevelBoonReg;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jahdoo.common.registers.mod.StatEntryReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.level_modifiers.AbstractLevelBoon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.fadeBlack;


public class RunScreen extends AbstractPanableScreen {

    public static final int SPACER = 14;
    public static final int WIDTH_OFFSET = 80;
    private RunData runData;
    private InstanceData instanceData;
    private double panYMain;
    private double mouseX;
    private double mouseY;
    private int levelBoonEntries;

    @Override
    protected void init() {
        super.init();
        var reversed = getPlayerTrialData().getPastRuns().reversed();
        var instanceData1 = getPlayerTrialData().getInstanceData().reversed();
        var spacer = 0;
        var moveX = -5;
        var i2 = 15;
        var mc = getMinecraft();
        var player = mc.player;
        if(player == null) return;

        if (runData == null || !PlayerTrialData.getData(player).getPastRuns().contains(runData)) {
            PlayerTrialData.getLastRun(player).ifPresent(x -> this.runData = x);
            PlayerTrialData.getLastInstance(player).ifPresent(x -> this.instanceData = x);
        }

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics graphics, int i, int i1, float v) {
                    var start = canScrollSelections(mouseX, mouseY, height) ? fadeBlack(0.8F): uiFade();
                    if(instanceData != null && runData != null) {
                        boxMaker(graphics, i2 + moveX, 63, WIDTH_OFFSET, height / 2 - 38, canScrollSelections(mouseX, mouseY, height) ? color(180, uiColour()) : 0, start, start);
                    }
                    graphics.enableScissor(3, 69, width - 3, height - 20);
                }
            }
        );

        for (var pastRun : reversed) {
            var x = this.addRenderableWidget(new FlexiButton(i2 + 15 + moveX, (int) (this.panY + spacer  + 78), 130, 36, runData == pastRun, pastRun, (s) -> doOnClick(pastRun, instanceData1.get(reversed.indexOf(pastRun)))));
            x.visible = x.getY() < this.height && x.getY() > 0;
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
        this.rebuildWidgets();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        if (canScrollSelections(mouseX, mouseY, height)) {
            this.rebuildWidgets();
            var zoomScale = this.zoomX + 1;
            panY += Math.round(dragY / zoomScale);
            panY = Math.min(panY, 0);

            int entryCount = getPlayerTrialData().getPastRuns().size();
            int totalHeight = entryCount * 47;

            int visibleHeight = (height - 13) - 64;
            int minPanY = Math.min(0, visibleHeight - totalHeight - 18);

            panY = Math.max(panY, minPanY);
            return true;
        }

        if (canScrollDetails(mouseX, mouseY, height, width)) {
            this.rebuildWidgets();
            panYMain += dragY;
            panYMain = Math.min(panYMain, 0);

            var componentHeight = this.levelBoonEntries * SPACER;
            var visibleHeight = (height - 13) - (62 + 36);
            var minPanYMain = Math.min(0, visibleHeight - componentHeight - 10);

            panYMain = Math.max(panYMain, minPanYMain);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var scrollSpeed = scrollY * SPACER;

        if (canScrollSelections(mouseX, mouseY, height)) {
            this.rebuildWidgets();
            var zoomScale = this.zoomX + 1;
            var scrollAmount = Math.round(scrollSpeed / zoomScale);
            panY = Math.min(panY + scrollAmount, 0);

            int entryCount = getPlayerTrialData().getPastRuns().size();
            int totalHeight = entryCount * 47;
            int visibleHeight = (height - 13) - 64;
            int minPanY = Math.min(0, visibleHeight - totalHeight - 18);

            panY = Math.max(panY, minPanY);
            return true;
        }

        if (canScrollDetails(mouseX, mouseY, height, width)) {
            this.rebuildWidgets();
            panYMain += scrollSpeed;
            panYMain = Math.min(panYMain, 0);

            var componentHeight = this.levelBoonEntries * SPACER;
            var visibleHeight = (height - 13) - (62 + 36);
            var minPanYMain = Math.min(0, visibleHeight - componentHeight - 10);

            panYMain = Math.max(panYMain, minPanYMain);
            return true;
        }

        return false;
    }

    private static boolean canScrollSelections(double mouseX, double mouseY, int height) {
        var v = 5;
        var boundMouseX = mouseX > v * 2 && mouseX < v + WIDTH_OFFSET * 2 + v;
        return boundMouseX && heightRestriction(mouseY, height);
    }

    private static boolean canScrollDetails(double mouseX, double mouseY, int height, int width) {
        var v = (double) width / 2;
        var canScrollX = mouseX > 175 && mouseX < (v * 2) - 12;
        return canScrollX && heightRestriction(mouseY, height);
    }

    public static boolean heightRestriction(double mouseY, int height){
        var v = 5;
        return mouseY > v * 10 + 12 && mouseY < v + height - (v*4) + 1;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        var scale = 2F;
        var startX = 176;
        var startY = 62;
        var startAllX = startX + 10;
        var startAllY = (int) (startY + 36 + this.panYMain);
        var y = (int) Math.round((startY - 24 + (this.panYMain / 2)));
        var pose = graphics.pose();

        if(instanceData != null && runData != null) {
            var spacer = 0;
            var start = canScrollDetails(mouseX, mouseY, height, width) ? fadeBlack(0.8F) : uiFade();

            this.mouseX = mouseX;
            this.mouseY = mouseY;


            var colourBorder = canScrollDetails(mouseX, mouseY, height, width) ? color(180, uiColour()) : 0;
            boxMaker(graphics, startX, startY + 1, this.width / 2 - 94, this.height / 2 - 38, colourBorder, start, start);
            graphics.enableScissor(startX, startY + 8, this.width / 2 + 200, this.height - 18);

            pose.pushPose();
            pose.scale(scale, scale, scale);
            graphics.drawCenteredString(getMinecraft().font, TextHelpers.withStyleComponentTrans("Run Data", uiColour()), startX / 2 + 27, y, -1);
            pose.popPose();

            if (runData != null) {
                var allComponents = getComponents(instanceData, runData, getPlayerTrialData());
                var coins = StatEntryReg.getCoins();
                var ores = StatEntryReg.getOres();
                var general = StatEntryReg.getGeneral();
                var mob = StatEntryReg.getMob();
                var loot = StatEntryReg.getLoot();

                for (var component : allComponents) {
                    spacer = drawEntryWithOptionalIcon(
                        graphics,
                        component.icon,
                        component.component(),
                        startAllX,
                        startAllY,
                        spacer
                    );
                }

                spacer = runInfo(graphics, spacer, startAllX, startAllY, general, mob, loot, coins, ores);
                spacer = instanceInfo(graphics, spacer, startAllX, startAllY, instanceData);

                this.levelBoonEntries = spacer / SPACER;
            }

            graphics.disableScissor();
        } else {
            pose.pushPose();
            pose.scale(scale, scale, scale);
            graphics.drawCenteredString(getMinecraft().font, TextHelpers.withStyleComponentTrans("No Run Data", uiColour()), width/4, height/4, -1);
            pose.popPose();
        }
    }

    public static int instanceInfo(GuiGraphics graphics, int spacer, int startAllX, int startAllY, InstanceData instanceData) {
        var positiveBoons = LevelBoonReg.getAllPositive();
        var negativeBoons = LevelBoonReg.getAllNegative();

        spacer = drawHeader(graphics, "Instance Modifiers", ColourHelpers.getHeaderColour(), startAllX, startAllY, spacer, true, true);
        spacer = drawHeader(graphics, "Positive", ColourHelpers.getMagnetRangeGreen(), startAllX, startAllY, spacer, true, false);
        spacer = drawLevelBoonList(graphics, positiveBoons, instanceData, startAllX, startAllY, spacer);

        var i = positiveBoons.size() * SPACER + SPACER;
        var i1 = 170;

        drawHeader(graphics, "Negative", ColourHelpers.getMagnetStrengthRed(), startAllX + i1, startAllY, spacer - i, true, false);
        drawLevelBoonList(graphics, negativeBoons, instanceData, startAllX + i1, startAllY, spacer - i + SPACER);
        spacer = addSpacerLines(spacer, 1);

        return spacer;
    }

    private int runInfo(GuiGraphics graphics, int spacer, int startAllX, int startAllY, List<AbstractStatEntry> general, List<AbstractStatEntry> mob, List<AbstractStatEntry> loot, List<AbstractStatEntry> coins, List<AbstractStatEntry> ores) {
//        spacer = addSpacerLines(spacer, 1);
        spacer = drawHeader(graphics, "Run Stats", ColourHelpers.getHeaderColour(), startAllX, startAllY, spacer, true, true);

        spacer = drawHeader(graphics, "General", ColourHelpers.getOffWhite(), startAllX, startAllY, spacer, true, false);
        spacer = drawPlayerBoonList(graphics, general, runData, startAllX, startAllY, spacer);

        var x = startAllX + 170;
        var i = general.size() * SPACER + SPACER;

        drawHeader(graphics, "Killed", ColourHelpers.getMagnetStrengthRed(), x, startAllY,  spacer - i, true, false);
        spacer = drawPlayerBoonList(graphics, mob, runData, x, startAllY,  spacer - i + SPACER);
        spacer = addSpacerLines(spacer, 1);

        spacer = drawHeader(graphics, "Looted", ColourHelpers.getAetherBlue(), startAllX, startAllY, spacer, true, false);
        spacer = drawPlayerBoonList(graphics, loot, runData, startAllX, startAllY, spacer);
        spacer = addSpacerLines(spacer, 1);

        var xs = coins.size() * SPACER + 56;
        drawHeader(graphics, "Collected", ColourHelpers.getGoldCoin(), x, startAllY,  spacer - xs, true, false);
        drawPlayerBoonList(graphics, coins, runData, x, startAllY,  spacer - xs + SPACER);

        spacer = drawHeader(graphics, "Mined", ColourHelpers.getHeaderColour(), startAllX, startAllY, spacer, true, false);
        spacer = drawPlayerBoonList(graphics, ores, runData, startAllX, startAllY, spacer);
        spacer = addSpacerLines(spacer, 1);

        return spacer;
    }

    public static int drawPlayerBoonList(
        GuiGraphics graphics,
        Collection<AbstractStatEntry> boons,
        RunData runData,
        int startX,
        int startY,
        int spacer
    ) {

        for (var boon : boons) {

            var icon = boon.icon();
            boolean hasIcon = icon != null;

            if (hasIcon) {
                int size = SPACER;
                graphics.blit(icon, startX - 4, startY + spacer - 3,
                    0, 0, size, size, size, size);
            }

            var label = TextHelpers.withStyleComponent(TextHelpers.stringIdToName(boon.getLabel()) + ": ", ColourHelpers.getHeaderColour());
            var labelX = TextHelpers.withStyleComponent(runData.getStat(boon.id()) + "", boon.colour());

            graphics.drawString(
                ClientHelpers.getMinecraft().font, label.copy().append(labelX),
                startX + (hasIcon ? 10 : 0),
                startY + spacer,
                -1, true
            );

            spacer += SPACER;
        }

        return spacer;
    }

    public static int drawLevelBoonList(
        GuiGraphics graphics,
        Collection<AbstractLevelBoon> boons,
        InstanceData instanceData,
        int startX,
        int startY,
        int spacer
    ) {
        for (var boon : boons) {

            var icon = boon.getIcon();
            boolean hasIcon = icon != null;

            if (hasIcon) {
                int size = SPACER;
                graphics.blit(icon, startX - 4, startY + spacer - 3,
                    0, 0, size, size, size, size);
            }

            var value = instanceData.get(boon.id());
            var label = boon.boonLabel(value, boon.id());

            graphics.drawString(
                ClientHelpers.getMinecraft().font,
                label,
                startX + (hasIcon ? 10 : 0),
                startY + spacer,
                -1,
                true
            );

            spacer += SPACER;
        }

        return spacer;
    }

    private int drawEntryWithOptionalIcon(
        GuiGraphics graphics,
        ResourceLocation icon,
        Component text,
        int startX,
        int startY,
        int spacer
    ) {

        boolean hasIcon = icon != null;

        if (hasIcon) {
            int size = SPACER;
            graphics.blit(icon, startX - 4, startY + spacer - 3,
                0, 0, size, size, size, size);
        }

        graphics.drawString(
            getMinecraft().font,
            text,
            startX + (hasIcon ? 10 : 0),
            startY + spacer,
            -1,
            true
        );

        return spacer + SPACER;
    }

    public static int drawHeader(
        GuiGraphics graphics,
        String text,
        int colour,
        int startX,
        int startY,
        int spacer,
        boolean bold,
        boolean underline
    ) {

        var styled = TextHelpers.withStyleComponent(text, colour, bold, underline);

        graphics.drawString(
            ClientHelpers.getMinecraft().font,
            styled,
            startX,
            startY + spacer,
            -1,
            true
        );

        return spacer + SPACER;
    }

    public static int addSpacerLines(int spacer, int lines) {
        return spacer + (SPACER * lines);
    }

    public static List<StatEntry> getComponents(@Nullable InstanceData instanceData, RunData runData, PlayerTrialData trialData){
        var allComponents = new ArrayList<StatEntry>();
        var spacer = new StatEntry(Component.empty().copy(), null);
        if(trialData == null || instanceData == null) return allComponents;

        var statColour = ColourHelpers.getOffWhite();
        allComponents.add(new StatEntry(componentTemplate("Trial No", trialData.getPastRuns().indexOf(runData) + 1 + "", statColour), null));
        allComponents.add(new StatEntry(componentTemplate("Date", runData.getDateAndTime().split(" ")[0], statColour), null));
        allComponents.add(new StatEntry(componentTemplate("Time", runData.getDateAndTime().split(" ")[1], statColour), null));
        allComponents.add(new StatEntry(componentTemplate("Difficulty", TextHelpers.stringIdToName(instanceData.getDifficulty()), statColour), null));
        allComponents.add(new StatEntry(componentTemplate("Player Level", String.valueOf(runData.getPlayerLevel()), statColour), null));

        var died = runData.died();
        allComponents.add(new StatEntry(componentTemplate("Fate", died ? "Died!" : "Survived!", died ? ColourHelpers.getRating2Red() : ColourHelpers.getRating5Green()), null));
        allComponents.add(spacer);

        var stat = runData.getCurrentQuestId();
        if(!stat.isEmpty()){
            var completed = runData.isCompletedQuest();
            var statusColour = completed ? ColourHelpers.getMagnetRangeGreen() : ColourHelpers.getMagnetStrengthRed();
            var questByName = QuestReg.getQuestByName(stat);
            questByName.ifPresent(abstractQuest -> allComponents.add(new StatEntry(componentTemplate("Quest Type", abstractQuest.getDisplayName(), ColourHelpers.getRating5Green()), null)));
            allComponents.add(new StatEntry(componentTemplate("Quest Status", (completed ? "Completed" : "Failed"), statusColour), null));
        }

        return allComponents;
    }

    public static MutableComponent componentTemplate(String header, String stat, int statColour){
        return componentTemplate(header, stat, statColour, uiColour());
    }

    public static MutableComponent componentTemplate(String header, String stat, int statColour, int headerColour){
        var preMob = TextHelpers.withStyleComponent(header + ": ", headerColour);
        var valueMob = TextHelpers.withStyleComponent(stat, statColour);
        return preMob.copy().append(valueMob);
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {}

    public record StatEntry(
        MutableComponent component,
        @Nullable ResourceLocation icon
    ){}

}
