package org.jahdoo.client.gui.ability_and_utility_menus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jahdoo.client.IconLocations;
import org.jahdoo.client.SharedUI;

public class AbilityIconButton extends ImageButton {
    private float sizes;
    private final int defaultSize;
    private final int totalSize;
    private final OnPress pOnPress;
    private final Runnable runnable;
    private boolean isSelected;

    public AbilityIconButton(int pX, int pY, WidgetSprites sprites, int size, OnPress pOnPress, boolean isSelected, Runnable hover) {
        super(pX, pY, size, size, sprites, pOnPress);
        this.defaultSize = size;
        this.sizes = size;
        this.totalSize = size + 22;
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.runnable = hover;
    }

    public float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        var normalizedTick = (sizes - defaultSize) / (totalSize - defaultSize);
        var easedTick = easeInOutCubic(normalizedTick);
        var easedValue = (int) (easedTick * (totalSize - defaultSize)) + defaultSize;
        var offset = (easedValue - defaultSize) / 2;
        iconHoverEffect(pPartialTick);
        pGuiGraphics.blit(this.sprites.enabled(), this.getX() - offset , this.getY() - offset , 0, 0, 0, easedValue, easedValue, easedValue, easedValue);
        if(isSelected & sizes >= totalSize) sizes = totalSize;
    }

    private void iconHoverEffect(float pPartialTick) {
        var fps = Minecraft.getInstance().getFps();
        var tick = (Math.max(9 - (fps/10), 2));
        if (this.isFocused()) {
            if(!isSelected) this.runnable.run();
            sizes = Math.min(sizes + (tick + pPartialTick), totalSize);
        } else sizes = Math.max(sizes - (tick + pPartialTick), defaultSize);
    }


    @Override
    protected boolean isValidClickButton(int button) {
        return !isSelected;
    }

    @Override
    public void onPress() {
        pOnPress.onPress(this);
    }
}
