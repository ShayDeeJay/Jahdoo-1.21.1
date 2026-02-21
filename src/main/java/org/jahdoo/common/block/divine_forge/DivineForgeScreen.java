package org.jahdoo.common.block.divine_forge;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.divine_forge.helpers.DivineForgeScreenShared;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.screens.AbstractPanableScreen;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.block.divine_forge.DivineForgeEntity.MODIFICATION_SLOT;
import static org.jahdoo.common.block.divine_forge.helpers.DivineForgeManager.*;
import static org.jahdoo.common.block.divine_forge.helpers.DivineForgeScreenHelpers.*;
import static org.jahdoo.common.block.divine_forge.helpers.DivineForgeScreenShared.overlayInventory;
import static org.jahdoo.common.block.divine_forge.helpers.DivineForgeScreenShared.tooltipSlots;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.augmentCoreSlots;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.common.client.button.ToggleComponent.menuButton;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.client.screens.AbilityModificationScreen.WIDGET;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.*;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.canRepair;

public class DivineForgeScreen extends AbstractContainerScreen<DivineForgeMenu> {

    private static final int RUNE_MANAGE = 0;
    private static final int REPAIR_MANAGE = 1;
    private static final int MODIFIER_MANAGE = 2;
    private List<Component> hoverTooltip = new ArrayList<>();
    private final DivineForgeMenu runeTableMenu;
    private final int borderColour;
    private boolean showTooltip;
    private int scaleItem = 60;
    private int selection;
    private int coinCost;

    public DivineForgeScreen(DivineForgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        var element = ElementReg.fromWand(menu.tableEntity().itemSlot().getItem());

        this.selection = REPAIR_MANAGE;
        this.menu.switchRuneVisibility(false);
        this.menu.switchModifierVisibility(false);
        this.runeTableMenu = menu;
        this.borderColour = color(200, element.map(AbstractElement::textColourA).orElseGet(AbstractPanableScreen::uiColour));
    }

    @Override
    protected void init() {
        super.init();
        var selected = new WidgetSprites(GUI_BUTTON_SELECTED, GUI_BUTTON_SELECTED);
        var posX = this.width/2 + 66;
        var posY = this.height / 2 - 112;
        var posY2 = this.height / 2 - 112;

        toggleRuneManager(posX, posY);
        toggleRepairManager(posX, posY);
        toggleModifierManager(posX, posY);
        toggleFullItemToolTip(posX, posY2, selected);
        repairItem(selected);
        modifyItem(selected);
    }

