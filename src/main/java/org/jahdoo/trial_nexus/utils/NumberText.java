package org.jahdoo.trial_nexus.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

import static org.jahdoo.trial_nexus.utils.Icons.NUMERIC;

public class NumberText {

    private static final int DIGIT_WIDTH = 3;
    private static final int DIGIT_HEIGHT = 5;
    private static final int DIGIT_SPACING = 1;
    private static final int TEXTURE_PADDING = 1;
    private static final int SPRITESHEET_WIDTH = 32;
    private static final int DIGITS_PER_ROW = 7;

    public static void drawCenteredNumber(GuiGraphics graphics, int number, int centerX, int y, float alpha) {
        String numStr = Integer.toString(number);
        int totalWidth = numStr.length() * DIGIT_WIDTH + (numStr.length() - 1) * DIGIT_SPACING;
        int startX = centerX - totalWidth / 2;

        for (int i = 0; i < numStr.length(); i++) {
            char digitChar = numStr.charAt(i);
            int digit = Character.getNumericValue(digitChar);

            int row = digit / DIGITS_PER_ROW;
            int col = digit % DIGITS_PER_ROW;

            int u = TEXTURE_PADDING + col * (DIGIT_WIDTH + DIGIT_SPACING);
            int v = TEXTURE_PADDING + row * (DIGIT_HEIGHT + DIGIT_SPACING);

            graphics.pose().pushPose();
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, alpha);
            graphics.blit(
                NUMERIC,
                startX + i * (DIGIT_WIDTH + DIGIT_SPACING),
                y, u, v,
                DIGIT_WIDTH, DIGIT_HEIGHT,
                SPRITESHEET_WIDTH, SPRITESHEET_WIDTH
            );
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
            graphics.pose().popPose();
        }
    }

}
