package org.jahdoo.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.client.GuiButton;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.client.gui.IconLocations.*;

public class TestingElements extends Screen  {

    public int yScroll;
    public int xScroll;

    public TestingElements() {
        super(Component.literal("Augment Menu"));
    }

    @Override
    protected void init() {
        buildComponent();
    }

    public void buildComponent(){
        this.addRenderableWidget(ToggleComponent.menuButton(50, 50, (press) -> {}, POWER_ON, "Power"));
//        this.addRenderableWidget(menuButton(50, 50, (press) -> {}, DIRECTION_ARROW_FORWARD));
    }

    public GuiButton menuButton(int posX, int posY, Button.OnPress action, ResourceLocation resourceLocation) {
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        return new GuiButton(posX, posY, button, 32, action, false, resourceLocation, "dfdsfsdf", 6, true);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
    }

}
