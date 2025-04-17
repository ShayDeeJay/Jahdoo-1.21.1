package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import org.jahdoo.common.client.button.FlexiButton;
import org.jahdoo.common.registers.AttachmentReg;
import org.jetbrains.annotations.NotNull;

public class TestingScreen extends AbstractPanableScreen {


    @Override
    protected void init() {
        super.init();
        var player = Minecraft.getInstance().player.getData(AttachmentReg.CASTER_DATA.get());
        var spacer = 0;
        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.enableScissor(3, 55, width - 3, height - 5);
                }
            }
        );

        for (var pastRun : player.getPastRuns().reversed()) {
            this.addRenderableWidget(
                new FlexiButton(20, (int) (this.panY + spacer  + (double) this.height /2), 130, 76, false, false, pastRun, (s) -> {}, player.getPastRuns().indexOf(pastRun) + 1 + "")
            );
            spacer += 100;
        }

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.disableScissor();
                }
            }
        );
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        var zoomScale = this.zoomX + 1;
        panY = Math.min(panY + Math.round(dragY / zoomScale), 1);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var zoomScale = this.zoomX + 1;
        panY = Math.min(panY + Math.round(scrollY * 20 / zoomScale), 1);
        return true;
    }

    @Override
    protected void renderWithScale(GuiGraphics guiGraphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        this.rebuildWidgets();
    }
}
