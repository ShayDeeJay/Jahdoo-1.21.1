package org.jahdoo.client.gui;

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
import net.minecraft.sounds.SoundEvents;
import org.jahdoo.client.GuiButton;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static org.jahdoo.client.gui.IconLocations.*;

public class ToggleComponent  {

    public static GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, String label) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, 32, action, false, resourceLocation, label, 6, true);
    }

    public static GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, String label, int size) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, size, action, false, resourceLocation, label, 6, true);
    }

    public static GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, int size, int scale) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, size, action, false, resourceLocation, "", scale, true);
    }

    public static GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, int size, boolean isSelected) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, size, action, isSelected, resourceLocation, "", 6, true);
    }

    public static GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, int size, boolean isSelected, int scale, WidgetSprites button) {
        return new GuiButton(posX, posY, button, size, action, isSelected, resourceLocation, "", scale, true);
    }

    public static GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, int size, boolean isSelected, int scale, WidgetSprites button, boolean showHover) {
        return new GuiButton(posX, posY, button, size, action, isSelected, resourceLocation, "", scale, showHover);
    }

    public static GuiButton menuButtonSound(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, int size, boolean isSelected, int scale, WidgetSprites button, boolean showHover, Runnable hoverAction) {
        return new GuiButton(posX, posY, button, size, action, isSelected, resourceLocation, "", scale, showHover){
            @Override
            public void playDownSound(SoundManager handler) {
                handler.play(SimpleSoundInstance.forUI(SoundEvents.VAULT_INSERT_ITEM, 1.2F));
                handler.play(SimpleSoundInstance.forUI(SoundEvents.VAULT_OPEN_SHUTTER, 1.4F));
            }

            @Override
            public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
                super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                if(this.isMouseOver(pMouseX, pMouseY)){
                    hoverAction.run();
                }
            }

        };
    }

    public static GuiButton menuButtonDrag(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, int size, boolean isSelected, int scale, WidgetSprites button, boolean showHover, Consumer<Double> hoverAction) {
        return new GuiButton(posX, posY, button, size, action, isSelected, resourceLocation, "", scale, showHover){
            @Override
            public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
                hoverAction.accept(mouseY);
                return true;
            }


        };
    }

    public static GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation, int size, boolean isSelected, int scale, WidgetSprites button, boolean showHover, String label) {
        return new GuiButton(posX, posY, button, size, action, isSelected, resourceLocation, label, scale, showHover);
    }




    public static Renderable textWithBackground(int posX, int posY, Minecraft minecraft,Component header) {
        return new Overlay() {
            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                guiGraphics.blit(TEXT_BACKGROUND, posX, posY, 0,0, 0, 0, 0, 0);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + 48, posY - 7,  -6052957);
            }
        };
    }

    public static Renderable textWithBackground(int posX, int posY, Component textOverlay, Minecraft minecraft,Component header) {
        return new Overlay() {
            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                int height1 = 32;
                guiGraphics.drawCenteredString(minecraft.font, textOverlay, posX + 48, posY + 13, -2763307);
                guiGraphics.blit(GUI_BUTTON, posX + 32, posY, 0,0, height1, height1, height1, height1);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + 48, posY - 7, -6052957);
            }
        };
    }

    public static Renderable textRenderable(int posX, int posY, Component textOverlay, Minecraft minecraft) {
        return new Overlay() {
            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                guiGraphics.drawString(minecraft.font, textOverlay, posX + 48, posY + 13, 0, true);
            }
        };
    }

    public static Renderable textWithBackgroundLarge(int posX, int posY, Component textOverlay, Minecraft minecraft, Component header, int scale) {
        return new Overlay() {
            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                int width = 96 - scale;
                int height1 = 32 - scale;
                int i2 = 43;
                guiGraphics.drawCenteredString(minecraft.font, textOverlay, posX + i2, posY + 8, -2763307);
                guiGraphics.blit(TEXT_BACKGROUND, posX, posY, 0,0, width, height1, width, height1);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + i2, posY - 7, -6052957);
            }
        };
    }

}
