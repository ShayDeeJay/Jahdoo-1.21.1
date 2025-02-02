package org.jahdoo.client.overlays;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jahdoo.client.SharedUI;

import static org.jahdoo.client.overlays.OverlayHelpers.jahdooStat;

public class StatScreen extends Screen  {
    private float fade;

    public StatScreen() {
        super(Component.literal("Augment Menu"));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        System.out.println(fade);
        fade = Math.max(-102,  Math.min(0, fade + (float) (scrollY * 4)));
//        if(fade > 0) fade -= ((float) scrollY * 6);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        int i = this.width/2;
        int j = this.height/2;
        var player = getMinecraft().player;
        var size = 100;
        var trimWidth = 60;
        var trimHeight = 40;

        if(player != null){
            guiGraphics.enableScissor(0, this.height/2 - 60, this.width, this.height/2 + 60);
            jahdooStat(guiGraphics, getMinecraft(), player, i - 158, (int) (j - 56 + fade));
            guiGraphics.disableScissor();

            SharedUI.boxMaker(guiGraphics, i - size + trimWidth, j - size + trimHeight, size - trimWidth, size - trimHeight, -1);
            SharedUI.renderEntityInInventoryFollowsMouse(guiGraphics, i, j, i, j, 50, 0.0625F, mouseX, mouseY, player, 1500);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}
}
