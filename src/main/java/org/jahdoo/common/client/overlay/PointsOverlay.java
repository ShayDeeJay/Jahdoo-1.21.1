package org.jahdoo.common.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;

public class PointsOverlay extends AbstractTimedOverlay {
    int points;
    int difference;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {

        var minecraft = ClientHelpers.getMinecraft();
        var player =  minecraft.player;

        if(player == null || !(player.level() instanceof ClientLevel cLevel)) return;
        if(!LevelGenerator.isNexus(cLevel) || InstanceData.hasInstanceStarted(cLevel)) return;
        super.render(graphics, deltaTracker);

        var font = minecraft.font;
        var size = 18;
        var runData = RunData.getRunData(player);
        var exp = runData.getStat(RunData.EXPERIENCE);

        if(points != exp) {
            var round = Math.round(fadeIn);
            var round1 = Math.round(maxFadeIn/2);

            if(round >= round1) fadeIn = minFadeIn;
            if(difference != 0 && points != 0) {
                timer = 100;
                var soundManager = ClientHelpers.getMinecraft().getSoundManager();
                soundManager.play(forUI(SoundReg.INCREASE_SCORE.get(), 1.2F, 1));
            }

            difference = exp - points;
            points = exp;
        }

        renderExperienceOverlay(graphics, font, exp, difference, fadeIn, size, true);

    }

    private void renderExperienceOverlay(
        GuiGraphics graphics,
        Font font,
        int exp,
        int difference,
        float fadeIn,
        int size,
        boolean centered
    ) {
        var info = TextHelpers
            .withStyleComponent(InstanceDataOverlay.HudEntry.getComp(exp).getString(), ColourHelpers.getCosmicPurple());

        var x1 = graphics.guiWidth() / 10;
        var y1 = -1;

        graphics.pose().pushPose();
        graphics.pose().scale(2, 2, 2);
        graphics.blit(Icons.TRIAL_EXPERIENCE, x1, y1, 0, 0, size, size, size, size);
        graphics.drawString(font, info, x1 + 16, y1 + 5, ColourHelpers.getCosmicPurple(), true);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, fadeIn);

        var y2A = (int) fadeIn + graphics.guiHeight() / 6;
        var xA = graphics.guiWidth() / 4 + 4;
        var yA = y2A + 22;

        var y2B = (int) fadeIn - 10;
        var xB = x1 + 16 + (exp + "").length() * 6 + 2;
        var yB = y2B + 5;

        var getX = centered ? xA : xB;
        var getY = centered ? yA : yB;

        getScore(graphics, font, difference, getX, getY);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        graphics.pose().popPose();
    }

    private void getScore(GuiGraphics graphics, Font font, int difference, int x, int y) {
        var color = ColourHelpers.colourByPercentRanged(500, difference);
        var append = color == ColourHelpers.getRating2Red() ? "!" : "";
        graphics.drawString(
            font, "+" + difference + append, x, y, color, true
        );
    }

}
