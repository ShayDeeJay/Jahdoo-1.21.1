package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.client.button.QuestLogButton;
import org.jahdoo.common.client.button.SimpleButton;
import org.jahdoo.common.networking.client2server.GivePlayerItemsC2SP;
import org.jahdoo.common.networking.client2server.QuestTrackerC2SP;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.TaskReg;
import org.jahdoo.trial_nexus.attachments.QuestTracker;
import org.jahdoo.trial_nexus.tasks.AbstractTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.overlay.InstanceDataOverlay.progressBar;
import static org.jahdoo.trial_nexus.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.trial_nexus.utils.Helpers.colourByPercent;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponentTrans;

public class CraftingHelperScreen extends AbstractPanableScreen {
    public static int frameTicks;
    public static final int WIDTH_OFFSET = 80;
    private AbstractTask task;
    private double mouseX;
    private double mouseY;
    private final List<AbstractTask> getAllTasks = TaskReg.getAllTasks();

    @Override
    protected void init() {
        super.init();
        var spacer = 0;
        var player = getMinecraft().player;

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics graphics, int i, int i1, float v) {
                    var start = fadeBlack(0.8F);
                    boxMaker(graphics, 14, 63, WIDTH_OFFSET, height/2 - 38,  uiColour(), start, start);
                    graphics.enableScissor(3, 69, width - 3, height - 20);
                }
            }
        );

        for (var quests : getAllTasks) {
            var questComplete = QuestTracker.claimedQuest(player, quests.taskId());
            var pY = (int) (panY + spacer + 78);
            var selected = quests.equals(this.task);
            this.addRenderableWidget(new QuestLogButton(28, pY, 130, 36, questComplete, selected, (button) -> doOnClick(quests), quests));
            spacer += 47;
        }

        if(player != null && task != null){
            var claimedQuest = QuestTracker.claimedQuest(player, task.taskId());
            var isSelected = task.completionPredicate(player) && !claimedQuest;
            this.addRenderableWidget(new SimpleButton(192, height - 45, width - 214, 22, isSelected, (button) -> claimReward(task.taskId(), task.rewards((int) getMinecraft().level.getGameTime())), claimedQuest ? "Reward Claimed" : "Claim Reward"));
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

    private void renderRewards(@NotNull GuiGraphics graphics) {
        if(task != null){
            var spacer = 0;
            var rewards = task.rewards(frameTicks);
            var startX = 192;
            var startY = -52;

            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 100);
            boxMaker(graphics, startX, startY + 185, rewards.size() * 16, 19,  uiColour(),  fadeBlack(0.8F),  fadeBlack(0.8F));
            graphics.drawString(font, withStyleComponentTrans("Rewards", uiColour()), startX + 4, startY + 190, -1);
            for (var reward : rewards) {
                var x = startX + spacer + 14;
                var y = 204;
                graphics.drawString(font, withStyleComponentTrans(reward.getCount() + "x ", SUB_HEADER_COLOUR), x - 10, startY + y + 4, -1);
                var y1 = startY + y;
                var x1 = x + 2;
                graphics.renderItem(reward, x1, y1);
                if(mouseX >= x1 && mouseX <= x1 + 15 && mouseY >= y1 && mouseY <= y1 + 15){
                    graphics.renderTooltip(font, reward, (int) mouseX, (int) mouseY);
                }
                spacer += 30;
            }
            graphics.pose().popPose();
        }
    }


    private void claimReward(String id, List<ItemStack> rewards){
        sendToServer(new QuestTrackerC2SP(id));
        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundReg.QUEST_COMPLETE, 1));
        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 0.6F));
        for (var reward : rewards) sendToServer(new GivePlayerItemsC2SP(reward));
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

            int entryCount = this.getAllTasks.size();
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

            int entryCount = this.getAllTasks.size();
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

    @Override
    protected void renderBlurredBackground(float partialTick) {
        super.renderBlurredBackground(partialTick);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        frameTicks++;
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
            graphics.blit(task.taskIcon(), 90, y - 6, 0, 0, size, size, size, size);
            graphics.drawString(font, withStyleComponentTrans(task.taskName(), uiColour()), 112, y-2, -1);
            pose.popPose();

            var player = mc.player;
            var isComplete = task.completionPredicate(player) || QuestTracker.claimedQuest(player, task.taskId());
            var currentValue = task.trackedValue(player);
            var requiredValue = task.countRequired();
            var tracker = Math.min(currentValue, requiredValue) + "/" + requiredValue;
            var complete = "Complete";
            var startX = 192;
            var text = withStyleComponentTrans(isComplete ? complete : tracker, isComplete ? 0 : SUB_HEADER_COLOUR);

            progressBar(graphics, startX, 110, 100, 8, currentValue, requiredValue, 3, colourByPercent(requiredValue, currentValue, true), SUB_HEADER_COLOUR);
            graphics.drawString(font, withStyleComponentTrans(task.taskDescription(), SUB_HEADER_COLOUR), startX + 32, 96, -1);
            centeredStringNoShadow(graphics, font, text,  startX + 98, 114, -1, false);
            renderRewards(graphics);
        }

        graphics.disableScissor();
        this.rebuildWidgets();
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {}

}
