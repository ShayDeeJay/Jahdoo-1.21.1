package org.jahdoo.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.client.GuiButton;
import org.jetbrains.annotations.NotNull;
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

    public static Renderable textWithBackgroundLarge(int posX, int posY, Component textOverlay, Minecraft minecraft, Component header) {
        return new Overlay() {
            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                int width = 96;
                int height1 = 32;
                guiGraphics.drawCenteredString(minecraft.font, textOverlay, posX + 48, posY + 13, -2763307);
                guiGraphics.blit(TEXT_BACKGROUND, posX, posY, 0,0, width, height1, width, height1);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + 48, posY - 7, -6052957);
            }
        };
    }

}
