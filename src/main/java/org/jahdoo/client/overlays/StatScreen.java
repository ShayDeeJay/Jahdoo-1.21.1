package org.jahdoo.client.overlays;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.client.IconLocations;
import org.jahdoo.client.SharedUI;
import org.jahdoo.utils.ColourStore;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.jahdoo.client.IconLocations.WAND_GUI;
import static org.jahdoo.client.gui.block.infusion_table.InfusionTableScreen.IMAGE_SIZE;
import static org.jahdoo.client.overlays.OverlayHelpers.getAllStat;

public class StatScreen extends EffectRenderingInventoryScreen<InventoryMenu> {
    private float fade;
    private int scrollBound;

    public StatScreen(Player player) {
        super(player.inventoryMenu, player.getInventory(), Component.translatable("stat_screen"));
    }

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
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
//        super.renderLabels(guiGraphics, mouseX, mouseY);
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        scrollStatScreen(mouseX, mouseY, dragY, 1);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        this.renderBlurredBackground(partialTick);
        int i = this.width/2;
        int j = this.height/2;
        var player = getMinecraft().player;

//        SharedUI.boxMaker(guiGraphics, i - 200, j - 105, 200, 100, -1);

        var x = ( this.width - IMAGE_SIZE) / 2;
        var y = ( this.height - IMAGE_SIZE) / 2;
        guiGraphics.blit(WAND_GUI, x, y + 38, 0, 0, IMAGE_SIZE, IMAGE_SIZE);

        if(player != null){
            renderStatScreen(guiGraphics, player);
            renderPlayer(guiGraphics, mouseX, mouseY, i, j, player);
        }

        SharedUI.boxMaker(guiGraphics, i - 170, j - 84, 62, 78, ColourStore.HEADER_COLOUR, SharedUI.getFadedColourBackground(0f));
        super.render(guiGraphics, mouseX, mouseY, partialTick);
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


    private static void renderPlayer(GuiGraphics guiGraphics, int mouseX, int mouseY, int i, int j, LocalPlayer player) {
        var trimWidth = 60;
        var trimHeight = 18;
        var size = 100;
        SharedUI.boxMaker(guiGraphics, i - size + trimWidth, j - size + trimHeight, size - trimWidth, size - trimHeight - 6, -1);
        SharedUI.renderEntityInInventoryFollowsMouse(guiGraphics, i, j - 10, i, j, 50, 0.0625F, mouseX, mouseY, player, 1500);
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

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }
}
