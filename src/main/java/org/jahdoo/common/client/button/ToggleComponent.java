package org.jahdoo.common.client.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.registers.SoundReg;

import static org.jahdoo.common.client.Icons.*;

public class ToggleComponent  {

    public static GuiButton menuButton(
        int posX,
        int posY,
        Button.OnPress action,
        ResourceLocation location,
        String label
    ) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, 32, action, false, location, label, 6, true);
    }

    public static GuiButton menuButton(
        int posX,
        int posY,
        Button.OnPress action,
        ResourceLocation location,
        String label,
        int size
    ) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, size, action, false, location, label, 6, true);
    }

    public static GuiButton menuButton(
        int posX,
        int posY,
        Button.OnPress action,
        ResourceLocation location,
        int size,
        int scale
    ) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, size, action, false, location, "", scale, true);
    }

    public static GuiButton menuButton(
        int posX,
        int posY,
        Button.OnPress action,
        ResourceLocation location,
        int size,
        boolean isSelected,
        int scale,
        WidgetSprites button
    ) {
        return new GuiButton(posX, posY, button, size, action, isSelected, location, "", scale, true);
    }

    public static GuiButton menuButton(
        int posX,
        int posY,
        Button.OnPress action,
        ResourceLocation location,
        int size,
        boolean isSelected,
        int scale,
        WidgetSprites button,
        boolean showHover
    ) {
        return new GuiButton(posX, posY, button, size, action, isSelected, location, "", scale, showHover);
    }

    public static AbilitySlotButton menuButtonAbility(
        int posX,
        int posY,
        Button.OnPress action,
        ResourceLocation location,
        int size,
        boolean showHover,
        Runnable hoverAction,
        int slot,
        boolean selected,
        String label
    ) {
        return new AbilitySlotButton(posX, posY, new WidgetSprites(ABILITY_BACKGROUND, ABILITY_BACKGROUND), size, action, selected, location, label, 0, showHover, slot) {

            public void playDownSound(SoundManager handler) {
                handler.play(SimpleSoundInstance.forUI(SoundReg.SELECT.get(), 1F));
            }

            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
                super.renderWidget(graphics, mouseX, mouseY, pPartialTick);
                if(this.isMouseOver(mouseX, mouseY)) hoverAction.run();
            }

        };
    }


    public static GuiButton menuButtonSound(
        int posX,
        int posY,
        Button.OnPress action,
        ResourceLocation location,
        int size,
        boolean active,
        int scale,
        WidgetSprites button,
        boolean showHover,
        Runnable hoverAction
    ) {
        return new GuiButton(posX, posY, button, size, action, active, location, "", scale, showHover) {

            public void playDownSound(SoundManager handler) {
                handler.play(SimpleSoundInstance.forUI(SoundReg.UPGRADE_MODIFIER.get(), 1F, 1F));
//                handler.play(SimpleSoundInstance.forUI(SoundEvents.VAULT_OPEN_SHUTTER, 1.4F));
            }

            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
                super.renderWidget(graphics, mouseX, mouseY, pPartialTick);
                if(this.isMouseOver(mouseX, mouseY)) hoverAction.run();
            }

        };
    }

    public static AbilityScreenButton menuButtonSoundAbilities(
        int posX,
        int posY,
        Button.OnPress action,
        ResourceLocation location,
        int size,
        boolean active,
        int scale,
        WidgetSprites button,
        boolean showHover,
        Runnable hoverAction,
        boolean isDummy,
        boolean locked,
        boolean hasDependency
    ) {
        return new AbilityScreenButton(posX, posY, button, size, action, active, location, "", scale, showHover, isDummy, locked, hasDependency) {

            public void playDownSound(SoundManager handler) {
                if(!locked) handler.play(SimpleSoundInstance.forUI(SoundReg.SELECT, 1F));
            }

            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
                super.renderWidget(graphics, mouseX, mouseY, pPartialTick);
                if(this.isMouseOver(mouseX, mouseY)) hoverAction.run();
            }

        };
    }

    public static Renderable textWithBackground(
        int posX,
        int posY,
        Minecraft minecraft,
        Component header
    ) {
        return new Overlay() {
            public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                guiGraphics.blit(TEXT_BACKGROUND, posX, posY, 0,0, 0, 0, 0, 0);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + 48, posY - 7,  -6052957);
            }
        };
    }

    public static Renderable textWithBackground(
        int posX,
        int posY,
        Component textOverlay,
        Minecraft minecraft,
        Component header
    ) {
        return new Overlay() {
            public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                int height = 32;
                guiGraphics.drawCenteredString(minecraft.font, textOverlay, posX + 48, posY + 13, -2763307);
                guiGraphics.blit(GUI_BUTTON, posX + 32, posY, 0,0, height, height, height, height);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + 48, posY - 7, -6052957);
            }
        };
    }

    public static Renderable textRenderable(
        int posX,
        int posY,
        Component textOverlay,
        Minecraft minecraft
    ) {
        return new Overlay() {
            public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                guiGraphics.drawString(minecraft.font, textOverlay, posX + 48, posY + 13, 0, true);
            }
        };
    }

    public static Renderable textWithBackgroundLarge(
        int posX,
        int posY,
        Component textOverlay,
        Minecraft minecraft,
        Component header,
        int scale
    ) {
        return new Overlay() {
            public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                var width = 96 - scale;
                var height1 = 32 - scale;
                var i2 = 43;

                guiGraphics.drawCenteredString(minecraft.font, textOverlay, posX + i2, posY + 8, -2763307);
                guiGraphics.blit(TEXT_BACKGROUND, posX, posY, 0, 0, width, height1, width, height1);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + i2, posY - 7, -6052957);
            }
        };
    }

    public static Renderable textWithBackgroundScaled(
        int posX,
        int posY,
        Component textOverlay,
        Minecraft minecraft,
        Component header,
        int scale
    ) {
        return new Overlay() {
            public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                var width = 96 - scale;
                var height1 = 32 - scale;
                var i2 = 43;
                var pose = guiGraphics.pose();

                pose.pushPose();
                pose.translate(posX, posY, 0);
                pose.scale(scale, scale, scale);
                pose.translate(-posX, -posY, 0);
                guiGraphics.drawCenteredString(minecraft.font, textOverlay, posX + i2, posY + 8, -2763307);
//                guiGraphics.blit(TEXT_BACKGROUND, posX, posY, 0, 0, width, height1, width, height1);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + i2, posY - 7, -6052957);
                pose.popPose();

            }
        };
    }
}
