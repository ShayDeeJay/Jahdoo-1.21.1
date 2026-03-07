package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jahdoo.common.client.screens.AbstractPanableScreen;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static org.jahdoo.trial_nexus.utils.Icons.ABILITY_BACKGROUND;
import static org.jahdoo.trial_nexus.utils.Icons.SELECTED_GUI_BUTTON_OVERLAY;
import static org.jahdoo.common.client.SharedUI.boxMaker;

public class AbilitySlotButton extends ImageButton {

    private final boolean isSelected;
    private final boolean showHover;
    private final int defaultSize;
    private final int totalSize;
    private final OnPress pOnPress;
    private final ResourceLocation buttonOverlay;
    private final String label;
    private final int slotIndex;
    private final boolean validSlot;
    private final boolean canPress;
    private static final WidgetSprites SPRITES = new WidgetSprites(ABILITY_BACKGROUND, ABILITY_BACKGROUND);

    public AbilitySlotButton(
        int pX,
        int pY,
        int size,
        int slotIndex,
        String label,
        boolean isSelected,
        boolean showHover,
        boolean validSlot,
        boolean canPress,
        @Nullable ResourceLocation buttonOverlay,
        OnPress pOnPress
    ) {
        super(pX, pY, size, size, SPRITES, pOnPress);
        this.defaultSize = size;
        this.totalSize = size ;
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.buttonOverlay = buttonOverlay;
        this.label = label;
        this.showHover = showHover;
        this.slotIndex = slotIndex;
        this.validSlot = validSlot;
        this.canPress = canPress;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return canPress;
    }

    @Override
    public void playDownSound(SoundManager handler) {
        if(validSlot && canPress){
            SoundHelpers.uiSound(SoundReg.SELECT.get());
        }
    }

    @Override
    public void onPress() {
        pOnPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if(player == null) return;

        var colourFaded = FastColor.ARGB32.color(190, AbstractPanableScreen.uiColour());
        var i1 = this.totalSize / 2 - 3;
        renderNameAndBackground(graphics, mc, i1, colourFaded, defaultSize);
        outlineSelectedButton(graphics, i1, colourFaded);
        onIsHovered(graphics, mouseX, mouseY, mc, defaultSize);
        showIconOrIndex(graphics, defaultSize, mc, i1);
    }

    private void renderNameAndBackground(GuiGraphics graphics, Minecraft mc, int i1, int colourFaded, int easedValue) {
        graphics.drawCenteredString(mc.font, label, this.getX() + i1 + 3, this.getY() + totalSize + 3, isSelected ? colourFaded : ColourHelpers.getSubHeaderColour());
        graphics.blit(this.sprites.enabled(), this.getX(), this.getY(), 0, 0, 0, easedValue, easedValue, easedValue, easedValue);
    }

    private void onIsHovered(GuiGraphics graphics, int mouseX, int mouseY, Minecraft mc, int easedValue) {
        if (this.isMouseOver(mouseX, mouseY)) {
            showLevelUnlock(graphics, mouseX, mouseY, mc);
            overlayHoverBorder(graphics, easedValue);
        }
    }

    private void outlineSelectedButton(GuiGraphics graphics, int i1, int colourFaded) {
        if(!isSelected) return;
        boxMaker(graphics, this.getX() + 3, this.getY() + 3, i1, i1, colourFaded, 0, 0);
    }

    private void showIconOrIndex(GuiGraphics graphics, int easedValue, Minecraft mc, int i1) {
        var i = easedValue/3;
        var size = easedValue - i;
        if(buttonOverlay != null){
            var x = this.getX() + i / 2;
            var y = this.getY() + i / 2;
            graphics.blit(buttonOverlay, x, y, 1, 0, 0, size, size, size, size);
        } else {
            var text = validSlot ? slotIndex + 1 + "" : "⧈";
            var x = this.getX() + i1 + 3;
            var y = this.getY() + i1 - 1;
            var color = validSlot ? ColourHelpers.getSubHeaderColour() : ColourHelpers.getHeaderColour();
            graphics.drawCenteredString(mc.font, text, x, y, color);
        }
    }

    private void overlayHoverBorder(GuiGraphics graphics, int easedValue) {
        graphics.pose().pushPose();
        graphics.pose().translate(0,0,2);
        if(showHover && validSlot){
            var x = this.getX();
            var y = this.getY();
            graphics.blit(SELECTED_GUI_BUTTON_OVERLAY, x, y, 0, 0, 0, easedValue, easedValue, easedValue, easedValue);
        }
        graphics.pose().popPose();
    }

    private void showLevelUnlock(GuiGraphics graphics, int mouseX, int mouseY, Minecraft mc) {
        if(!validSlot && !canPress){
            var prefix = TextHelpers.withStyleComponentTrans("info.jahdoo.requires_level", ColourHelpers.getHeaderColour());
            var getUnlockLevel = String.valueOf((slotIndex - 1) * CasterData.UNLOCKED_AT);
            var level = TextHelpers.withStyleComponent(getUnlockLevel, ColourHelpers.getSubHeaderColour());
            graphics.renderTooltip(mc.font, List.of(prefix.copy().append(level)), Optional.empty(), mouseX, mouseY);
        }
    }

}
