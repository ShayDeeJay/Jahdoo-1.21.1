package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;

import static org.jahdoo.ascension.attachments.RunData.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Maths.ticksToTime;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.screens.StatScreen.fadeBackground;

public class RunScreen extends AbstractPanableScreen {

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        var zoomScale = this.zoomX + 1;
        panY = Math.min(panY + Math.round(dragY / zoomScale), 1);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var zoomScale = this.zoomX + 1;
        panY = Math.min(panY + Math.round(scrollY * 20 / zoomScale), 1);
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        var withPanX =  (centerX + this.panX);
        var withPanY =  (this.panY);

        playerLevel(graphics, getMinecraft(), withPanX - 86, withPanY + (double) this.height / 2, uiColour());
    }

    public void playerLevel(
        GuiGraphics graphics,
        Minecraft minecraft,
        double startX,
        double startY,
        int primary
    ) {
        var startX1 = (int) startX - 6;
        var startY2 = (int) startY;
        var headerX = startX1 - 8;
        var data1 = minecraft.player.getData(AttachmentReg.CASTER_DATA.get());
        var getXStart = headerX + 46;
        var spacer = 0;
        var runs = data1.getPastRuns().reversed();
        var index = runs.size();

        boxMaker(graphics, getXStart - 30, -100000000, 83, 100000000, primary, fadeBackground, fadeBackground);

        if(runs.isEmpty()){
            var pre1 = Helpers.withStyleComponent("No Runs Registered", primary);
            graphics.drawCenteredString(minecraft.font, pre1, graphics.guiWidth() / 2, graphics.guiHeight()/2 + 20, -1);
        }

        for (var pastRun : runs) {
            var preA = Helpers.withStyleComponent(index + "", primary);
            graphics.drawCenteredString(minecraft.font, preA, graphics.guiWidth()/2, startY2 + spacer - 18, -1);

            var preDate = Helpers.withStyleComponent("Date: ", HEADER_COLOUR);
            var valueDate = Helpers.withStyleComponent(pastRun.getDateAndTime().split(" ")[0], SUB_HEADER_COLOUR);
            var appendDate = preDate.copy().append(valueDate);
            graphics.drawString(minecraft.font, appendDate, getXStart, startY2 + spacer, -1);

            var preTime = Helpers.withStyleComponent("Time: ", HEADER_COLOUR);
            var valueTime = Helpers.withStyleComponent(pastRun.getDateAndTime().split(" ")[1], SUB_HEADER_COLOUR);
            var appendTime = preTime.copy().append(valueTime);
            graphics.drawString(minecraft.font, appendTime, getXStart, startY2 + 10 + spacer, -1);

            var preRunTime = Helpers.withStyleComponent("Run Time: ", HEADER_COLOUR);
            var valueRunTime = Helpers.withStyleComponent(ticksToTime(pastRun.getStat(TIME_IN_TRIAL)+""), SUB_HEADER_COLOUR);
            var appendRunTime = preRunTime.copy().append(valueRunTime);
            graphics.drawString(minecraft.font, appendRunTime, getXStart, startY2 + 20 + spacer, -1);

            var preXp = Helpers.withStyleComponent("Experience: ", HEADER_COLOUR);
            var valueXp = Helpers.withStyleComponent(pastRun.getStat(EXPERIENCE)+"XP", ABSORPTION_YELLOW);
            var appendXp = preXp.copy().append(valueXp);
            graphics.drawString(minecraft.font, appendXp, getXStart, startY2 + 30 + spacer, -1);

            var preRoom = Helpers.withStyleComponent("Rooms Cleared: ", HEADER_COLOUR);
            var valueRoom = Helpers.withStyleComponent(pastRun.getStat(ROOMS_CLEARED) + "", AETHER_BLUE);
            var appendRoom = preRoom.copy().append(valueRoom);
            graphics.drawString(minecraft.font, appendRoom, getXStart, startY2 + 40 + spacer, -1);

            var preChest = Helpers.withStyleComponent("Loot Chests: ", HEADER_COLOUR);
            var valueChest = Helpers.withStyleComponent(pastRun.getStat(CHESTS_OPENED) + "", COSMIC_PURPLE);
            var appendChest = preChest.copy().append(valueChest);
            graphics.drawString(minecraft.font, appendChest, getXStart, startY2 + 50 + spacer, -1);

            var preMob = Helpers.withStyleComponent("Mobs Killed: ", HEADER_COLOUR);
            var valueMob = Helpers.withStyleComponent(pastRun.getStat(MOBS_KILLED) + "", MAGNET_STRENGTH_RED);
            var appendMob = preMob.copy().append(valueMob);
            graphics.drawString(minecraft.font, appendMob, getXStart, startY2 + 60 + spacer, -1);

//            boxMaker(graphics, getXStart - 6, startY2 - 6 + spacer, 60, 40, primary, fadeBackground, fadeBackground);

            spacer += 100;
            index--;
        }
    }

}
