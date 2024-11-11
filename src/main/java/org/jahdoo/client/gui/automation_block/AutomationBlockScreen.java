package org.jahdoo.client.gui.automation_block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.client.gui.ToggleComponent;
import org.jahdoo.client.gui.wand_block.AugmentSlot;
import org.jahdoo.registers.AttachmentRegister;

import java.util.List;

import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.gui.IconLocations.*;
import static org.jahdoo.client.gui.ToggleComponent.*;
import static org.jahdoo.items.augments.AugmentItemHelper.setAugmentModificationScreen;

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
        var copy = entity().direction();
        var currentPos = entity().getData(AttachmentRegister.POS);
        int posX = this.width / 2 - 70;
        int posY = this.height / 2 - 100;
        this.modifyAugmentProperties(posX, posY);
        buildCarouselComponent(
            this.getMinecraft(), posX, posY,
            "Direction",
            ()-> movePosition(copy, false, entity()),
            ()-> movePosition(copy, true, entity()),
            copy,
            currentPos
        );
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public AutomationBlockEntity entity(){
        return this.automationBlockMenu.getAutomationEntity();
    }

    private void modifyAugmentProperties(int posX, int posY){
        var currentPower = entity().getData(AttachmentRegister.BOOL);
        int size = 16;
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 135, posY + 2, (press) -> togglePower(entity()), currentPower ? POWER_OFF : POWER_ON, "", size));
        if(!this.entity().inputItemHandler.getStackInSlot(0).isEmpty()){
            this.addRenderableWidget(ToggleComponent.menuButton(posX + 135, posY + 14, (press) -> setModifyAugmentScreen(entity().inputItemHandler.getStackInSlot(0).copy()), COG, "", size));
        }
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

    private void setModifyAugmentScreen(ItemStack itemStack){
        setAugmentModificationScreen(itemStack, this);
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

    public void renderSlotWithLabel(GuiGraphics guiGraphics, int i, int i1, String label){
        guiGraphics.drawCenteredString(this.font, Component.literal(label), i + 16, i1 - 8, -1);
        guiGraphics.blit(GUI_GENERAL_SLOT, i, i1, 0, 0, 32, 32,32, 32);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        this.setCustomBackground(guiGraphics);
        int i = this.width / 2;
        int i1 = this.height / 2;
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        abilityIcon(guiGraphics, this.automationBlockMenu.getAutomationEntity().inputItemHandler.getStackInSlot(0), width, height, 109);
        itemSlots(guiGraphics, i, i1);
        renderInventoryBackground(guiGraphics, this, IMAGE_SIZE, 24);
        setSlotTexture(
            guiGraphics,
            i - 128,
            i1 - 160,
            IMAGE_SIZE
        );
    }

    private void itemSlots(GuiGraphics guiGraphics, int i, int i1) {
        var offsetX = automationBlockMenu.offSetX;
        var offsetY = automationBlockMenu.offSetY;
        renderSlotWithLabel(guiGraphics, i + 33 + offsetX, i1 -51 + offsetY, "Output");
        renderSlotWithLabel(guiGraphics, i - 65 + offsetX, i1 -51 + offsetY, "Input");
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
