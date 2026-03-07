package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.overlay.InstanceDataOverlay;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE;
import static net.minecraft.world.effect.MobEffects.REGENERATION;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static org.jahdoo.common.client.OverlayHelpers.elementalModStat;
import static org.jahdoo.common.client.OverlayHelpers.getModStat;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.common.client.overlay.WalletOverlay.renderWallet;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.trial_nexus.trackable.player_boons.BoonSelection.iconFromEffect;
public class StatScreen extends AbstractPanableScreen {

    public static int fadeBackground = fadeBlack(0.6F);

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

        boxMaker(guiGraphics, i - size + trimWidth, j - size + trimHeight, size - trimWidth, size - trimHeight - 6, 0, fadeBackground, fadeBackground);
        SharedUI.renderEntityInInventoryFollowsMouse(guiGraphics, i, j - 10, i, j, 50, 0.0625F, mouseX, mouseY, player, 1500);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        var withPanX =  (centerX + this.panX);
        var withPanY =  (centerY + 43 + this.panY);
        var shiftY = 44;

        otherMagicStats(graphics, centerX, centerY - 28 + shiftY, player, mc);
        elementalStats(graphics, player, centerX - 280, centerY + shiftY, mc);
        renderPlayer(graphics, mouseX, mouseY, (int) withPanX, (int) withPanY + shiftY, player);
        renderWallet(graphics, mc, 10, withPanX - 49, withPanY - 200 + shiftY, false, false, true, null);
        SharedUI.renderMiniXPBar(graphics, (int) withPanX - 41, (int) withPanY + 118, Minecraft.getInstance());
    }

    private void otherMagicStats(GuiGraphics guiGraphics, float i, float j, LocalPlayer player, Minecraft mc) {
        var spacing = 22;
        var xSpacing = i + this.panX + spacing;
        var ySpacing = j + this.panY + 100;
        var data = player.getData(AttachmentReg.CASTER_DATA.get());

        playerLevelData(guiGraphics, player, (int) xSpacing, (int) ySpacing, data);

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing - 264,
            "Protections",
            iconFromEffect(DAMAGE_RESISTANCE),
            ColourHelpers.getNegativeRed(),
            player.getAttribute(RESILIENCE)
        );

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing - 224,
            "Base Stats",
            iconFromEffect(REGENERATION),
            ColourHelpers.getPerkGreen(),
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
            ColourHelpers.getAetherBlue(),
            player.getAttribute(MANA_POOL),
            player.getAttribute(MANA_REGEN),
            player.getAttribute(MANA_COST_REDUCTION),
            player.getAttribute(SKIP_MANA)
        );

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing - 44,
            "Cooldown",
            Icons.CLOCK,
            ColourHelpers.getCooldownGreen(),
            player.getAttribute(COOLDOWN_REDUCTION),
            player.getAttribute(SKIP_COOLDOWN)
        );

        getModStat(
            guiGraphics, mc, xSpacing, ySpacing + 6,
            "Magic Damage",
            Icons.EASY,
            ColourHelpers.getColorTransition(ColourHelpers.getCosmicPurple(), ColourHelpers.getNetheriteBox(), (int) mc.level.getGameTime(),100),
            player.getAttribute(MAGIC_DAMAGE_MULTIPLIER)
        );
    }

    private void playerLevelData(GuiGraphics guiGraphics, LocalPlayer player, int xSpacing, int ySpacing, CasterData data) {
        var x = xSpacing - 210;
        var y = ySpacing - 220;
        var colour = ColourHelpers.getCosmicPurple();

        var adjustY = -5;
        SharedUI.boxMaker(guiGraphics, x - 20, y - 8, 70, 14, 0, fadeBackground, fadeBackground);

        var showLevel = TextHelpers.withStyleComponent("Level: ", colour).copy().append(TextHelpers.withStyleComponent("" + data.getLevel(), colour));
        guiGraphics.drawCenteredString(font, showLevel, x + 50, y + adjustY - 1, -1);

        var nextLevel = CasterData.getXpNeededForNextLevel(CasterData.getLevel(player));
        var progressToNextLevel = CasterData.getXpRemainingToNextLevel(player);
        var showNextLevel = TextHelpers.withStyleComponent((nextLevel - progressToNextLevel) + "/" + nextLevel, ColourHelpers.getOffWhite());

        guiGraphics.drawCenteredString(font, showNextLevel, x + 50, y + 12 + adjustY, -1);
        InstanceDataOverlay.progressBar(guiGraphics, x - 18, y + 4, 68, 7, nextLevel - progressToNextLevel, nextLevel, 2, colour, colour, ColourHelpers.getBoxColour());
    }

}
