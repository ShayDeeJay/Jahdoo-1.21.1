package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.SoundReg;

import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;
import static org.jahdoo.trial_nexus.utils.ColourStore.HEADER_COLOUR;

public class QuestLogButton extends ImageButton {

    private final float width;
    private final float height;
    private final boolean isSelected;
    private final OnPress pOnPress;
    private final ResourceLocation questIcon;
    private final List<Component> questDetails;


    public QuestLogButton(
        int pX,
        int pY,
        int width,
        int height,
        boolean isSelected,
        OnPress pOnPress,
        ResourceLocation questIcon,
        List<Component> questDetails
    ) {
        super(pX, pY, width, height, new WidgetSprites(Icons.BLANK,Icons.BLANK), pOnPress);
        this.width = width;
        this.height = height;
        this.pOnPress = pOnPress;
        this.isSelected = isSelected;
        this.questIcon = questIcon;
        this.questDetails = questDetails;
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
        pOnPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        var fade = isHovered ? color(60, uiColour()) : uiFade();
        var minecraft = Minecraft.getInstance();
        var getXStart = this.getX();
        var startY2 = this.getY() + 2;
        var isSelected = !this.isSelected ? fade : color(160, HEADER_COLOUR);
        var borderColour =  0;
        var spacer = 0;
        var width = (int) this.width / 2;
        var height = (int) this.height / 2;
        var size = 32;

        boxMaker(graphics, this.getX(), this.getY(), width, height, borderColour, isSelected, isSelected);
        graphics.blit(questIcon, getXStart, startY2, 0, 0, size, size, size, size);
        for (var getAllComponent : questDetails) {
            var componentText = minecraft.font.ellipsize(FormattedText.of(getAllComponent.getString()), 95).getString();
            var componentColour = getAllComponent.getStyle().getColor().getValue();

            graphics.drawString(minecraft.font, componentText, getXStart + 32, startY2 + spacer + 8, componentColour, true);
            spacer += 10;
        }
    }


}
