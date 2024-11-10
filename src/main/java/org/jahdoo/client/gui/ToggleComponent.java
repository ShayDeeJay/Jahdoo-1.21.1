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
        return new GuiButton(posX, posY, button, 32, action, false, resourceLocation, label);
    }

    public static GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, 32, action, false, resourceLocation);
    }

    public static Renderable textWithBackground(int posX, int posY, Component textOverlay, Minecraft minecraft,Component header) {
        return new Overlay() {
            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                int width1 = 96;
                int height1 = 32;
                guiGraphics.drawCenteredString(minecraft.font, textOverlay, posX + 48, posY + 12, -2763307);
                guiGraphics.blit(TEXT_BACKGROUND, posX, posY, 0,0, width1, height1, width1, height1);
                guiGraphics.drawCenteredString(minecraft.font, header, posX + 48, posY - 7, -1);
            }
        };
    }

}
