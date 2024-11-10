package org.jahdoo.client.gui.automation_block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.client.gui.ToggleComponent;
import org.jahdoo.registers.AttachmentRegister;

import java.util.List;

import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.gui.IconLocations.*;
import static org.jahdoo.client.gui.ToggleComponent.*;

public class AutomationBlockScreen extends AbstractContainerScreen<AutomationBlockMenu> {
    private static final int IMAGE_SIZE = 256;
    private final AutomationBlockMenu automationBlockMenu;

    public AutomationBlockScreen(AutomationBlockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.automationBlockMenu = pMenu;
    }

    @Override
    protected void init() {
        super.init();
        var entity = this.automationBlockMenu.getAutomationEntity();
        var copy = entity.direction();
        var currentPos = entity.getData(AttachmentRegister.POS);
        var currentPower = entity.getData(AttachmentRegister.BOOL);
        int posX = this.width / 2 - 70;
        int posY = this.height / 2 - 100;
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 135, posY, (press) -> togglePower(entity), currentPower ? POWER_OFF : POWER_ON, "Power"));
        buildCarouselComponent(
            this.getMinecraft(), posX, posY,
            "Direction",
            ()-> movePosition(copy, false, entity),
            ()-> movePosition(copy, true, entity),
            copy,
            currentPos
        );
    }

    private void togglePower(AutomationBlockEntity entity){
        AutomationBlockData.togglePower(entity);
        this.rebuildWidgets();
    }
    
    private void movePosition(List<Pair<String, BlockPos>> pos, boolean forward, AutomationBlockEntity entity) {
        AutomationBlockData.movePosition(pos, forward, entity);
        this.rebuildWidgets();
    }

    public void buildCarouselComponent(
        Minecraft minecraft, 
        int posX, int posY, 
        String label,
        Runnable buttonPressBack, 
        Runnable buttonPressForward,
        List<Pair<String, BlockPos>> copy,
        BlockPos currentPos
    ){
        var getName = copy.stream().filter(pair -> pair.getSecond().equals(currentPos)).findFirst();
        getName.ifPresent(pair -> this.addRenderableOnly(textWithBackground(posX + 22, posY, Component.literal(pair.getFirst()), minecraft, Component.literal(label))));
        this.addRenderableWidget(menuButton(posX, posY, (press) -> buttonPressBack.run(), DIRECTION_ARROW_BACK));
        this.addRenderableWidget(menuButton(posX + 108, posY, (press) -> buttonPressForward.run(), DIRECTION_ARROW_FORWARD));
    }

    private void setCustomBackground(GuiGraphics guiGraphics){
        var width = this.width/2;
        var height = this.height/2;
        var widthOffset = 100;
        var heightOffset = 115;
        var widthFrom = width - widthOffset;
        var heightFrom = height - heightOffset;
        var widthTo = width + widthOffset;
        var heightTo = height + heightOffset;
        var fromColour = -1072689136;
        var toColour = -804253680;
        var borderColour = -10066330;

        guiGraphics.fillGradient(widthFrom, heightFrom, widthTo, heightTo, fromColour, toColour);
        guiGraphics.renderOutline(widthFrom, heightFrom, widthTo - widthFrom, heightTo - heightFrom, borderColour);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        this.setCustomBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        abilityIcon(guiGraphics, this.automationBlockMenu.getAutomationEntity().inputItemHandler.getStackInSlot(0), width, height, 109);
        renderInventoryBackground(guiGraphics, this, IMAGE_SIZE, 24);
        setSlotTexture(
            guiGraphics,
            this.width/2 - 128,
            this.height/2 - 160,
            IMAGE_SIZE
        );
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
