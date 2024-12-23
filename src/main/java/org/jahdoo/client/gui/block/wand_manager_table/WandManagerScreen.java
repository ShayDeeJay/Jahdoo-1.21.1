package org.jahdoo.client.gui.block.wand_manager_table;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.block.wand_block_manager.WandManagerTableEntity;
import org.jahdoo.client.IconLocations;
import org.jahdoo.client.SharedUI;
import org.jahdoo.client.gui.block.augment_modification_station.AugmentCoreSlot;
import org.jahdoo.client.gui.block.augment_modification_station.InventorySlots;
import org.jahdoo.components.WandData;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.items.wand.WandItemHelper;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.client.IconLocations.*;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.gui.ToggleComponent.*;
import static org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationData.extractName;
import static org.jahdoo.items.augments.AugmentItemHelper.getModifierContextSingle;
import static org.jahdoo.items.augments.AugmentRatingSystem.calculateRatingNext;
import static org.jahdoo.utils.ModHelpers.doubleFormattedDouble;
import static org.jahdoo.utils.ModHelpers.withStyleComponent;

public class WandManagerScreen extends AbstractContainerScreen<WandManagerMenu> {
    public static WidgetSprites WIDGET = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final WandManagerMenu wandManager;
    private double yScroll;
    public Inventory inventory;
    boolean showInventory;
    boolean setView;
    int section = 140;
    double scale = 1;
    private final float FADE = 0.7f;
    AbstractElement element;
    int borderColour;
    int fadeSwap;
    private final int fadeSwapDirection = 1;

    public WandManagerScreen(WandManagerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.wandManager = pMenu;
        this.inventory = pPlayerInventory;
        this.switchVisibility();
        var element = ElementRegistry.getElementByWandType(pMenu.getWandManagerEntity().getWandSlot().getItem());
        this.element = element.getFirst();
        this.borderColour = element.isEmpty() ? BORDER_COLOUR : FastColor.ARGB32.color(100, element.getFirst().textColourPrimary());
    }

    @Override
    protected void init() {
        super.init();
        this.keyboardButton();
        this.upgradeButton();
    }

