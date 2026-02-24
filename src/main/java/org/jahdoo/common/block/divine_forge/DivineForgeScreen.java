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
import org.jahdoo.common.client.overlay.WalletOverlay;
import org.jahdoo.common.client.screens.AbstractPanableScreen;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.block.divine_forge.DivineForgeEntity.MODIFICATION_SLOTS;
import static org.jahdoo.common.block.divine_forge.helpers.DivineForgeScreenShared.*;
import static org.jahdoo.common.block.divine_forge.helpers.ModifierTab.onModifyClick;
import static org.jahdoo.common.block.divine_forge.helpers.RepairTab.*;
import static org.jahdoo.common.block.divine_forge.helpers.RuneTab.runeSlotTexture;
import static org.jahdoo.common.block.divine_forge.helpers.RuneTab.runeToolTip;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.augmentCoreSlots;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.common.client.button.ToggleComponent.menuButton;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.client.screens.AbilityModificationScreen.WIDGET;

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
        var posX = this.width / 2 + 66;
        var i = this.height / 2;
        var posY = i - 112;
        var posY2 = i - 112;

        toggleRuneManager(posX, posY);
        toggleRepairManager(posX, posY);
        toggleModifierManager(posX, posY);
        toggleFullItemToolTip(posX, posY2, selected);
        repairItem(selected);
        modifyItem(selected);
    }

    private void repairItem(WidgetSprites selected) {
        if(isRepairManager() && !this.getItem().isEmpty()){
            var canRepairItem = repairable(getItem());
            var posX1 = this.width / 2 + 9;
            var posY1 = this.height / 2 - 43;

            this.addRenderableWidget(
                menuButtonSound(
                    posX1,
                    posY1,
                    (press) -> {
                        onRepair(menu);
                        this.rebuildWidgets();
                    },
                    REPAIR,
                    44,
                    canRepairItem,
                    0,
                    hasRepairSlots(getItem()) ? WIDGET : selected,
                    !canRepairItem,
                    () -> this.coinCost = onHoverRepair(this.hoverTooltip, borderColour, getItem()),
                    forUI(SoundReg.UPGRADE_MODIFIER.get(), 0F)
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

    public DivineForgeMenu getRuneMenu() {
        return runeTableMenu;
    }

    private void modifyItem(WidgetSprites selected) {
        var spacer = 0;
        var spaceBy = 32;

        for (int i = 0; i < 4; i++){
            if(isModifierManager() && !this.getItem().isEmpty()){
                var posX1 = this.width / 2 - 38 + spacer;
                var posY1 = this.height / 2 - 38;
                var instance = forUI(SoundReg.UPGRADE_MODIFIER.get(), 0F, 0F);
                var buttonType = hasRepairSlots(getItem()) ? WIDGET : selected;

                var b = entity().getInputItemHandler().getStackInSlot(i + MODIFICATION_SLOTS).isEmpty();
                int finalI = i;
                this.addRenderableWidget(
                    menuButtonSound(
                        posX1 + 12, posY1 + 4,
                        (press) -> {
                            onModifyClick(finalI, getItem(), this);
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
                            var size = 32;
                            guiGraphics.blit(GUI_ITEM_SLOT, posX1 + 6, posY1 - 22, 0 , 0, size, size, size, size);
                        }
                    }
                );

                spacer += spaceBy;
            }
        }

    }

    private void toggleFullItemToolTip(int posX, int posY2, WidgetSprites selected) {
        this.addRenderableWidget(
            menuButton(posX + 19, posY2, (press) -> hideTooltip(), INFORMATION, 24, false, 0, this.showTooltip ? selected : WIDGET, false)
        );
    }

    private void toggleRepairManager(int posX, int posY) {
        newTab(posX, posY, REPAIR_MANAGE, REPAIR, isRepairManager());
    }

    private void toggleRuneManager(int posX, int posY) {
        newTab(posX - 19, posY, RUNE_MANAGE, RUNE, isRuneManager());
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
    
    public boolean isRepairManager() {
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

        displayWallet(guiGraphics);
        this.coinCost = 0;

        tooltipSlots(hoverTooltip, hoveredSlot, menu.getCarried());
        augmentCoreSlots(guiGraphics, adjustX, adjustY+1, borderColour, this.width + 6, this.height + 6, groupFade());
        togglePrimaryToolTip(guiGraphics, mouseX - 5, mouseY + 20);
        overlayInventory(guiGraphics, startX, startY, this, borderColour);
        sharedGearData(this, guiGraphics, getItem(), i, i1);
        header(guiGraphics);
        DivineForgeScreenShared.renderItem(guiGraphics, mouseX, mouseY, startX, startY, width, height, scaleItem, getMinecraft(), getItem(), this.leftPos, this.topPos, borderColour);
        runeSlotTexture(guiGraphics, width, height, isRuneManager(), getMinecraft(), borderColour, runeTableMenu);

        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        runeTooltip(guiGraphics, mouseX, mouseY);
        costToolTip(guiGraphics, mouseX, mouseY);
        hoverCarried(guiGraphics, mouseX, mouseY, hoveredSlot, runeTableMenu, getMinecraft());
        this.hoverTooltip = new ArrayList<>();
    }

    private void runeTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var coinCost1 = runeToolTip(guiGraphics, mouseX, mouseY, hoveredSlot, borderColour, entity(), getMinecraft());
        if(coinCost1 > 0) this.coinCost = coinCost1;
    }

    private void displayWallet(@NotNull GuiGraphics guiGraphics) {
        var converter = coinCost > 0 ? PlayerWallet.CurrencyConverter.convertToCoins(coinCost) : null;
        var adjustXA= guiGraphics.guiWidth() / 2 - 128;
        var adjustYA = guiGraphics.guiHeight() / 2 + 3;
        WalletOverlay.renderWallet(guiGraphics, getMinecraft(), 1, adjustXA, adjustYA, false, false, false, converter);
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

    private void header(@NotNull GuiGraphics guiGraphics) {
        var header = Component.literal(isRuneManager() ? "Rune Manager" : "Gear Repair");
        var x = width / 2 ;
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
