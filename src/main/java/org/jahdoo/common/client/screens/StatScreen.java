package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.ElementReg;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.ColourStore.AETHER_BLUE;
import static org.jahdoo.ascension.utils.ColourStore.COOLDOWN_GREEN;
import static org.jahdoo.common.client.OverlayHelpers.elementalModStat;
import static org.jahdoo.common.client.OverlayHelpers.getModStat;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.renderEntityInInventoryFollowsMouse;
import static org.jahdoo.common.client.overlay.WalletOverlay.renderWallet;
import static org.jahdoo.common.registers.AttributeReg.*;

public class StatScreen extends AbstractPanableScreen {

    private void elementalStats(GuiGraphics guiGraphics, LocalPlayer player, int i, int j, Minecraft mc) {
        var x = 0;
        for (var abstractElement : ElementReg.getWithout()) {
            elementalModStat(guiGraphics, mc, player, i + this.panX + 20, j + this.panY + x - 124, "Jahdoo", abstractElement);
            x += 60;
        }
    }

    private static void renderPlayer(GuiGraphics guiGraphics, int mouseX, int mouseY, int i, int j, LocalPlayer player) {
        var trimWidth = 60;
        var trimHeight = 18;
        var size = 100;
        boxMaker(guiGraphics, i - size + trimWidth, j - size + trimHeight, size - trimWidth, size - trimHeight - 6, -1);
        renderEntityInInventoryFollowsMouse(guiGraphics, i, j - 10, i, j, 50, 0.0625F, mouseX, mouseY, player, 1500);
    }

    @Override
    protected void renderObjects(GuiGraphics guiGraphics, int mouseX, int mouseY, LocalPlayer player, int centerX, int centerY, Minecraft mc) {
        if(player != null){
            individualStats(guiGraphics, centerX, centerY, player, mc);
            elementalStats(guiGraphics, player, centerX, centerY, mc);
            renderPlayer(guiGraphics, mouseX, mouseY, (int) (centerX + this.panX), (int) (centerY + this.panY), player);
            renderWallet(guiGraphics, mc, 10, centerX + this.panX - 49, centerY + this.panY - 180, false);
        }
    }

    private void individualStats(GuiGraphics guiGraphics, int i, int j, LocalPlayer player, Minecraft mc) {
        var spacing = 172;
        var xSpacing = i + this.panX + spacing;
        var ySpacing = j + this.panY;
        getModStat(
            guiGraphics, mc, xSpacing, ySpacing - 124,
            "Mana",
            Icons.MANA,
            AETHER_BLUE,
            color(39, 130, 196),
            player.getAttribute(MANA_POOL),
            player.getAttribute(MANA_REGEN),
            player.getAttribute(MANA_COST_REDUCTION),
            player.getAttribute(SKIP_MANA)
        );

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing - 54,
            "Cooldown",
            Icons.CLOCK,
            COOLDOWN_GREEN,
            color(0, 176, 129),
            player.getAttribute(COOLDOWN_REDUCTION),
            player.getAttribute(SKIP_COOLDOWN)
        );

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing - 4,
            "Magic Damage",
            Icons.EASY,
            color(237, 199, 74),
            color(201, 154, 0),
            player.getAttribute(MAGIC_DAMAGE_MULTIPLIER)
        );
    }
}