    private void repairItem(WidgetSprites selected) {
        if(isRepairManager() && !this.getItem().isEmpty()){
            var canRepairItem = repairable(getItem(), entity());
            var posX1 = this.width / 2 + 9;
            var posY1 = this.height / 2 - 43;
            var instance = forUI(SoundReg.UPGRADE_MODIFIER.get(), 0F);
            var buttonType = canRepair(getItem()) ? WIDGET : selected;

            this.addRenderableWidget(
                menuButtonSound(
                    posX1, posY1,
                    (press) -> {
                        onRepair(getMinecraft(), menu);
                        this.rebuildWidgets();
                    }, REPAIR,
                    44, canRepairItem, 0, buttonType, !canRepairItem,
                    () -> onHoverRepair(this.hoverTooltip, borderColour, getItem()),
                    instance
                )
            );

            this.addRenderableOnly(
                new Overlay() {
                    @Override
                    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                        var color = color(200, canRepairItem ? ColourHelpers.getHeaderColour() : borderColour);
                        SharedUI.boxMaker(guiGraphics, posX1 + 6, posY1 + 6, 16, 16, color, 0, 0);
                    }
                }
            );
        }
    }

    private void modifyItem(WidgetSprites selected) {
        var spacer = 0;
        for (int i = 0; i < 4; i++){
            if(isModifierManager() && !this.getItem().isEmpty()){
                var canRepairItem = repairable(getItem(), entity());
                var posX1 = this.width / 2 - 38 + spacer;
                var posY1 = this.height / 2 - 38;
                var instance = forUI(SoundReg.UPGRADE_MODIFIER.get(), 0F);
                var buttonType = canRepair(getItem()) ? WIDGET : selected;

                var b = entity().getInputItemHandler().getStackInSlot(i + MODIFICATION_SLOT).isEmpty();
                int finalI = i;
                this.addRenderableWidget(
                    menuButtonSound(
                        posX1 + 12, posY1 + 4,
                        (press) -> {
                            onRepairSlot(getMinecraft(), menu);
                            this.rebuildWidgets();
                        },
                        COG, 20, b, 0, buttonType, !b,
                        () -> onHoverRepair(this.hoverTooltip, borderColour, getItem()),
                        instance
                    )
                );

                this.addRenderableOnly(
                    new Overlay() {
                        @Override
                        public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                            var color = color(200, canRepairItem ? ColourHelpers.getHeaderColour() : borderColour);
//                        SharedUI.boxMaker(guiGraphics, posX1 + 6, posY1 + 6, 16, 16, color, 0, 0);
                            guiGraphics.blit(GUI_ITEM_SLOT, posX1 + 6, posY1 - 22, 0,0,32,32,32,32);
                        }
                    }
                );
                spacer += 32;
            }
        }

    }

    private void toggleFullItemToolTip(int posX, int posY2, WidgetSprites selected) {
        this.addRenderableWidget(
            menuButton(
                posX + 19, posY2, (press) -> hideTooltip(), INFORMATION, 24, false, 0, this.showTooltip ? selected : WIDGET, false
            )
        );
    }

    private void toggleRepairManager(int posX, int posY) {
        newTab(posX, posY, REPAIR_MANAGE, REPAIR, isRepairManager());
    }

    private void toggleRuneManager(int posX, int posY) {
        newTab(posX - 19, posY, RUNE_MANAGE, BLANK_RUNE, isRuneManager());
    }

    private void toggleModifierManager(int posX, int posY) {
        newTab(posX + 19, posY, MODIFIER_MANAGE, COG, isModifierManager());
    }

    private void newTab(int posX, int posY, int tab, ResourceLocation icon, boolean selected){
        this.addRenderableWidget(
            menuButton(posX - 19, posY, (press) -> inventoryHandler(tab), icon, 24, selected, 0, WIDGET, false)
        );
    }

    private boolean isModifierManager() {
        return selection == MODIFIER_MANAGE;
    }
    
    private boolean isRepairManager() {
        return selection == REPAIR_MANAGE;
    }

    private boolean isRuneManager() {
        return selection == RUNE_MANAGE;
    }

    public void hideTooltip(){
        this.showTooltip = !this.showTooltip;
    }

    public void inventoryHandler(int selection){
        this.selection = selection;
        this.menu.switchRuneVisibility(isRuneManager());
        this.menu.switchModifierVisibility(isModifierManager());
        this.rebuildWidgets();
    }

    public DivineForgeEntity entity(){
        return this.runeTableMenu.tableEntity();
    }

    private void scaleItem() {
        this.scaleItem = Math.min(140, scaleItem + 8);
    }

    public ItemStack getItem(){
        return entity().getInputItemHandler().getStackInSlot(0);
    }

    public static int groupFade() {
        return fadeBlack(0.7f);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.rebuildWidgets();
        var start = fadeBlack(0.4F);
        SharedUI.boxMaker(guiGraphics, 0, 0, this.width, this.height, 0, start, start);
        this.renderBlurredBackground(pPartialTick);
        var adjustX = 18;
        var adjustY = -27;
        var i = this.width / 2;
        var i1 = this.height / 2;
        var startX = i - 140;
        var startY = i1 + 22;
        scaleItem();

        runeToolTip(guiGraphics, mouseX, mouseY, hoveredSlot, coinCost, borderColour, entity(), getMinecraft());
        tooltipSlots(hoverTooltip, hoveredSlot, menu.getCarried());
        augmentCoreSlots(guiGraphics, adjustX, adjustY+1, borderColour, this.width + 6, this.height + 6, groupFade());
        togglePrimaryToolTip(guiGraphics, mouseX - 5, mouseY + 20);
        runeSlotTexture(guiGraphics, width, height, isRuneManager(), getMinecraft(), borderColour, runeTableMenu);
        overlayInventory(guiGraphics, startX, startY, this, borderColour);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);

        hoverCarried(guiGraphics, mouseX, mouseY, hoveredSlot, runeTableMenu, getMinecraft());
        sharedGearData(guiGraphics, i, i1);
        costToolTip(guiGraphics, mouseX, mouseY);
        header(guiGraphics);
        DivineForgeScreenShared.renderItem(guiGraphics, mouseX, mouseY, startX, startY, width, height, scaleItem, getMinecraft(), getItem(), this.leftPos, this.topPos, borderColour);

        this.hoverTooltip = new ArrayList<>();
        this.coinCost = 0;
    }

    private void togglePrimaryToolTip(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if(!this.getItem().isEmpty() && this.showTooltip){
            var startX1 = this.width / 2 + 100;
            var startY1 = this.height / 2 - 92;
            guiGraphics.renderTooltip(font, getItem(), startX1, startY1);
        }
    }

    private void costToolTip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(!this.hoverTooltip.isEmpty()){
            guiGraphics.renderTooltip(font, this.hoverTooltip, Optional.empty(), mouseX, mouseY + 10);
        }
    }

    public void sharedGearData(@NotNull GuiGraphics guiGraphics, int i, int i1) {
        var gearDataX = i - 37;
        var gearDataY = i1 - 85;

        getPotentialComponent(getItem(), (s) -> guiGraphics.drawString(font, s, gearDataX, gearDataY + 2, 0));
        if(this.isRepairManager()){
            guiGraphics.drawString(font, appendDurability(getItem()), gearDataX, gearDataY + 14, -1);
            getRepairSlotsComponent(getItem(), (s) -> guiGraphics.drawString(font, s, gearDataX, gearDataY + 26, -1));
        }
    }

    private void header(@NotNull GuiGraphics guiGraphics) {
        var header = Component.literal(isRuneManager() ? "Rune Manager" : "Gear Repair");
        var x = width / 2 + 32;
        var y1 = height / 2 - 120;
        guiGraphics.drawString(font, header, x, y1, borderColour);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partial, int mouseX, int mouseY) {}

}
