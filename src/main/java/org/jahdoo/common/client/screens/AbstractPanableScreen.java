package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jahdoo.common.client.SharedUI;

import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.ColourStore.*;
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
        double scaleFactor = Math.max(0.1, Math.abs(zoomX) * 0.5); // Scale dynamically
        this.zoomX = Math.max(-0.1, Math.min(Math.max(this.zoomX + (scrollY * scaleFactor), -0.6), 1));
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        var zoomScale = this.zoomX + 1;

        panX += Math.round(dragX / zoomScale);
        panY += Math.round(dragY / zoomScale);
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        var centerX = this.width / 2;
        var centerY = this.height / 2;

        smoothZoom();
        customRenderBackground(graphics, centerX, centerY);
        transformativeObjects(graphics, mouseX, mouseY, centerX, centerY);
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 100);
        SharedUI.bezelMaker(graphics, -20 , -20, this.width - 20, this.height - 20, 60, null);
        graphics.pose().popPose();
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void transformativeObjects(GuiGraphics guiGraphics, int mouseX, int mouseY, float centerX, float centerY) {
        var pose = guiGraphics.pose();
        var mc = getMinecraft();
        var player = mc.player;
        var zoom = (float) (this.smoothToX + 1);

        baseRender(guiGraphics, mouseX, mouseY, player, centerX, centerY, mc);

        var scissor = 4;
        guiGraphics.enableScissor(scissor, scissor, this.width - scissor, this.height - scissor);
        pose.pushPose();
        pose.translate(centerX, centerY, 0);
        pose.scale(zoom, zoom, zoom);
        pose.translate(-centerX, -centerY, 0);
        renderWithScale(guiGraphics, mouseX, mouseY, player, centerX, centerY, mc);
        pose.popPose();
        guiGraphics.disableScissor();
    }

    public void customRenderBackground(GuiGraphics guiGraphics, int centerX, int centerY) {
        var i = 3;
        var fade = getFadedColourBackground(0.8f);
        boxMaker(guiGraphics, i, i, centerX - i, centerY - i, NEGATIVE_RED, fade, color(60, MAGNET_STRENGTH_RED));
    }

    private void smoothZoom() {
        var easing = 0.02;
        if(this.zoomX < this.smoothToX){
            this.smoothToX = Math.max(smoothToX - easing, zoomX);
        } else {
            this.smoothToX = Math.min(zoomX, smoothToX + easing);
        }
    }

    protected abstract void renderWithScale(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        LocalPlayer player,
        float centerX,
        float centerY,
        Minecraft mc
    );

    protected void baseRender(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        LocalPlayer player,
        float centerX,
        float centerY,
        Minecraft mc
    ){};

}
