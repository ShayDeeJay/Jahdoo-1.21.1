package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static org.jahdoo.ascension.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.getFadedColourBackground;

public abstract class AbstractPanableScreen extends Screen {

    protected double panX;
    protected double panY;
    protected double zoomX;
    protected double smoothToX;

    public AbstractPanableScreen() { super(empty()); }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public Component getTitle() { return literal("stat_screen"); }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.zoomX = Math.min(Math.max(this.zoomX + (scrollY / 12), -0.6), 1);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        var zoomScale = this.zoomX + 1;
        var adjustedDragX = dragX / zoomScale;
        var adjustedDragY = dragY / zoomScale;

        panX += adjustedDragX;
        panY += adjustedDragY;

        return true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        var centerX = this.width / 2;
        var centerY = this.height / 2;

        smoothZoom();
        renderBackground(guiGraphics, centerX, centerY);
        transformativeObjects(guiGraphics, mouseX, mouseY, centerX, centerY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void transformativeObjects(GuiGraphics guiGraphics, int mouseX, int mouseY, int centerX, int centerY) {
        var pose = guiGraphics.pose();
        var mc = getMinecraft();
        var player = mc.player;
        var zoom = (float) (this.smoothToX + 1);

        guiGraphics.enableScissor(3, 3,  this.width - 3, this.height - 4);
        pose.pushPose();
        pose.translate(centerX, centerY, 0);  // Move to center
        pose.scale(zoom, zoom, zoom);  // Scale
        pose.translate(-centerX, -centerY, 0);  // Move back
        renderObjects(guiGraphics, mouseX, mouseY, player, centerX, centerY, mc);
        pose.popPose();
        guiGraphics.disableScissor();
    }

    private static void renderBackground(GuiGraphics guiGraphics, int centerX, int centerY) {
        boxMaker(guiGraphics, 2, 2, centerX - 2, centerY - 2, HEADER_COLOUR, getFadedColourBackground(0.8f));
    }

    private void smoothZoom() {
        var easing = 0.02;
        if(this.zoomX < this.smoothToX){
            this.smoothToX = Math.max(smoothToX - easing, zoomX);
        } else {
            this.smoothToX = Math.min(zoomX, smoothToX + easing);
        }
    }

    protected abstract void renderObjects(GuiGraphics guiGraphics, int mouseX, int mouseY, LocalPlayer player, int centerX, int centerY, Minecraft mc);

}
