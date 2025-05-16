package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.client.button.QuestLogButton;
import org.jahdoo.common.client.button.SimpleButton;
import org.jahdoo.common.client.overlay.InstanceDataOverlay;
import org.jahdoo.common.networking.client2server.QuestTrackerC2SP;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.QuestTracker;
import org.jahdoo.trial_nexus.tasks.AbstractTask;
import org.jahdoo.trial_nexus.tasks.RookieAssassin;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.trial_nexus.utils.ColourStore.SUB_HEADER_COLOUR;

public class QuestLog extends AbstractPanableScreen {

    public static final int WIDTH_OFFSET = 80;
    private AbstractTask task;
    private double mouseX;
    private double mouseY;

    @Override
    protected void init() {
        super.init();
        var trialData = List.of(new RookieAssassin());
        var spacer = 0;

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics graphics, int i, int i1, float v) {
                    var start = canScrollSelections(mouseX, mouseY, width) ? fadeBlack(0.8F): uiFade();
                    boxMaker(graphics, 14, 63, WIDTH_OFFSET, height/2 - 38, canScrollSelections(mouseX, mouseY, width) ? color(180, uiColour()) : 0, start, start);

                    if(task != null){
                        var spacer = 0;
                        graphics.drawString(font, Helpers.withStyleComponentTrans("Rewards", uiColour()), 200, 190, -1);
                        for (var reward : task.rewards()) {
                            graphics.renderItem(reward, 196 + spacer, 200);
                            spacer += 20;
                        }
                    }
                    graphics.enableScissor(3, 69, width - 3, height - 20);
                }
            }
        );

        for (var pastRun : trialData) {
            this.addRenderableWidget(new QuestLogButton(28, spacer + 78, 130, 36, false, (button) -> doOnClick(pastRun), pastRun));
            spacer += 47;
        }

        var player = getMinecraft().player;
        if(player != null && task != null){
            var isSelected = task.completionPredicate(player) && !QuestTracker.claimedQuest(player, task.taskId());
            this.addRenderableWidget(new SimpleButton(192, height - 45, width - 214, 22, isSelected, (button) -> claimReward(task.taskId()), "Claim Reward"));
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
        super.onClose();
    }

    private @NotNull PlayerTrialData getPlayerTrialData() {
        return PlayerTrialData.getData(getMinecraft().player);
    }

    private void claimReward(String id){
        PacketDistributor.sendToServer(new QuestTrackerC2SP(id));
    }

    private void doOnClick(AbstractTask task){
        this.task = task;
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

        return true;
    }

    private static boolean canScrollSelections(double mouseX, double mouseY, int width) {
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
        super.render(graphics, mouseX, mouseY, partialTick);
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        var startY = 62;
        var pose = graphics.pose();
        var scale = 2F;
        var y = startY - 24;

        var widthOffset = width / 2 - 99;
        var startX1 = WIDTH_OFFSET * 2 + 24;

        boxMaker(graphics, startX1, startY+1, widthOffset, this.height/2 - 38, uiColour(), uiFade(), uiFade());
        graphics.enableScissor(0, startY+8, this.width, this.height - 18);

        if(task != null){
            var size = 24;
            var mc = getMinecraft();
            var font = mc.font;

            pose.pushPose();
            pose.scale(scale, scale, scale);
            graphics.blit(task.taskIcon(), 94, y - 4, 0, 0, size, size, size, size);
            graphics.drawString(font, Helpers.withStyleComponentTrans(task.taskName(), uiColour()), 120, y + 6, -1);
            pose.popPose();

            var player = mc.player;
            var isComplete = task.completionPredicate(player);
            var currentValue = task.trackedValue(player);
            var requiredValue = task.countRequired();
            var tracker = Math.min(currentValue, requiredValue) + "/" + requiredValue;
            var complete = "Complete";
            var colour = isComplete ? uiColour() : SUB_HEADER_COLOUR;

            InstanceDataOverlay.progressBar(graphics, 200, 160, 50, 8, currentValue, requiredValue, 3, uiColour(), colour);
            graphics.drawString(font, Helpers.withStyleComponentTrans(task.taskDescription(), uiColour()), 200, 120, -1);
            graphics.drawString(font, Helpers.withStyleComponentTrans("Progress", SUB_HEADER_COLOUR), 200, 140, -1);
            graphics.drawString(font, Helpers.withStyleComponentTrans(isComplete ? complete : tracker, colour), 200, 150, -1);
        }

        graphics.disableScissor();
        this.rebuildWidgets();
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {}

}
