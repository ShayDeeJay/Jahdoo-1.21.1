package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.button.ToggleComponent;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.common.client.OverlayHelpers.elementalModStat;

public class AbilityUnlockScreen extends AbstractPanableScreen {

    @Override
    protected void init() {
        super.init();
        var withPanX =  (this.panX - 31);
        var withPanY =  (this.panY);
        renderButton(withPanX - 60, withPanY, Icons.FROST_ICON);
        renderButton(withPanX - 0, withPanY, Icons.INFERNO_ICON);
        renderButton(withPanX + 60, withPanY, Icons.MYSTIC_ICON);
        renderButton(withPanX + 120, withPanY, Icons.VITALITY_ICON);
    }

    private void renderButton(double withPanX, double withPanY, ResourceLocation icons) {
        this.addRenderableWidget(ToggleComponent.menuButton((int) ((double) this.width /2 + withPanX) - 15, (int) ((double) this.height /2 + withPanY)- 15, (Button) -> {}, icons, "", 30));
    }

    private void elementalStats(GuiGraphics guiGraphics, LocalPlayer player, float i, float j, Minecraft mc) {
        var x = 0;
        for (var abstractElement : ElementReg.getWithout()) {
            elementalModStat(guiGraphics, mc, player, i + this.panX + 20, j + this.panY + x - 124, "Jahdoo", abstractElement);
            x += 60;
        }
    }

    @Override
    protected void renderObjects(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        var withPanX =  (centerX + this.panX);
        var withPanY =  (centerY + this.panY);
//        renderButton(withPanX - 60, withPanY, Icons.FROST_ICON);
//        elementalStats(graphics, player, centerX - 280, centerY, mc);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.rebuildWidgets();
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.rebuildWidgets();
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}
