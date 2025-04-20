package org.jahdoo.common.client.overlay;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

    }

    private void levelData(GuiGraphics graphics, ClientLevel level, Minecraft mc, InstanceData currentData, Screen screen) {
        var getInstanceData = level.getData(INSTANCE_DATA);
        var getRunData = mc.player.getData(AttachmentReg.RUN_DATA.get());
        var questId = getRunData.getCurrentQuestId();
        var getQuestStuff = QuestReg.getQuestByName(questId);
        var size = 18;
        var spacer = 0;
        var offsetX = -22 + fade;
        var offsetY = -4;
        var remainingTime = getInstanceData.getMaxTime() - getInstanceData.getTicks();
        var pose = graphics.pose();
        var trialHud = new ArrayList<Pair<MutableComponent, ResourceLocation>>();

        trialHud.add(of(appendStat("Room's Completed: ", valueOf(getInstanceData.getClearedRooms()), MAGNET_RANGE_GREEN), Icons.UP));
        trialHud.add(of(appendStat("Time: ", ticksToTime(valueOf(remainingTime)), remainingTime > 400 ? MAGNET_RANGE_GREEN : NEGATIVE_RED), Icons.CLOCK));
        if(getQuestStuff.isPresent()){
            trialHud.add(of(appendStat("", "  ", -1), Icons.BLANK));
            var abstractQuest = getQuestStuff.get();
            trialHud.add(of(appendStat("Quest: ", abstractQuest.getDisplayName() , -1), abstractQuest.questIcon()));
            var current = getRunData.getStat(questId);
            var needed = abstractQuest.questQuantity(mc.player);
            var isComplete = current >= needed;
            var display = isComplete ? "Quest Complete" : current + "/" + needed;
            var colour = isComplete ? PERK_GREEN : MAGNET_STRENGTH_RED;

            trialHud.add(of(appendStat("Progress: ", display, colour), Icons.BLANK));
        }


        timer = Math.max(0, timer - 1);
        instanceData = getInstanceData;

        if(getInstanceData.getDifficulty().isEmpty()) timer = 0;

        pose.pushPose();
        pose.translate(0, (float) graphics.guiHeight() - 160, 0);
        for (var getComp : trialHud) {
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
