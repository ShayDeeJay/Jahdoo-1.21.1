package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.FormattedText;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.tasks.AbstractTask;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;

public class QuestLogButton extends ImageButton {

    private final float width;
    private final float height;
    private final boolean questComplete;
    private final boolean isSelected;
    private final OnPress pOnPress;
    private final AbstractTask task;

    public QuestLogButton(
        int pX,
        int pY,
        int width,
        int height,
        boolean questComplete,
        boolean isSelected,
        OnPress pOnPress,
        AbstractTask task
    ) {
        super(pX, pY, width, height, new WidgetSprites(Icons.BLANK,Icons.BLANK), pOnPress);
        this.width = width;
        this.height = height;
        this.pOnPress = pOnPress;
        this.questComplete = questComplete;
        this.isSelected = isSelected;
        this.task = task;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return true;
    }

    @Override
    public void playDownSound(SoundManager handler) {
        SoundHelpers.uiSound(SoundReg.SELECT.get());
    }

    @Override
    public void onPress() {
        pOnPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        var fade1 = ColourHelpers.getRating5Green();
        var fade = isHovered ? color(60, uiColour()) : fadeBlack(1);
        var minecraft = Minecraft.getInstance();
        var getXStart = this.getX();
        var startY2 = this.getY() + 2;
        var spacer = 0;
        var width = (int) this.width / 2;
        var height = (int) this.height / 2;
        var sizeIcon = 32;
        var sizeComplete = 16;

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 1);
        var selectedColour = isSelected ? ColourHelpers.getBorderColour() : fade;
        boxMaker(graphics, this.getX(), this.getY(), width, height, questComplete ? fade1 : color(100, uiColour()), selectedColour, selectedColour);
        graphics.blit(task.taskIcon(), getXStart, startY2, 0, 0, sizeIcon, sizeIcon, sizeIcon, sizeIcon);

        if(!this.questComplete && task.completionPredicate(minecraft.player)){
            graphics.blit(Icons.TICK, getXStart + 112, startY2 - 2, 0, 0, sizeComplete, sizeComplete, sizeComplete, sizeComplete);
        }

        var string = minecraft.font.ellipsize(FormattedText.of(task.taskName()), 95).getString();
        graphics.drawString(minecraft.font, string, getXStart + 32, startY2 + spacer + 12, questComplete ? fade1 : uiColour(), true);
        graphics.pose().popPose();
    }


}
