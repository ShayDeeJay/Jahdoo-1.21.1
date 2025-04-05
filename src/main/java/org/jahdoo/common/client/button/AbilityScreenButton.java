package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.registers.SoundReg;

import javax.annotation.Nullable;

public class AbilityScreenButton extends ImageButton {

    private float sizes;
    private final boolean isSelected;
    private final boolean showHover;
    private final int defaultSize;
    private final int totalSize;
    private final OnPress pOnPress;
    private final ResourceLocation buttonOverlay;
    private final String label;

    public AbilityScreenButton(
        int pX,
        int pY,
        WidgetSprites sprites,
        int size,
        OnPress pOnPress,
        boolean isSelected,
        @Nullable ResourceLocation buttonOverlay,
        String label,
        int scale,
        boolean showHover
    ) {
        super(pX, pY, size, size, sprites, pOnPress);
        this.defaultSize = size;
        this.sizes = size;
        this.totalSize = size + scale;
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.buttonOverlay = buttonOverlay;
        this.label = label;
        this.showHover = showHover;
    }

    public float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
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
        this.sizes = defaultSize;
        pOnPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        var normalizedTick = (sizes - defaultSize) / (totalSize - defaultSize);
        var easedTick = easeInOutCubic(normalizedTick);
        var easedValue = (int) (easedTick * (totalSize - defaultSize)) + defaultSize;
        var offset = (easedValue - defaultSize) / 2;

        if(isSelected) sizes = totalSize;
        this.setSize((int) sizes-4, (int) sizes-4);

        graphics.drawCenteredString(Minecraft.getInstance().font, label, this.getX() + 17, this.getY()-8, -1);
        graphics.blit(this.sprites.enabled(), this.getX() - offset, this.getY() - offset, 0, 0, 0, easedValue, easedValue, easedValue, easedValue);

        if (this.isMouseOver(mouseX, mouseY)) {
            sizes = Math.min(sizes + 2f, totalSize);
            int i = 4;
            graphics.pose().pushPose();
            graphics.pose().translate(0,0,2);
//            if(showHover){
//                graphics.blit(SELECTED_GUI_BUTTON_OVERLAY, this.getX() - offset + i / 2, this.getY() - offset + i / 2, 0, 0, 0, easedValue - i, easedValue - i, easedValue - i, easedValue - i);
//            }
            graphics.pose().popPose();
        } else {
            if (sizes > defaultSize) sizes -= 2f;
        }

        if(buttonOverlay != null){
            var uWidth = easedValue;
            graphics.blit(buttonOverlay, this.getX() - offset , this.getY() - offset , 0, 0, 0, uWidth, uWidth, uWidth, uWidth);
        }
    }

}
