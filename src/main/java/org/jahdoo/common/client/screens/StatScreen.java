package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.registers.mod.ElementReg;

import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.effect.MobEffects.REGENERATION;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static org.jahdoo.ascension.boon.player_boons.BoonSelection.iconFromEffect;
import static org.jahdoo.ascension.utils.ColourStore.AETHER_BLUE;
import static org.jahdoo.ascension.utils.ColourStore.COOLDOWN_GREEN;
import static org.jahdoo.common.client.Icons.ABILITY;
import static org.jahdoo.common.client.Icons.STAT;
import static org.jahdoo.common.client.OverlayHelpers.*;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonAbility;
import static org.jahdoo.common.client.overlay.WalletOverlay.renderWallet;
import static org.jahdoo.common.registers.AttributeReg.*;
public class StatScreen extends AbstractPanableScreen {

    @Override
    protected void init() {
        screenTab();
    }

    private void screenTab() {
        this.addRenderableWidget(
            menuButtonAbility(
                this.width - 40, 10, (Button) -> { getMinecraft().setScreen(new StatScreen()); },
                STAT, 30, false, () -> {}, 0, true, "Stats"
            )
        );

        this.addRenderableWidget(
            menuButtonAbility(
                this.width - 80, 10, (Button) -> { getMinecraft().setScreen(new AbilityUnlockScreen()); },
                ABILITY, 30, false, () -> {}, 0, false, "Abilities"
            )
        );
    }

    private void elementalStats(GuiGraphics guiGraphics, LocalPlayer player, float i, float j, Minecraft mc) {
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
        boxMaker(guiGraphics, i - size + trimWidth, j - size + trimHeight, size - trimWidth, size - trimHeight - 6);
        SharedUI.renderEntityInInventoryFollowsMouse(guiGraphics, i, j - 10, i, j, 50, 0.0625F, mouseX, mouseY, player, 1500);
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        var withPanX =  (centerX + this.panX);
        var withPanY =  (centerY + this.panY);

        otherMagicStats(graphics, centerX, centerY, player, mc);
        elementalStats(graphics, player, centerX - 280, centerY, mc);
        renderPlayer(graphics, mouseX, mouseY, (int) withPanX, (int) withPanY, player);
        renderWallet(graphics, mc, 10, withPanX - 49, withPanY - 180, false);
//        playerLevel(graphics, getMinecraft(), withPanX - 86, withPanY + 72, "Level");
    }

    private void otherMagicStats(GuiGraphics guiGraphics, float i, float j, LocalPlayer player, Minecraft mc) {
        var spacing = 22;
        var xSpacing = i + this.panX + spacing;
        var ySpacing = j + this.panY + 100;

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing - 224,
            "Base Stats",
            iconFromEffect(REGENERATION),
            color(166, 224, 65),
            color(123, 184, 17),
            player.getAttribute(MAX_HEALTH),
            player.getAttribute(MAX_ABSORPTION),
            player.getAttribute(ARMOR),
            player.getAttribute(ATTACK_SPEED),
            player.getAttribute(MOVEMENT_SPEED),
            player.getAttribute(ARMOR_TOUGHNESS),
            player.getAttribute(ATTACK_DAMAGE),
            player.getAttribute(JUMP_STRENGTH)
        );

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing - 114,
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
            guiGraphics, mc, xSpacing, ySpacing - 44,
            "Cooldown",
            Icons.CLOCK,
            COOLDOWN_GREEN,
            color(0, 176, 129),
            player.getAttribute(COOLDOWN_REDUCTION),
            player.getAttribute(SKIP_COOLDOWN)
        );

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing + 6,
            "Magic Damage",
            Icons.EASY,
            color(237, 199, 74),
            color(201, 154, 0),
            player.getAttribute(MAGIC_DAMAGE_MULTIPLIER)
        );
    }

}
