package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.sounds.SoundManager;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.HashMap;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;
import static org.jahdoo.common.client.screens.RunScreen.getComponents;

public class FlexiButton extends ImageButton {

    private final float width;
    private final float height;
    private final boolean isSelected;
    private final OnPress pOnPress;
    private final RunData pastRun;


    public FlexiButton(
        int pX,
        int pY,
        int width,
        int height,
        boolean isSelected,
        RunData pastRun,
        OnPress pOnPress
    ) {
        super(pX, pY, width, height, new WidgetSprites(Icons.BLANK,Icons.BLANK), pOnPress);
        this.width = width;
        this.height = height;
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.pastRun = pastRun;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return !isSelected;
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
        var fade = isHovered ? color(60, uiColour()) : uiFade();
        var minecraft = Minecraft.getInstance();
        var trialData = PlayerTrialData.getData(minecraft.player);
        var getXStart = this.getX() + 4;
        var startY2 = this.getY() + 4;
        var spacer = 0;
        var newInst = new InstanceData("", new HashMap<>());
        var getAllComponents = getComponents(newInst, pastRun, trialData).subList(0,3);
        var isSelected = !this.isSelected ? fade : color(60, uiColour());
        var borderColour = this.isSelected ? uiColour() : uiFade();
        var width = (int) this.width / 2;
        var height = (int) this.height / 2;

        graphics.pose().pushPose();
        boxMaker(graphics, this.getX(), this.getY(), width, height, borderColour, isSelected, isSelected);
        for (var getAllComponent : getAllComponents) {
            graphics.drawString(minecraft.font, getAllComponent.component(), getXStart, startY2 + spacer, -1, true);
            spacer += 10;
        }
        graphics.pose().popPose();
    }


}
