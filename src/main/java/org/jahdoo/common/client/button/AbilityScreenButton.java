package org.jahdoo.common.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.common.registers.SoundReg;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import javax.annotation.Nullable;

import static com.mojang.blaze3d.platform.InputConstants.KEY_LSHIFT;
import static com.mojang.blaze3d.systems.RenderSystem.setShaderColor;

public class AbilityScreenButton extends ImageButton {

    private float sizes;
    private final boolean isSelected;
    private final boolean showHover;
    private final int defaultSize;
    private final int totalSize;
    private final OnPress pOnPress;
    private final ResourceLocation buttonOverlay;
    private final String label;
    private final boolean isDummy;
    private final boolean locked;
    private final boolean hasDependency;
    private final boolean isSkill;


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
        boolean showHover,
        boolean isDummy,
        boolean locked,
        boolean hasDependency,
        boolean isSkill
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
        this.isDummy = isDummy;
        this.locked = locked;
        this.hasDependency = hasDependency;
        this.isSkill = isSkill;
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
        SoundHelpers.uiSound(SoundReg.SELECT.get());
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

        var easedValue1 = (int) (easedTick * (totalSize - defaultSize)) + (defaultSize*2);
        var offset1 = (easedValue1 - (defaultSize)) / 2;

        var easedValue = (int) (easedTick * (totalSize - defaultSize)) + defaultSize;
        var offset = (easedValue - defaultSize) / 2;

        if(isSelected) sizes = totalSize;

        graphics.drawCenteredString(Minecraft.getInstance().font, label, this.getX() + 17, this.getY()-8, -1);
        graphics.blit(this.sprites.enabled(), this.getX() - offset1, this.getY() - offset1, 0, 0, 0, easedValue1, easedValue1, easedValue1, easedValue1);

        if (this.isMouseOver(mouseX, mouseY)) {
            var shift = ClientHelpers.isKeyDown(KEY_LSHIFT);

            if(!isDummy){
                RenderSystem.enableBlend();
                setShaderColor(1.0F, 1.0F, 1.0F, !shift || isSkill ? 0.5F : 1F);
                graphics.blit(isSkill ? Icons.LOCKED_SKILL_CENTER : Icons.LOCKED_ABILITY, this.getX() - offset1, this.getY() - offset1, 0, 0, 0, easedValue1, easedValue1, easedValue1, easedValue1);
                setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();
            }

            sizes = Math.min(sizes + 2f, totalSize);
            graphics.pose().pushPose();
            graphics.pose().translate(0,0,2);
            graphics.pose().popPose();
        } else {
            if (sizes > defaultSize) sizes -= 2f;
        }

        if(buttonOverlay != null){
            graphics.blit(buttonOverlay, this.getX() - offset , this.getY() - offset , 0, 0, 0, easedValue, easedValue, easedValue, easedValue);

            if(locked && !isDummy){
                RenderSystem.enableBlend();

                setShaderColor(1.0F, 1.0F, 1.0F, 0.4F);
                graphics.blit(isSkill ? Icons.LOCKED_SKILL_CENTER : Icons.LOCKED_ABILITY_CENTER, this.getX() - offset1, this.getY() - offset1, 0, 0, 0, easedValue1, easedValue1, easedValue1, easedValue1);
                setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


                RenderSystem.disableBlend();
            }

        }
    }

}
