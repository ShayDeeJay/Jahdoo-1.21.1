package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jahdoo.common.client.screens.AbstractPanableScreen;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static org.jahdoo.common.client.Icons.SELECTED_GUI_BUTTON_OVERLAY;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.trial_nexus.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.trial_nexus.utils.ColourStore.SUB_HEADER_COLOUR;

public class AbilitySlotButton extends ImageButton {

    private float sizes;
    private final boolean isSelected;
    private final boolean showHover;
    private final int defaultSize;
    private final int totalSize;
    private final OnPress pOnPress;
    private final ResourceLocation buttonOverlay;
    private final String label;
    private final int slotIndex;

    public AbilitySlotButton(
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
        int slotIndex
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
        this.slotIndex = slotIndex;
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
        var slots = Minecraft.getInstance().player.getData(AttachmentReg.CASTER_DATA.get());
        var validSlot = slotIndex < slots.getAllowedSlots();
        if(validSlot){
            handler.play(SimpleSoundInstance.forUI(SoundReg.SELECT, 1));
        }
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
        var mc = Minecraft.getInstance();
        var player = mc.player;

        if(player == null) return;
        var slots = player.getData(AttachmentReg.CASTER_DATA.get());
        var validSlot = slotIndex < slots.getAllowedSlots();
        var colourFaded = FastColor.ARGB32.color(190, AbstractPanableScreen.uiColour());

        if(isSelected) sizes = totalSize;
        this.setSize((int) sizes-4, (int) sizes-4);

        graphics.drawCenteredString(mc.font, label, this.getX() + this.totalSize/2 +1, this.getY() + totalSize + 2, isSelected ? colourFaded : ColourStore.SUB_HEADER_COLOUR);
        graphics.blit(this.sprites.enabled(), this.getX() - offset, this.getY() - offset, 0, 0, 0, easedValue, easedValue, easedValue, easedValue);

        if(isSelected) {
            boxMaker(graphics, this.getX() + 3, this.getY() + 3, 12, 12, colourFaded, 0, 0);
        }

        if (this.isMouseOver(mouseX, mouseY)) {
            if(!validSlot){
                var prefix = Helpers.withStyleComponent("Requires Level: ", HEADER_COLOUR);
                var level = Helpers.withStyleComponent(((slotIndex - 1) * CasterData.UNLOCKED_AT)  + "", ColourStore.SUB_HEADER_COLOUR);
                graphics.renderTooltip(mc.font, List.of(prefix.copy().append(level)), Optional.empty(), mouseX, mouseY);
            }
            sizes = Math.min(sizes + 2f, totalSize);
            var i = 0;
            graphics.pose().pushPose();
            graphics.pose().translate(0,0,2);
            if(showHover && validSlot){
                graphics.blit(SELECTED_GUI_BUTTON_OVERLAY, this.getX() - offset, this.getY() - offset, 0, 0, 0, easedValue - i, easedValue - i, easedValue - i, easedValue - i);
            }
            graphics.pose().popPose();
        } else {
            if (sizes > defaultSize) sizes -= 2f;
        }

        var i = easedValue/3;
        var size = easedValue - i;
        if(buttonOverlay != null){
            graphics.blit(buttonOverlay, this.getX() - offset + i/2, this.getY() - offset + i/2, 1, 0, 0, size, size, size, size);
        } else {
            graphics.drawCenteredString(mc.font, validSlot ? slotIndex + 1 + "" : "⧈", this.getX() - offset + (this.totalSize/2), this.getY() - offset + (this.totalSize/2) - 4, validSlot ? SUB_HEADER_COLOUR : HEADER_COLOUR);
        }
    }

}
