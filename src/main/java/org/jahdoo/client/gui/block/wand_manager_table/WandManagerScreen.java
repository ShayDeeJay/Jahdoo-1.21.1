package org.jahdoo.client.gui.block.wand_manager_table;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.block.wand_block_manager.WandManagerTableEntity;
import org.jahdoo.client.gui.block.augment_modification_station.AugmentCoreSlot;
import org.jahdoo.client.gui.block.augment_modification_station.InventorySlots;
import org.jahdoo.components.WandData;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.wand.WandItemHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.client.IconLocations.*;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.gui.ToggleComponent.*;

public class WandManagerScreen extends AbstractContainerScreen<WandManagerMenu> {
    public static WidgetSprites WIDGET = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final WandManagerMenu wandManager;
    private double yScroll;
    public Inventory inventory;
    boolean showInventory;
    boolean setView;
    int section;
    double scale = 1;

    public WandManagerScreen(WandManagerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.wandManager = pMenu;
        this.inventory = pPlayerInventory;
        this.switchVisibility();
    }

    @Override
    protected void init() {
        super.init();
        this.keyboardButton();
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public WandManagerTableEntity entity(){
        return this.wandManager.getWandManagerEntity();
    }

    private void switchVisibility() {
        for (Slot slot : this.menu.slots) {
            if(slot instanceof InventorySlots inventorySlots){
                inventorySlots.setActive(showInventory);
            }

            if(slot instanceof AugmentCoreSlot augmentCoreSlot){
                if(augmentCoreSlot.getItem().getItem() instanceof RuneItem){
                    augmentCoreSlot.setActive(showInventory);
                }
            }
        }
    }

    private void keyboardButton() {
        var resourceLocation = this.showInventory ? INFORMATION : INVENTORY;
        this.addRenderableWidget(
            menuButton(this.width/2 - 133, this.height/2 + 45, (press) -> inventoryHandler(), resourceLocation, 24, false, 0, WIDGET, true)
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(isInHitbox(mouseX, mouseY)) windowMoveVertical(scrollY * 4);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(isInHitbox(mouseX, mouseY)) windowMoveVertical(dragY);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var startX = this.width / 2 - 140;
        var startY = this.height / 2 + 22;
        var i = 40;
        var i1 = -17;
        var startX1 = startX + i + 80;
        var startY1 = startY + i1 - 88;

        var x = mouseX > startX1 && mouseX < startX1 + 40 && mouseY > startY1 && mouseY < startY1 + 34;
        if(x) {
            System.out.println("clicked");
            this.section = this.section == 1 ? 2 : 1;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        var startX = this.width / 2 - 140;
        var startY = this.height / 2 + 22;
        var i = 40;
        var i1 = -17;
        var startX1 = startX + i + 80;
        var startY1 = startY + i1 - 88;
        var x = mouseX > startX1 && mouseX < startX1 + 40 && mouseY > startY1 && mouseY < startY1 + 34;
//        section = x ? 1 : 2;
    }

    public void inventoryHandler(){
        setView = !setView;
        showInventory = !showInventory;
        switchVisibility();
        this.yScroll = 0;
        this.rebuildWidgets();
    }

    public boolean isInHitbox(double mouseX, double mouseY){
        var widthOffset = 100;
        var heightOffset = 115;
        int i = width / 2;
        int i1 = height / 2;
        var widthFrom = i - widthOffset;
        var heightFrom = i1 - heightOffset;
        var widthTo = i + widthOffset;
        var heightTo = i1 + heightOffset - 5;
        return mouseX > widthFrom && mouseX < widthTo && mouseY > heightFrom + 35 && mouseY < heightTo - (showInventory ?  114 : 5);
    }

    private void windowMoveVertical(double dragY) {
        int size = getComponents(ItemStack.EMPTY)
            .stream()
            .filter(component -> component.getString().contains("|"))
            .toList()
            .size();
        if (size > 6 || size > 2 && showInventory) {
            int b = 7 * size * size - 135 * size + 578;
            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, b + (!showInventory ? -20 : -120)));
            this.rebuildWidgets();
        } else this.yScroll = 0;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    private void renderWand(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int startX, int startY) {
        var i = 40;
        var i1 = -17;
        var shiftX1 = 75;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,-10);
        guiGraphics.enableScissor(startX + i + 70 - shiftX1, startY + i1 - 94, startX + i + 70 + 60, startY + i1 - 94 + 172);
        renderItem(guiGraphics, this.width - shiftX1 * 2, this.height - (showInventory ? 80 : 0), wandManager.getWandManagerEntity().inputItemHandler.getStackInSlot(0),  (float) (showInventory ? 70 : 140), mouseX, mouseY, 16);
        boxMaker(guiGraphics, startX + i + 70 - shiftX1, startY + i1 - 94, 30, 86, BORDER_COLOUR, this.getMinecraft().options.getBackgroundColor(0.4F));
        guiGraphics.disableScissor();
        boxMaker(guiGraphics, startX + i + 60, startY + i1 - 94 - 11, 20, (int) this.scale + 5, BORDER_COLOUR, this.getMinecraft().options.getBackgroundColor(0.2F));
        boxMaker(guiGraphics, startX + i + 104, startY + i1 - 94 - 11, 20, (int) this.scale + 5, BORDER_COLOUR, this.getMinecraft().options.getBackgroundColor(0.2F));
        wandProperties(guiGraphics, startX, i, startY, i1);
        guiGraphics.pose().popPose();
    }

    private void wandProperties(@NotNull GuiGraphics guiGraphics, int startX, int i, int startY, int i1) {
        var shiftY = 0;
        var shiftX = -5;
        var maxWidth = 0;
        var spacer = new AtomicInteger();

        if(!this.setView){
            for (Component components : WandItemHelper.getItemModifiers(this.wandManager.getWandManagerEntity().getWandSlot())) {
                if (!components.getString().contains("Selected")) {
                    guiGraphics.drawString(this.font, components, this.width / 2 - 30 + shiftX, this.height / 2 - 80 + spacer.get() + shiftY, 0);
                    spacer.set(spacer.get() + 12);
                    if (maxWidth < font.width(components)) maxWidth = font.width(components);
                }
            }
        } else {
            var wand = this.wandManager.getWandManagerEntity().getWandSlot();
            var getRunes = WandData.wandData(wand);
            var index = new AtomicInteger();
            for (ItemStack ignored : getRunes.upgradeSlots()) {
                var size = 32;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0,0,100);
                var posX = startX + wandManager.offSetX + spacer.get() - (index.get() > 3 ? 148 : 0) + (this.setView ? + 94 : 0);
                var posY = startY + wandManager.offSetY - 112 + (index.get() > 3 ? 37 : 0) + (this.setView ? - 96 : 0);
                guiGraphics.blit(GUI_GENERAL_SLOT, posX, posY, 0,0, size, size, size, size);
                spacer.set(spacer.get() + wandManager.runeYSpacer);
                index.set(index.get()+1);
                guiGraphics.pose().popPose();
            }
        }

        boxMaker(guiGraphics, startX + i + 62 + shiftX, startY + i1 - 94 + shiftY, Math.max(maxWidth/2 + 10, 74), (int) this.scale + 85, BORDER_COLOUR, this.getMinecraft().options.getBackgroundColor(0.2F));
    }