    private void upgradeButton() {
        var posX = this.width/2 - 40;
        var posY = this.height/2 + 48;

        this.addRenderableWidget(
            menuButtonSound(
                posX, posY,
                (press) -> {
                    /*Press Action*/
                    this.rebuildWidgets();
                },
                REFRESH, 32, false, 0, WIDGET, true,
                () -> {
                    this.fadeSwap = 1;
                    /*Hover Action*/
                }
            )
        );
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
        this.addRenderableWidget(menuButton(this.width/2 - 136, this.height/2 + 3, (press) -> inventoryHandler(), resourceLocation, 24, false, 0, WIDGET, true));
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
        bezelMaker(guiGraphics,startX + 23, startY + i1 - 106, 52, 164 - (showInventory ? 80 : 0), 32, this.element);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,-10);
        guiGraphics.enableScissor(startX + i + 70 - shiftX1, startY + i1 - 94, startX + i + 70 + 60, startY + i1 - 94 + (!setView ? 172 : 92));
        renderItem(guiGraphics, this.width - shiftX1 * 2, this.height - (144 - section), wandManager.getWandManagerEntity().inputItemHandler.getStackInSlot(0), section, mouseX, mouseY, 16);
        if(this.element != null){
            var colorA = FastColor.ARGB32.color(0, borderColour);
            var colorFade = FastColor.ARGB32.color(100, borderColour);
            boxMaker(guiGraphics, startX + i + 70 - shiftX1, startY + i1 - 94, 30, 86 - (showInventory ? 40 : 0), this.getMinecraft().options.getBackgroundColor(0.4f));
            boxMakerTest(guiGraphics, startX + i + 70 - shiftX1, startY + i1 - 94, 30, 86 - (showInventory ? 40 : 0), FastColor.ARGB32.color(50, borderColour), colorA, colorFade);
        }
        guiGraphics.disableScissor();
        wandProperties(guiGraphics, startX, i, startY, i1);
        guiGraphics.pose().popPose();

    }

    private void wandProperties(@NotNull GuiGraphics guiGraphics, int startX, int i, int startY, int i1) {
        var shiftY = 0;
        var shiftX = -5;
        var maxWidth = 0;
        var spacer = new AtomicInteger();

        if(!this.setView){
            baseWandProperties(guiGraphics, shiftX, spacer, shiftY, maxWidth, startX, startY);
        } else {
            var wand = this.wandManager.getWandManagerEntity().getWandSlot();
            var getRunes = WandData.wandData(wand);
            SharedUI.handleSlotsInGridLayout(
                (slotX, slotY, index) -> {
                    for (ItemStack ignored : getRunes.runeSlots()) {
                        var size = 32;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0,0,100);
                        guiGraphics.blit(GUI_GENERAL_SLOT, slotX + wandManager.posX - 96, slotY -  wandManager.posY - 9, 0,0, size, size, size, size);
                        spacer.set(spacer.get() + wandManager.runeYSpacer);
                        guiGraphics.pose().popPose();
                    }
                },
                getRunes.runeSlots().size(),
                this.width,
                this.height,
                wandManager.offSetX,
                wandManager.offSetY
            );
            boxMaker(guiGraphics, startX + i + 62 + shiftX, startY + i1 - 94 + shiftY, Math.max(maxWidth/2 + 10, 74), (int) this.scale + 85 - (showInventory ? 40 : 0), borderColour, this.getMinecraft().options.getBackgroundColor(FADE));
        }

        boxMaker(guiGraphics, this.width / 2 - 38 + shiftX, this.height / 2 - 111 + shiftY, 39, 10, borderColour, this.getMinecraft().options.getBackgroundColor(FADE));
        guiGraphics.drawString(this.font, Component.literal(this.setView ? "Rune Manager" : "Wand Manager"), this.width / 2 - 34 + shiftX, this.height / 2 - 105 + shiftY, ColourStore.SUB_HEADER_COLOUR);
    }

    private int baseWandProperties(@NotNull GuiGraphics guiGraphics, int shiftX, AtomicInteger spacer, int shiftY, int maxWidth, int startX, int startY) {
        if(this.wandManager.getWandManagerEntity().getWandSlot().getItem() instanceof WandItem){
            var itemModifiers = WandItemHelper.getItemModifiers(this.wandManager.getWandManagerEntity().getWandSlot());
            var rarityAndSlots = itemModifiers.stream().filter(component -> component.getString().contains("Rarity") || component.getString().contains("Slots")).toList();
            var modifiersAndHeader = itemModifiers.stream().filter(component -> component.getString().contains("%") || component.getString().contains("Applies")).toList();
            var widthHeader = 0;
            var widthProperties = 0;

            for (Component components : rarityAndSlots) {
                guiGraphics.drawString(this.font, components, this.width / 2 - 30 + shiftX, this.height / 2 - 80 + spacer.get() + shiftY, 0);
                spacer.set(spacer.get() + 12);
                if (widthHeader < font.width(components)) widthHeader = font.width(components);
            }
            boxMaker(guiGraphics, startX + 102 + shiftX, startY - 111 + shiftY, widthHeader/2 + 8, 20, borderColour, this.getMinecraft().options.getBackgroundColor(FADE));

            for (Component components : modifiersAndHeader) {
                guiGraphics.drawString(this.font, components, this.width / 2 - 30 + shiftX, this.height / 2 - 64 + spacer.get() + shiftY, 0);
                var skip = components.getString().contains("Applies");
                if(!skip){
                    guiGraphics.drawString(this.font, getModifierRange(wandManager, components.getString()), this.width / 2 - 28 + shiftX, this.height / 2 - 53 + spacer.get() + shiftY, ColourStore.HEADER_COLOUR);
                }
                spacer.set(spacer.get() + (!skip ? 24 : 13));
                if (widthProperties < font.width(components)) widthProperties = font.width(components);
            }
            boxMaker(guiGraphics, startX + 102 + shiftX, startY - 69 + shiftY, widthProperties / 2 + 8, 65, borderColour, this.getMinecraft().options.getBackgroundColor(FADE));
        }
        return maxWidth;
    }

    public static Component getModifierRange(WandManagerMenu wandManagerMenu, String type){
        var rarity = WandData.wandData(wandManagerMenu.getWandManagerEntity().getWandSlot()).rarityId();
        var attributes = JahdooRarity.getAllRarities().get(rarity).getAttributes();
        var first = type.contains("Cooldown") ? attributes.getCooldownRange() : type.contains("Mana") ? attributes.getManaReductionRange() : attributes.getDamageRange();
        return ModHelpers.withStyleComponent("(" + first.getFirst() +"%"+ " - " + first.getSecond()+"%" + ")", BORDER_COLOUR);
    }

    private void coreSlots(@NotNull GuiGraphics guiGraphics, int adjustX, int adjustY) {
        var spacer = new AtomicInteger();
        var posX = this.width / 2;
        var posY = this.height / 2;
        boxMaker(guiGraphics, posX + adjustX - 159, posY - 62 + adjustY, 17, 46, borderColour, this.getMinecraft().options.getBackgroundColor(FADE));
        for(ResourceLocation location : getOverlays()){
            var offsetX = 158;
            var offsetY = 60;
            guiGraphics.blit(GUI_ITEM_SLOT, posX - offsetX + adjustX, posY + spacer.get() - offsetY + adjustY, 0,0,32,32,32,32);
            guiGraphics.blit(location, posX - offsetX + adjustX, posY + spacer.get() - offsetY + adjustY, 0,0,32,32,32,32);
            spacer.set(spacer.get() + 28);
        }
    }

    private void overlayInventory(@NotNull GuiGraphics guiGraphics, int startX, int startY) {
        guiGraphics.pose().popPose();
        var i = 40;
        var i1 = -17;
        guiGraphics.pose().translate(0,0,20);
        if(showInventory){
            boxMaker(guiGraphics, startX + i - 5, startY + i1, 105, 55, borderColour, this.getMinecraft().options.getBackgroundColor(FADE ));
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
//        this.renderMenuBackground(guiGraphics);
//        if (this.fadeSwap >= 350) {
//            this.fadeSwapDirection = -1; // Start decrementing
//        } else if (this.fadeSwap <= 55) {
//            this.fadeSwapDirection = 1; // Start incrementing
//        }
//
//        this.fadeSwap += this.fadeSwapDirection;

        var scale = 8;
        if(!this.setView) {
            this.section = Math.min(140, section + scale);
        } else {
            this.section = Math.max(62, section - scale);
        }


        var adjustX = 18;
        var adjustY = -27;
        var startX = this.width / 2 - 140;
        var startY = this.height / 2 + 22;

        SharedUI.boxMaker(guiGraphics, startX + 105, startY + 31, 35, 11, BORDER_COLOUR, SharedUI.getFadedColourBackground(0.9f));
        guiGraphics.drawCenteredString(this.font,  ModHelpers.withStyleComponent("Re-Roll", 0xb97700), startX + 150, startY + 38, 0);

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
