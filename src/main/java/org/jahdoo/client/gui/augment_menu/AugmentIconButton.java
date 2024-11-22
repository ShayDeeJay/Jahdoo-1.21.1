package org.jahdoo.client.gui.augment_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.util.FastColor;

public class AugmentIconButton extends ImageButton {
    private float sizes;
    private final int defaultSize;
    private final int totalSize;
    private final OnPress pOnPress;
    private final String value;
    private final boolean isSelected;
    private final boolean isAvailable;

    public AugmentIconButton(int pX, int pY, WidgetSprites sprites, int size, OnPress pOnPress, String value, boolean isSelected, boolean isAvailable) {
        super(pX, pY, size, size, sprites, pOnPress);
        this.defaultSize = size; // Default size is the initial size of the button
        this.sizes = size; // Initialize sizes to the default size
        this.totalSize = size + 10; // Increased size
        this.pOnPress = pOnPress;
        this.value = value;
        this.isSelected = isSelected;
        this.isAvailable = isAvailable;
    }

    public float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if(isSelected) this.sizes = totalSize;
        var normalizedTick = (sizes - defaultSize) / (totalSize - defaultSize);
        var easedTick = easeInOutCubic(normalizedTick);
        var easedValue = (int) (easedTick * (totalSize - defaultSize)) + defaultSize;
        var offset = (easedValue - defaultSize) / 2;
        var colourValid = -2763307;
        var colourInvalid = FastColor.ABGR32.color(256, 217, 215, 215);
        var getColour = !this.isAvailable ? colourValid : colourInvalid;

        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, this.value, this.getX() + 16, this.getY() + 12, getColour);

        if(this.isAvailable){
            if (this.isMouseOver(pMouseX, pMouseY)) {
                sizes = Math.min(sizes + 2f, totalSize);
                this.clicked(pMouseX, pMouseY);
            } else {
                if (sizes > defaultSize) sizes -= 2f;
            }
        }

        pGuiGraphics.blit(this.sprites.enabled(), this.getX() - offset, this.getY() - offset, 0, 0, 0, easedValue, easedValue, easedValue, easedValue);
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(this.isAvailable ? 0.5f : 1f);
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return this.isAvailable && !this.isSelected;
    }

    @Override
    public void onPress() {
        pOnPress.onPress(this);
    }
}
