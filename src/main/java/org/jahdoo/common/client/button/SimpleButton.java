package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.sounds.SoundManager;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.SoundReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.SharedUI.boxMaker2;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;

public class SimpleButton extends ImageButton {

    private final float width;
    private final float height;
    private final boolean isSelected;
    private final OnPress pOnPress;
    private final String label;

    public SimpleButton(
        int pX,
        int pY,
        int width,
        int height,
        boolean isSelected,
        OnPress pOnPress,
        String label
    ) {
        super(pX, pY, width, height, new WidgetSprites(Icons.BLANK,Icons.BLANK), pOnPress);
        this.width = width;
        this.height = height;
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.label = label;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return isSelected;
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
        var fade = isSelected && isHovered ? color(60, uiColour()) : fadeBlack(0.8f);
        var minecraft = Minecraft.getInstance();
        var isSelected = !this.isSelected ? ColourHelpers.getHeaderColour() : uiColour();

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 1);
        boxMaker2(graphics, this.getX(), this.getY(), (int) width, (int) height, isSelected, fade, fade);
        graphics.drawCenteredString(minecraft.font, label, (int) (this.getX() + width/2), (int) (this.getY() + height/2) - 4, isSelected);
        graphics.pose().popPose();
    }


}
