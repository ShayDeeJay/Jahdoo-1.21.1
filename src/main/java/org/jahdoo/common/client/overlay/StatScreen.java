package org.jahdoo.common.client.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.util.List;

import static org.jahdoo.ascension.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.common.client.Icons.WAND_GUI;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.overlay.OverlayHelpers.getAllStat;

public class StatScreen extends Screen {

    private static final int IMAGE_SIZE = 256;
    private float fade;
    private int scrollBound;
    private double scrollSpeed;

    public StatScreen() { super(Component.empty()); }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public Component getTitle() {
        return Component.literal("stat_screen");
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
        scrollStatScreen(mouseX, mouseY, scrollY, 4);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        scrollStatScreen(mouseX, mouseY, dragY, 1);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private static void renderPlayer(GuiGraphics guiGraphics, int mouseX, int mouseY, int i, int j, LocalPlayer player) {
        var trimWidth = 60;
        var trimHeight = 18;
        var size = 100;
        boxMaker(guiGraphics, i - size + trimWidth, j - size + trimHeight, size - trimWidth, size - trimHeight - 6, -1);
        renderEntityInInventoryFollowsMouse(guiGraphics, i, j - 10, i, j, 50, 0.0625F, mouseX, mouseY, player, 1500);
    }

    private void renderStatScreen(GuiGraphics guiGraphics, LocalPlayer player) {
        var minX = statBound().getFirst();
        var minY = statBound().get(1);
        var maxX = statBound().get(2);
        var maxY = statBound().get(3);
        guiGraphics.enableScissor(minX, minY, maxX, maxY);


        scrollBound = getAllStat(guiGraphics, getMinecraft(), player, minX + 4, (int) (minY + 4 + fade));
        guiGraphics.disableScissor();
    }

    public List<Integer> statBound(){
        var i = this.width/2;
        var j = this.height/2;
        var startX = i - 164;
        var minX = startX - 4;
        var minY = j - 82;
        var maxX = startX + 116;
        var maxY = j + 70;
        return List.of(minX, minY, maxX, maxY);
    }

    private void scrollStatScreen(double mouseX, double mouseY, double scrollY, double setScrollSpeed) {
        var setScroll = Math.min(0, fade + (float) (scrollY * setScrollSpeed));
        var setNonScrollBound = 102;
        var setMaxScroll = -scrollBound + setNonScrollBound;
        var minX = statBound().getFirst();
        var minY = statBound().get(1);
        var maxX = statBound().get(2);
        var maxY = statBound().get(3);

        if(mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY ){
            if(scrollBound > setNonScrollBound){
                fade = Math.max(setMaxScroll, setScroll);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);

        int i = this.width/2;
        int j = this.height/2;
        var player = getMinecraft().player;

        var x = ( this.width - IMAGE_SIZE) / 2;
        var y = ( this.height - IMAGE_SIZE) / 2;
        guiGraphics.blit(WAND_GUI, x, y + 38, 0, 0, IMAGE_SIZE, IMAGE_SIZE);

        if(player != null){
            renderStatScreen(guiGraphics, player);
            renderPlayer(guiGraphics, mouseX, mouseY, i, j, player);
        }

        WalletOverlay.renderWallet(guiGraphics, getMinecraft(), 10);

        boxMaker(guiGraphics, i - 170, j - 84, 62, 78, HEADER_COLOUR, getFadedColourBackground(0f));
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

}
