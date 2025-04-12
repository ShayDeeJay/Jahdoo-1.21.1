package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

import static org.jahdoo.common.client.OverlayHelpers.playerLevel;

public class RunScreen extends AbstractPanableScreen {

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        var zoomScale = this.zoomX + 1;
        panY = Math.min(panY + Math.round(dragY / zoomScale), 1);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var zoomScale = this.zoomX + 1;
        panY = Math.min(panY + Math.round(scrollY * 20 / zoomScale), 1);
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        var withPanX =  (centerX + this.panX);
        var withPanY =  (14 + this.panY);

        playerLevel(graphics, getMinecraft(), withPanX - 86, withPanY + 72, uiColour());
    }

}
