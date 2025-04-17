package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.jahdoo.ascension.attachments.RunData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.SoundReg;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.attachments.RunData.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Maths.ticksToTime;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.getFadedColourBackground;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;

public class FlexiButton extends ImageButton {

    private final float width;
    private final float height;
    private final boolean isSelected;
    private final boolean showHover;
    private final OnPress pOnPress;
    private final String label;
    private final RunData pastRun;


    public FlexiButton(
        int pX,
        int pY,
        int width,
        int height,
        boolean isSelected,
        boolean showHover,
        RunData pastRun,
        OnPress pOnPress,
        String label
    ) {
        super(pX, pY, width, height, new WidgetSprites(Icons.BLANK,Icons.BLANK), pOnPress);
        this.width = width;
        this.height = height;
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.label = label;
        this.showHover = showHover;
        this.pastRun = pastRun;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return !isSelected;
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundReg.SELECT, 1));
    }

    @Override
    public void onPress() {
        pOnPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        var fade = isHovered ? color(100, uiColour()) : getFadedColourBackground(0.6F);
        boxMaker(graphics, this.getX(), this.getY(), (int) this.width/2, (int) this.height/2, uiColour(), fade, fade);

        var minecraft = Minecraft.getInstance();
        var getXStart = this.getX() + 4;
        var startY2 = this.getY() + 4;

        var preA = Helpers.withStyleComponent(this.label, uiColour());
        graphics.drawCenteredString(minecraft.font, preA, (int) (this.getX() + this.width/2), startY2  - 18, -1);

        var headerColour = HEADER_COLOUR;
        var preDate = Helpers.withStyleComponent("Date: ", headerColour);
        var valueDate = Helpers.withStyleComponent(pastRun.getDateAndTime().split(" ")[0], SUB_HEADER_COLOUR);
        var appendDate = preDate.copy().append(valueDate);
        graphics.drawString(minecraft.font, appendDate, getXStart, startY2 , -1, false);

        var preTime = Helpers.withStyleComponent("Time: ", headerColour);
        var valueTime = Helpers.withStyleComponent(pastRun.getDateAndTime().split(" ")[1], SUB_HEADER_COLOUR);
        var appendTime = preTime.copy().append(valueTime);
        graphics.drawString(minecraft.font, appendTime, getXStart, startY2 + 10 , -1, false);

        var preRunTime = Helpers.withStyleComponent("Run Time: ", headerColour);
        var valueRunTime = Helpers.withStyleComponent(ticksToTime(pastRun.getStat(TIME_IN_TRIAL)+""), SUB_HEADER_COLOUR);
        var appendRunTime = preRunTime.copy().append(valueRunTime);
        graphics.drawString(minecraft.font, appendRunTime, getXStart, startY2 + 20 , -1, false);

        var preXp = Helpers.withStyleComponent("Experience: ", headerColour);
        var valueXp = Helpers.withStyleComponent(pastRun.getStat(EXPERIENCE)+"XP", ABSORPTION_YELLOW);
        var appendXp = preXp.copy().append(valueXp);
        graphics.drawString(minecraft.font, appendXp, getXStart, startY2 + 30 , -1, false);

        var preRoom = Helpers.withStyleComponent("Rooms Cleared: ", headerColour);
        var valueRoom = Helpers.withStyleComponent(pastRun.getStat(ROOMS_CLEARED) + "", AETHER_BLUE);
        var appendRoom = preRoom.copy().append(valueRoom);
        graphics.drawString(minecraft.font, appendRoom, getXStart, startY2 + 40 , -1, false);

        var preChest = Helpers.withStyleComponent("Loot Chests: ", headerColour);
        var valueChest = Helpers.withStyleComponent(pastRun.getStat(CHESTS_OPENED) + "", COSMIC_PURPLE);
        var appendChest = preChest.copy().append(valueChest);
        graphics.drawString(minecraft.font, appendChest, getXStart, startY2 + 50 , -1, false);

        var preMob = Helpers.withStyleComponent("Mobs Killed: ", headerColour);
        var valueMob = Helpers.withStyleComponent(pastRun.getStat(MOBS_KILLED) + "", MAGNET_STRENGTH_RED);
        var appendMob = preMob.copy().append(valueMob);
        graphics.drawString(minecraft.font, appendMob, getXStart, startY2 + 60 , -1, false);
    }

}
