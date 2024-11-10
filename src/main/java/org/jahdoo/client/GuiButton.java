package org.jahdoo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

import static org.jahdoo.client.gui.IconLocations.SELECTED_GUI_BUTTON_OVERLAY;

public class GuiButton extends ImageButton {
    private float sizes;
    private final int defaultSize;
    private final int totalSize;
    private final OnPress pOnPress;
    private final boolean isSelected;
    private final ResourceLocation buttonOverlay;
    private String label = "";

    public GuiButton(int pX, int pY, WidgetSprites sprites, int size, OnPress pOnPress, boolean isSelected, @Nullable ResourceLocation buttonOverlay) {
        super(pX, pY, size, size, sprites, pOnPress);
        this.defaultSize = size; // Default size is the initial size of the button
        this.sizes = size; // Initialize sizes to the default size
        this.totalSize = size + 5; // Increased size
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.buttonOverlay = buttonOverlay;
    }

    public GuiButton(int pX, int pY, WidgetSprites sprites, int size, OnPress pOnPress, boolean isSelected, @Nullable ResourceLocation buttonOverlay, String label) {
        super(pX, pY, size, size, sprites, pOnPress);
        this.defaultSize = size; // Default size is the initial size of the button
        this.sizes = size; // Initialize sizes to the default size
        this.totalSize = size + 5; // Increased size
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.buttonOverlay = buttonOverlay;
        this.label = label;
    }

    public float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if(isSelected) sizes = totalSize;
        var normalizedTick = (sizes - defaultSize) / (totalSize - defaultSize);
        var easedTick = easeInOutCubic(normalizedTick);
        var easedValue = (int) (easedTick * (totalSize - defaultSize)) + defaultSize;
        var offset = (easedValue - defaultSize) / 2;
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, label, this.getX() + 17, this.getY()-8, -1);
        pGuiGraphics.blit(this.sprites.enabled(), this.getX() - offset, this.getY() - offset, 0, 0, 0, easedValue, easedValue, easedValue, easedValue);
        if (this.isMouseOver(pMouseX, pMouseY)) {
            sizes = Math.min(sizes + 2f, totalSize);
            int i = 4;
            pGuiGraphics.blit(SELECTED_GUI_BUTTON_OVERLAY, this.getX() - offset + i/2, this.getY() - offset + i/2, 0, 0, 0, easedValue - i, easedValue- i, easedValue- i, easedValue- i);
        } else {
            if (sizes > defaultSize) sizes -= 2f;
        }

        if(buttonOverlay != null){
            pGuiGraphics.blit(buttonOverlay, this.getX() - offset, this.getY() - offset, 1, 0, 0, easedValue, easedValue, easedValue, easedValue);
        }
    }


    @Override
    protected boolean isValidClickButton(int button) {
        return !isSelected;
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
    }

    @Override
    public void onPress() {
        this.sizes = defaultSize;
        pOnPress.onPress(this);
    }
}