    private void coreSlots(@NotNull GuiGraphics guiGraphics, int adjustX, int adjustY) {
        var spacer = new AtomicInteger();
        for(ResourceLocation location : getOverlays()){
            guiGraphics.blit(GUI_ITEM_SLOT, this.width/2 - 156 + adjustX, (this.height/2) + spacer.get() - 23 + adjustY, 0,0,32,32,32,32);
            guiGraphics.blit(location, this.width/2 - 156 + adjustX, (this.height/2) + spacer.get() - 23 + adjustY, 0,0,32,32,32,32);
            spacer.set(spacer.get() + 30);
        }
    }

    private void overlayInventory(@NotNull GuiGraphics guiGraphics, int startX, int startY) {
        guiGraphics.pose().popPose();
        var i = 40;
        var i1 = -17;
        guiGraphics.pose().translate(0,0,20);
        if(showInventory){
            boxMaker(guiGraphics, startX + i, startY + i1, 100, 55, BORDER_COLOUR);
            renderInventoryBackground(guiGraphics, this, 256, 24, this.showInventory);
        }
        guiGraphics.pose().pushPose();
    }

    private List<ResourceLocation> getOverlays(){
        return List.of(CORE, ADVANCED_AUGMENT_CORE, AUGMENT_HYPER_CORE);
    }



    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);

        var adjustX = 18;
        var adjustY = -27;
        var startX = this.width / 2 - 140;
        var startY = this.height / 2 + 22;

        boxMaker(guiGraphics, startX - 18 + adjustX, startY - 47 + adjustY, 18, 48, BORDER_COLOUR);
        coreSlots(guiGraphics, adjustX, adjustY);
        renderWand(guiGraphics, mouseX, mouseY, startX, startY);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        overlayInventory(guiGraphics, startX, startY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
