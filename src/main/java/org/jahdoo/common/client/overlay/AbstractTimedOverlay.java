package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public abstract class AbstractTimedOverlay implements LayeredDraw.Layer {

    public int timer;
    public float fadeIn;
    public float maxFadeIn = 10.0F;
    public float minFadeIn = -240.0F;
    public float easeFactor = 0.15F;

    public void slideGui() {

        if (timer > 0) {
            var distanceToMax = maxFadeIn - this.fadeIn;
            var fadeAmount = distanceToMax * easeFactor;
            this.fadeIn = Math.min(this.fadeIn + fadeAmount, maxFadeIn);
        } else {
            var distanceFromMin = this.fadeIn - minFadeIn;
            var fadeAmount = distanceFromMin * easeFactor;
            this.fadeIn = Math.max(this.fadeIn - fadeAmount, minFadeIn);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        slideGui();
        timer = Math.max(0, timer - 1);
    }

}
