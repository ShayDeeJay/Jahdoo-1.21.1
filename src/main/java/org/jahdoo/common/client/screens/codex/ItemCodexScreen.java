package org.jahdoo.common.client.screens.codex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.items.JahdooItem;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;

public class ItemCodexScreen extends Screen {
    public static int frameTicks;
    public static final int WIDTH_OFFSET = 80;
    private final ItemStack codec;
    private final Screen lastScreen;
    private double mouseX;
    private double mouseY;
    private final boolean startAnim;
    private float fade;

    private void slideGuiStats() {
        var maxFadeIn = 101.0F;
        var minFadeIn = -240.0F;
        var easeFactor = 0.10F;

        if (startAnim) {
            var distanceToMax = maxFadeIn - this.fade;
            var fadeAmount = distanceToMax * easeFactor;
            this.fade = Math.min(this.fade + fadeAmount, maxFadeIn);
        } else {
            var distanceFromMin = this.fade - minFadeIn;
            var fadeAmount = distanceFromMin * easeFactor;
            this.fade = Math.max(this.fade - fadeAmount, minFadeIn);
        }
    }

    //Might not need a new item, might be worth that all items from mod just need to inherit from jahdoo item
    //and then can filter registry from there. Would also allow jahdoo item to take in extra params
    public ItemCodexScreen(ItemStack codec, Screen lastScreen) {
        super(Component.empty());
        this.lastScreen = lastScreen;
        this.codec = codec;
        this.startAnim = true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        var instance = Minecraft.getInstance();
        if(lastScreen != null) {
            instance.setScreen(lastScreen);
            return false;
        }

        return super.shouldCloseOnEsc();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        frameTicks++;
        super.render(graphics, mouseX, mouseY, partialTick);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.slideGuiStats();
        if(codec != null){
            var x = 0;
            var v = this.fade ;
            var y = (int) (v - 100);
            renderBounding(graphics, 12, 12, width / 2 - 12);
            renderItem(graphics, x, y);
            renderHeader(graphics, -y, 0);
            description(graphics, x, (int) (100 - v));
            SharedUI.bezelMaker(graphics, -12, -12, this.width - 36, this.height - 36, 60, null);
        }
    }

    private void renderBounding(GuiGraphics graphics, int startX1, int startY, int widthOffset) {
        boxMaker(graphics, startX1, startY +1, widthOffset, this.height/2 - 12, uiColour(), uiFade(), uiFade());
    }

    private void renderHeader(GuiGraphics graphics, int x, int y){
        var scale = 2F;
        var pose = graphics.pose();

        pose.pushPose();
        pose.scale(scale, scale, scale);
        var y1 = y + 30;
        var x1 = x + 54;
        graphics.drawString(font, TextHelpers.withStyleComponentTrans(codec.getDescriptionId(), uiColour()), x1, y1 -2, -1);
        pose.popPose();
    }

    private void renderItem(GuiGraphics graphics, int x, int y) {
        var scale = 4;
        var pose = graphics.pose();
        var fade1 = fade / 28f;
        var renderX = x * scale + 60;
        var renderY = scale + 52;
        var centerX = renderX + 8;
        var centerY = renderY + 8;

        pose.pushPose();
        pose.translate(centerX, centerY, 0);
        pose.scale(fade1, fade1, fade1);
        pose.translate(-centerX, -centerY, 0);
        graphics.renderFakeItem(codec, renderX, renderY);
        pose.popPose();

        var padding = 34;
        boxMaker(
            graphics,
            padding - 4,
            padding - 4,
            this.width / 2 - padding + 4 + y,
            padding,
            uiColour(),
            uiFade(),
            uiFade()
        );
    }

    private void additionalInformation(GuiGraphics graphics, int x, int y) {
//        var text = TextHelpers.withStyleComponentTrans(codex.description(), ColourHelpers.getSubHeaderColour());
//        textWithWidthAdjust(font, x, y, text, graphics);
    }

    private void description(GuiGraphics graphics, int x, int y) {
        if(codec.getItem() instanceof JahdooItem item){
            var text = TextHelpers.withStyleComponentTrans(item.descriptionId(), ColourHelpers.getSubHeaderColour());
            textWithWidthAdjust(font, x+1, y-10, text, graphics);
        }
    }

    private void textWithWidthAdjust(Font font, int x, int y, Component text, GuiGraphics graphics) {
        var maxWidth = width - (x + 16) - 44;
        var lines = font.split(text, maxWidth);
        var yy = y + 120;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, x + 29, yy, -1);
            yy += font.lineHeight;
        }
    }

}
