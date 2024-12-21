package org.jahdoo.client.gui.block.infusion_table;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.jahdoo.utils.ModHelpers;


public class InfusionTableScreen extends AbstractContainerScreen<InfusionTableMenu> {
    int buttonSize = this.getXSize() / 7;
    int selectedEntry;
    public static final int IMAGE_SIZE = 256;
    private boolean render;

//    Map<Integer, UtilitySelector.UtilInfo> utilityAbilitiesDelivery = new UtilitySelector().getMethodName;

    public InfusionTableScreen(InfusionTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
//        super.init();
//        this.titleLabelY = 10000;
//        this.inventoryLabelY = 10000;
//        int buttonWidth = 25; // Set your button width
//        int buttonHeight = 25; // Set your button height
//        int columns = 3; // Set the number of columns you want
//
//        for (int i = 0; i < utilityAbilitiesDelivery.size(); i++) {
//            int row = i / columns;
//            int col = i % columns;
//
//            int x = (width - IMAGE_SIZE) / 2 + 50 + (col * (buttonWidth + 5)); // Adjust spacing between buttons as needed
//            int y = (height - IMAGE_SIZE) / 2 + 50 + (row * (buttonHeight + 5)); // Adjust spacing between buttons as needed
//            int actualIndex = i + 1;
//
//            this.addRenderableWidget(
//                new UnlockAbilitiesEntries(
//                    x, y,
//                    utilityAbilitiesDelivery.get(actualIndex).iconLocation(),
//                    buttonSize,
//                    utilityAbilitiesDelivery.get(actualIndex).abilityName(),
//                    utilityAbilitiesDelivery.get(actualIndex).description(),
//                    pButton -> {
//                        if(this.minecraft != null){
//                            selectedEntry = actualIndex;
//                            this.minecraft.setScreen(this);
//                        }
//                    },
//                    actualIndex,
//                    selectedEntry
//                )
//            );
//        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - IMAGE_SIZE) / 2;
        int y = (height - IMAGE_SIZE) / 2;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(selectedEntry > 0){
            int offsetY = y + 167;
            guiGraphics.pose().pushPose();
            for (FormattedCharSequence lineText : font.split(Component.translatable(""), 150)) {
                guiGraphics.drawString(getMinecraft().font, lineText, x + 55, offsetY, -5083392,true);
                offsetY += getMinecraft().font.lineHeight; // Increase Y position with spacing
            }
            guiGraphics.pose().popPose();
        }
        guiGraphics.blit(ModHelpers.res("textures/gui/skills_menu.png"),x,y, 0, 0, IMAGE_SIZE, IMAGE_SIZE);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}
