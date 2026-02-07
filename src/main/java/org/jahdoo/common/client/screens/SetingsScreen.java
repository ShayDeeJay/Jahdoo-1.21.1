package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.registers.mod.ElementReg;
import org.shaydee.shaydeeapi.Colours;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.button.ToggleComponent.menuButton;
import static org.jahdoo.trial_nexus.utils.Configuration.UI_COLOUR;

public class SetingsScreen extends AbstractPanableScreen {
    public static int frameTicks;
    public static final int WIDTH_OFFSET = 80;
    private double mouseX;
    private double mouseY;
    private boolean startAnim;
    private float fade;

    public static final List<Integer> uiColours = List.of(
        Colours.getNetheriteBox(),
        Colours.getSympathiserOrange(),
        ElementReg.utility().textColourB(),
        Colours.getCosmicPurple(),
        Colours.getUniqueA(),
        Colours.getUniqueB()
    );

    @Override
    protected void init() {
        super.init();
        var spacer = 0;
        var sharedX = this.width/2 - 84;
        for (var uiColour : uiColours) {
            int finalSpacer = spacer;
            var comment = Arrays.stream(Objects.requireNonNull(UI_COLOUR.getSpec().getComment()).split("\\r?\\n")).findFirst().get();
            var value = uiColours.indexOf(uiColour);
            this.addRenderableWidget(
                menuButton(sharedX + spacer, 100, (button) -> {
                    this.rebuildWidgets();
                    UI_COLOUR.set(value);
                }, Icons.BLANK, 20, 0, "")
            );
            this.addRenderableOnly(
                new Overlay() {
                    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0, 0, 100);
                        SharedUI.boxMaker(guiGraphics, sharedX + 5 + finalSpacer, 100 + 5, 5, 5, uiColour, uiColour);
                        guiGraphics.pose().popPose();
                        guiGraphics.drawCenteredString(Minecraft.getInstance().font, comment, sharedX + 84, 84, Colours.getOffWhite());
                    }
                }
            );
            spacer += 30;
        }
    }

    private void slideGuiStats() {
        var maxFadeIn = 101.0F;
        var minFadeIn = -240.0F;
        var easeFactor = 0.10F;

        if (startAnim) {
            var distanceToMax = maxFadeIn - this.fade;
            var fadeAmount = distanceToMax * easeFactor;
            this.fade = Math.min(this.fade + fadeAmount, maxFadeIn);
        } else {
            var distanceFromMin = this.fade - minFadeIn;
            var fadeAmount = distanceFromMin * easeFactor;
            this.fade = Math.max(this.fade - fadeAmount, minFadeIn);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        var instance = Minecraft.getInstance();

        return super.shouldCloseOnEsc();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        frameTicks++;
        super.render(graphics, mouseX, mouseY, partialTick);
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        UI_COLOUR.get();

    }

    @Override
    protected void renderWithScale(GuiGraphics guiGraphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {

    }

    private void renderBounding(GuiGraphics graphics, int startX1, int startY, int widthOffset) {
        boxMaker(graphics, startX1, startY +1, widthOffset, this.height/2 - 12, uiColour(), uiFade(), uiFade());
    }

    private void renderHeader(GuiGraphics graphics, int x, int y){
        var scale = 2F;
        var pose = graphics.pose();

        pose.pushPose();
        pose.scale(scale, scale, scale);
        var y1 = y + 30;
        var x1 = x + 54;
        pose.popPose();
    }

    private void renderItem(GuiGraphics graphics, int x, int y) {
        var scale = 4;
        var pose = graphics.pose();
        var fade1 = fade / 28f;
        var renderX = x * scale + 60;
        var renderY = scale + 52;
        var centerX = renderX + 8;
        var centerY = renderY + 8;

        pose.pushPose();
        pose.translate(centerX, centerY, 0);
        pose.scale(fade1, fade1, fade1);
        pose.translate(-centerX, -centerY, 0);
        pose.popPose();

        var padding = 34;
        boxMaker(
            graphics,
            padding - 4,
            padding - 4,
            this.width / 2 - padding + 4 + y,
            padding,
            uiColour(),
            uiFade(),
            uiFade()
        );
    }

    private void additionalInformation(GuiGraphics graphics, int x, int y) {
//        var text = withStyleComponentTrans(codex.description(), SUB_HEADER_COLOUR);
//        textWithWidthAdjust(font, x, y, text, graphics);
    }

    private void description(GuiGraphics graphics, int x, int y) {

    }

    private void textWithWidthAdjust(Font font, int x, int y, Component text, GuiGraphics graphics) {
        var maxWidth = width - (x + 16) - 44;
        var lines = font.split(text, maxWidth);
        var yy = y + 120;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, x + 29, yy, -1);
            yy += font.lineHeight;
        }
    }

}
