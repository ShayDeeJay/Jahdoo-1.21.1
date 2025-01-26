package org.jahdoo.client.gui.block.rune_table;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.block.rune_table.RuneTableEntity;
import org.jahdoo.client.SharedUI;
import org.jahdoo.client.gui.block.RuneSlot;
import org.jahdoo.client.gui.block.augment_modification_station.InventorySlots;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.runes.rune_data.RuneHolder;
import org.jahdoo.items.wand.WandData;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.items.wand.WandItemHelper;
import org.jahdoo.registers.ElementRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.client.IconLocations.GUI_BUTTON;
import static org.jahdoo.client.IconLocations.GUI_GENERAL_SLOT;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.utils.ModHelpers.filterList;
import static org.jahdoo.utils.ModHelpers.withStyleComponent;

public class RuneTableScreen extends AbstractContainerScreen<RuneTableMenu> {
    public static WidgetSprites WIDGET = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final RuneTableMenu runeTableMenu;
    boolean showInventory = true;
    boolean setView = true;
    int scaleItem = 60;
    AbstractElement element;
    int borderColour;
    boolean isHovering;

    public RuneTableScreen(RuneTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        var element = ElementRegistry.getElementByWandType(pMenu.getRuneTableEntity().inputItemHandler.getStackInSlot(0).getItem());
        var color = -1;
        this.runeTableMenu = pMenu;
        this.switchVisibility();
        this.element = ElementRegistry.MYSTIC.get();
        this.borderColour = element.isEmpty() ? BORDER_COLOUR : color;
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public RuneTableEntity entity(){
        return this.runeTableMenu.getRuneTableEntity();
    }

    private void switchVisibility() {
        for (Slot slot : this.menu.slots) {
            if(slot instanceof InventorySlots inventorySlots){
                inventorySlots.setActive(showInventory);
            }
            if(slot instanceof RuneSlot runeSlot){
                if(runeSlot.getItem().getItem() instanceof RuneItem){
                    runeSlot.setActive(showInventory);
                }
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    private void wandProperties(@NotNull GuiGraphics guiGraphics, int startX, int i, int startY, int i1) {
        var shiftY = 0;
        var shiftX = -5;
        var spacer = new AtomicInteger();

        remainingPotential(guiGraphics, shiftX, spacer, shiftY);
        var getRunes = RuneHolder.getRuneholder(getWand());
        handleSlotsInGridLayout(
                (slotX, slotY, index) -> {
                    for (ItemStack ignored : getRunes.runeSlots()) {
                        var size = 32;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0,0,100);
                        var x = slotX + runeTableMenu.posX - 96;
                        var y = slotY - runeTableMenu.posY - 9;
                        guiGraphics.blit(GUI_GENERAL_SLOT, x, y, 0,0, size, size, size, size);
                        spacer.set(spacer.get() + runeTableMenu.runeYSpacer);
                        guiGraphics.pose().popPose();
                    }
                },
                getRunes.runeSlots().size(),
                this.width,
                this.height,
                runeTableMenu.offSetX,
                runeTableMenu.offSetY
        );

        var startX11 = this.width/2 + shiftX - 38;
        var startY11 = this.height/2 + shiftY - 89;
        var heightOffset = 86 - (showInventory ? 40 : 0);
        boxMaker(guiGraphics, startX11, startY11, Math.max(10, 74), heightOffset, borderColour, groupFade());

        if(getRunes.runeSlots().isEmpty()){
            guiGraphics.drawCenteredString(this.font, "No Slots Available", startX11 + 74, startY11 + 40, SUB_HEADER_COLOUR);
        }

        var startX1 = this.width / 2 - 38 + shiftX;
        var startY1 = this.height / 2 - 111 + shiftY;
        boxMaker(guiGraphics, startX1, startY1, 39, 10, borderColour, groupFade());

        var header = Component.literal((this.setView ? "Rune" : "Wand") + " Manager");
        var x = this.width / 2 - 34 + shiftX;
        var y1 = this.height / 2 - 105 + shiftY;
        guiGraphics.drawString(this.font, header, x, y1, SUB_HEADER_COLOUR);
    }

    private void renderItem(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int startX, int startY) {
        var i = 40;
        var i1 = -17;
        var shiftX1 = 75;
        var minX = startX + i + 70 - shiftX1;
        var minY = startY + i1 - 94;
        var maxX = startX + i + 55 ;
        var width1 = this.width - shiftX1 * 2;
        var height1 = this.height - (144 - scaleItem);
        var posX = startX + 23;
        var posY = startY + i1 - 106;
        var offsetY = 164 - (showInventory ? 80 : 0);

        guiGraphics.pose().pushPose();
        bezelMaker(guiGraphics, posX, posY, 52, offsetY, 32, null);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,-10);
        guiGraphics.enableScissor(minX, minY, maxX, minY + (!setView ? 172 : 92));
        SharedUI.renderItem(guiGraphics, width1, height1, getWand(), scaleItem, mouseX, mouseY, 16);
        if(this.element != null){
            var colorFade = FastColor.ARGB32.color(100, borderColour);
            var heightOffset = 86 - (showInventory ? 40 : 0);
            var color = FastColor.ARGB32.color(50, borderColour);
            var colorA = FastColor.ARGB32.color(20, borderColour);
            boxMaker(guiGraphics, minX, minY, 30, heightOffset, getFadedColourBackground(0.4f));
            SharedUI.boxMaker(guiGraphics, minX, minY, 30, heightOffset, color, colorA, colorFade);
        }
        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
    }

    public ItemStack getWand(){
        return this.runeTableMenu.getRuneTableEntity().inputItemHandler.getStackInSlot(0);
    }


    private void remainingPotential(@NotNull GuiGraphics guiGraphics, int shiftX, AtomicInteger spacer, int shiftY) {
        if (getWand().getItem() instanceof WandItem) {
            var itemModifiers = WandItemHelper.getItemModifiers(getWand());
            var potentialList = filterList(itemModifiers, "Potential");
            var sharedX = this.width / 2 - 30 + shiftX;
            for (Component component : potentialList) {
                var posY = this.height / 2 - 85 + spacer.get() + shiftY;
                guiGraphics.drawString(this.font, component, sharedX -1, posY + 2, 0);
            }
        }
    }

    private static int groupFade() {
        return getFadedColourBackground(0.7f);
    }

    private void hoverCarried(GuiGraphics guiGraphics, int x, int y){
        var carried = this.hoveredSlot == null || hoveredSlot.getItem().isEmpty() ? runeTableMenu.getCarried() : hoveredSlot.getItem();
        if(carried.getItem() instanceof RuneItem){
            var getTooltip = this.getTooltipFromContainerItem(carried);
            if(!(hoveredSlot instanceof RuneSlot) || !runeTableMenu.getCarried().isEmpty()){
                getTooltip.add(Component.empty());
                var carriedRuneCost = String.valueOf(RuneData.RuneHelpers.getCostFromRune(carried));
                var carriedCostComponent = withStyleComponent(carriedRuneCost, -1);
                var potentialCostPreFix = withStyleComponent("Potential Cost: ", HEADER_COLOUR);
                getTooltip.add(potentialCostPreFix.copy().append(carriedCostComponent));
            }
            if (!carried.isEmpty()) {
                guiGraphics.renderTooltip(font, getTooltip, Optional.empty(), x, y);
            }
        }
    }

    private void overlayInventory(@NotNull GuiGraphics guiGraphics, int startX, int startY) {
        guiGraphics.pose().popPose();
        var i = 40;
        var i1 = -17;
        guiGraphics.pose().translate(0,0,20);
        if(showInventory){
            var startX1 = startX + i - 5;
            boxMaker(guiGraphics, startX1, startY + i1, 105, 55, borderColour, groupFade());
            renderInventoryBackground(guiGraphics, this, 256, 24, this.showInventory);
        }
        guiGraphics.pose().pushPose();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        var adjustX = 18;
        var adjustY = -27;
        var i = this.width / 2;
        var i1 = this.height / 2;
        var startX = i - 140;
        var startY = i1 + 22;

        scaleItem();
        augmentCoreSlots(guiGraphics, adjustX, adjustY, borderColour, this.width, this.height, groupFade());
        renderItem(guiGraphics, mouseX, mouseY, startX, startY);
        wandProperties(guiGraphics, startX, i, startY, i1);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        var hSlot = this.hoveredSlot;
        if(hSlot != null && !(hSlot.getItem().getItem() instanceof RuneItem)){
            this.renderTooltip(guiGraphics, mouseX, mouseY);
        }
        overlayInventory(guiGraphics, startX, startY);
        experienceCost(guiGraphics, mouseX, mouseY, i, startY);

        hoverCarried(guiGraphics, mouseX, mouseY);
        this.isHovering = false;
    }

    private void experienceCost(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int i, int startY) {
        var player = this.getMinecraft().player;
        if(player == null) return;
        var exp = player.experienceLevel;
        var getMaxCost = getExperienceCost();
        var expColour = exp >= getMaxCost ? 8453920 : -2070938;
        var refinementPotential = Component.literal(String.valueOf(getMaxCost));
        var expLvl = Component.literal(String.valueOf(exp));
        var offsetX = 0;
        var offsetY = -27;
        var potential = getPotential();
        if(!showInventory && isHovering && potential > 0){
            SharedUI.boxMaker(guiGraphics, mouseX - 26 + offsetX, mouseY + offsetY, 26, 13, BORDER_COLOUR, getFadedColourBackground(0.6f));
            SharedUI.drawStringWithBackground(guiGraphics, this.font, refinementPotential, mouseX + offsetX, mouseY + 15 + offsetY, 0, expColour, true);
            guiGraphics.drawCenteredString(font, "Exp Cost", mouseX + offsetX, mouseY + 4 + offsetY, -1);
            SharedUI.drawStringWithBackground(guiGraphics, this.font, expLvl, i, startY + 69, 0, 8453920, true);
            renderExperienceBar(guiGraphics, i - 91, startY + 78, this.getMinecraft());
        }
    }

    private boolean canModify(){
        var player = this.getMinecraft().player;
        if(player == null) return false;
        var exp = player.experienceLevel;
        var potential = getPotential();
        var getMaxCost = getExperienceCost();
        return exp >= getMaxCost && potential > 0  ;
    }

    private int getPotential() {
        return WandData.wandData(getWand()).refinementPotential();
    }

    private int getExperienceCost() {
        var potential = getPotential();
        return Math.max(10, 100 - potential);
    }

    private void scaleItem() {
        var scale = 8;
        this.scaleItem = !setView ? Math.min(140, scaleItem + scale) : Math.max(62, scaleItem - scale);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
