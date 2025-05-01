package org.jahdoo.common.block.rune_table;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.screens.AbstractPanableScreen;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.items.caster_item.CasterItem;
import org.jahdoo.common.networking.client2server.ItemInBlockC2SP;
import org.jahdoo.common.networking.client2server.JahdooGearDataC2SP;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.util.FastColor.ARGB32.color;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.repairDurability;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.button.ToggleComponent.menuButton;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.client.screens.AbilityModificationScreen.WIDGET;
import static org.jahdoo.common.client.slots.RuneSlot.removeRuneCost;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.*;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.*;

public class RuneTableScreen extends AbstractContainerScreen<RuneTableMenu> {

    private final RuneTableMenu runeTableMenu;
    private final AbstractElement element;
    private final int borderColour;
    private int scaleItem = 60;
    private boolean showInventory;
    private boolean showTooltip;
    private List<Component> hoverTooltip = new ArrayList<>();

    public RuneTableScreen(RuneTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        var element = ElementReg.fromWand(menu.tableEntity().itemSlot().getItem());
        this.menu.switchVisibility(false);
        this.runeTableMenu = menu;
        this.element = element.orElse(ElementReg.mystic());
        this.borderColour = color(200, element.map(AbstractElement::textColourA).orElseGet(AbstractPanableScreen::uiColour));
    }

    @Override
    protected void init() {
        super.init();
        setButtons();
    }

    private void setButtons() {
        var selected = new WidgetSprites(GUI_BUTTON_SELECTED, GUI_BUTTON_SELECTED);
        var posX = this.width/2 + 66;
        var posY = this.height / 2 - 112;
        var posY2 = this.height / 2 - 112;

        this.addRenderableWidget(
            menuButton(
                posX - 19, posY, (press) -> inventoryHandler(true), BLANK_RUNE,
                24, showInventory, 0, WIDGET, false
            )
        );

        this.addRenderableWidget(
            menuButton(
                posX, posY, (press) -> inventoryHandler(false), REPAIR,
                24, !showInventory, 0, WIDGET, false
            )
        );

        this.addRenderableWidget(
            menuButton(
                posX + 19, posY2, (press) -> hideTooltip(), INFORMATION,
                24, false, 0, this.showTooltip ? selected : WIDGET, false
            )
        );

        if(!this.showInventory && !this.getItem().isEmpty()){
            var canUpgrade = isCanUpgrade();
            var posX1 = this.width / 2 + 9;
            var posY1 = this.height / 2 - 43;
            this.addRenderableWidget(
                menuButtonSound(
                    posX1, posY1, (press) -> onRepair(), REPAIR,
                    44, canUpgrade, 0, canUpgrade(getItem()) ? WIDGET : selected, !canUpgrade, this::onHoverRepair,  forUI(SoundReg.UPGRADE_MODIFIER.get(), 0F)
                )
            );

            this.addRenderableOnly(
                new Overlay() {
                    @Override
                    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                        SharedUI.boxMaker(guiGraphics, posX1 + 6, posY1 + 6, 16, 16, color(200, canUpgrade ?  HEADER_COLOUR : borderColour), 0, 0);
                    }
                }
            );
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partial, int mouseX, int mouseY) {}

    public void hideTooltip(){
        this.showTooltip = !this.showTooltip;
    }

    private boolean isCanUpgrade() {
        var canUpgrade = canUpgrade(getItem());
        return !canUpgrade || !(getItemPotential(getItem()) >= repairPotentialCost()) || !(entity().checkAndChargeCores(coreCost().getItem(), false));
    }

    public void onHoverRepair(){
        if(this.hoverTooltip.isEmpty()){
            if(!coreCost().isEmpty()){
                var subHeaderColour = SUB_HEADER_COLOUR;
                var prefix = withStyleComponent("Potential: ", subHeaderColour);
                var colour = this.borderColour;
                var value = withStyleComponent("" + repairPotentialCost(), colour);

                this.hoverTooltip.add(withStyleComponent("Cost:", colour));
                this.hoverTooltip.add(prefix.copy().append(value));

                var hoverName = withStyleComponent(coreCost().getHoverName().getString() + ":", subHeaderColour);
                this.hoverTooltip.add(hoverName.copy().append(withStyleComponent(" 1", colour)));
            }
        }
    }

    private ItemStack coreCost() {
        var coreType = getRepairCoreCost();
        return coreType < 0 ? ItemStack.EMPTY : new ItemStack(getCore().get(coreType));
    }

    private int getRepairCoreCost() {
        return Math.min(totalRepairs(getItem()), 2);
    }

    private int getRuneRemovalCost() {
        return Math.min(totalRepairs(getItem()), 2);
    }

    public void onRepair(){
        getMinecraft().player.playSound(SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 0.6F);
        getMinecraft().player.playSound(SoundReg.REJECT.get(), 1, 1.8F);
        var handler = entity().inputItemHandler.getStackInSlot(getRepairCoreCost()+1);

        sendToServer(new ItemInBlockC2SP(handler.copyWithCount(handler.getCount()-1), entity().getBlockPos(), getRepairCoreCost()+1));
        repairDurability(getItem());
        updateRefinementPotential(getItem(), getItemPotential(getItem()) - repairPotentialCost());
        checkAndRepair(getItem());
        sendToServer(new JahdooGearDataC2SP(getRuneholder(getItem()), entity().getBlockPos(), 0));
        this.rebuildWidgets();
    }

    private int repairPotentialCost() {
        return 5 * (totalRepairs(getItem()) + 1);
    }

    public void inventoryHandler(boolean showInventory){
        this.showInventory = showInventory;
        this.menu.switchVisibility(showInventory);
        switchVisibility();
        this.rebuildWidgets();
    }

    private void switchVisibility() {
        this.menu.switchVisibility(showInventory);
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public RuneTableEntity entity(){
        return this.runeTableMenu.tableEntity();
    }

    private void scaleItem() {
        this.scaleItem = Math.min(140, scaleItem + 8);
    }

    public ItemStack getItem(){
        return entity().inputItemHandler.getStackInSlot(0);
    }

    private static int groupFade() {
        return fadeBlack(0.7f);
    }

    private int getExperienceCost() {
        var potential = getItemPotential(getItem());
        return Math.max(10, 100 - potential);
    }

    private void remainingPotential(GuiGraphics guiGraphics, int shiftX, AtomicInteger spacer, int shiftY, int mouseX, int mouseY) {
        var sharedX = this.width / 2 - 30 + shiftX;
        var posY = this.height / 2 - 85 + spacer.get() + shiftY;

        getPotentialComponent(getItem(), (s) -> guiGraphics.drawString(this.font, s, sharedX -1, posY + 2, 0));
        if(!this.showInventory){
            guiGraphics.drawString(font, appendDurability(getItem()), sharedX - 1, posY + 14, -1);
            getRepairSlotsComponent(getItem(), (s) -> guiGraphics.drawString(font, s, sharedX - 1, posY + 26, -1));
        } else {
            if(hoveredSlot instanceof RuneSlot && !hoveredSlot.getItem().isEmpty()){
                var item = hoveredSlot.getItem();
                var coreItemType = removeRuneCost(item);
                var cost = new ItemStack(coreItemType).getHoverName();
                var prefix = "Extraction Cost: ";
                var text = cost.getString();
                var quantityCost = withStyleComponent("1", borderColour);
                var list = List.of(
                    withStyleComponent(prefix, borderColour),
                    withStyleComponent(text + ": ", entity().checkAndChargeCores(coreItemType, false) ? SUB_HEADER_COLOUR : NEGATIVE_RED).copy().append(quantityCost)
                );
                guiGraphics.renderTooltip(font, list, Optional.empty(), mouseX, mouseY - 30);
            }
        }
    }

    private void hoverCarried(GuiGraphics guiGraphics, int x, int y){
        var carried = this.hoveredSlot == null || hoveredSlot.getItem().isEmpty() ? runeTableMenu.getCarried() : hoveredSlot.getItem();
        if (!carried.isEmpty()) {
            guiGraphics.renderTooltip(font, carried, x, y);
        }
    }

    private void overlayInventory(@NotNull GuiGraphics guiGraphics, int startX, int startY) {
        guiGraphics.pose().popPose();
        var i = 40;

        guiGraphics.pose().translate(0,0,20);
        var startX1 = startX + i + 3;

        boxMaker(guiGraphics, startX1 + 11, startY - 5, 86, 43, borderColour, groupFade());
        renderInventoryBackground(guiGraphics, this, 256, 24, true);
        guiGraphics.pose().pushPose();
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
        augmentCoreSlots(guiGraphics, adjustX, adjustY+1, borderColour, this.width + 6, this.height + 6, groupFade());
        runeSlotTexture(guiGraphics, mouseX, mouseY);
        renderItem(guiGraphics, mouseX, mouseY, startX, startY);

        if(!this.getItem().isEmpty() && this.showTooltip){
            var startX1 = this.width / 2 + 100;
            var startY1 = this.height / 2 - 73;
            guiGraphics.renderTooltip(font, getItem(), startX1, startY1);
        }

        overlayInventory(guiGraphics, startX, startY);

        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        hoverCarried(guiGraphics, mouseX, mouseY);

        if(!this.hoverTooltip.isEmpty()){
            guiGraphics.renderTooltip(font, this.hoverTooltip, Optional.empty(), mouseX, mouseY + 10);
        }

        this.hoverTooltip = new ArrayList<>();

    }

    private void runeSlotTexture(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var shiftY = 0;
        var shiftX = -5;
        var spacer = new AtomicInteger();

        remainingPotential(guiGraphics, shiftX, spacer, shiftY, mouseX, mouseY);
        var getRunes = getRuneholder(getItem());

        handleSlotsInGridLayout(
            (slotX, slotY, index) -> {
                if(this.showInventory){
                    for (ItemStack ignored : getRunes.runeSlots()) {
                        var size = 32;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0, 0, 100);
                        var x = slotX + runeTableMenu.posX - 96;
                        var y = slotY - runeTableMenu.posY - 9;
                        guiGraphics.blit(GUI_GENERAL_SLOT, x-3, y+8, 0, 0, size, size, size, size);
                        spacer.set(spacer.get() + runeTableMenu.runeYSpacer);
                        guiGraphics.pose().popPose();
                    }
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
        var heightOffset = 86 - 36;
        boxMaker(guiGraphics, startX11, startY11, Math.max(10, 74), heightOffset, borderColour, groupFade());

        if(getRunes.runeSlots().isEmpty() && this.showInventory){
            guiGraphics.drawCenteredString(this.font, "No Slots Available", startX11 + 74, startY11 + 46, SUB_HEADER_COLOUR);
        }

        var header = Component.literal(showInventory ? "Rune Manager" : "Gear Repair");
        var x = this.width / 2 - 34 + shiftX;
        var y1 = this.height / 2 - 102 + shiftY;
        guiGraphics.drawString(this.font, header, x, y1, OFF_WHITE);
    }


    private void renderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, int startX, int startY) {
        // Define constants for positioning and sizing
        final var ITEM_OFFSET_X = 40;
        final var ITEM_OFFSET_Y = -17;
        final var SHIFT_X = 75;
        final var WAND_ITEM_OFFSET = getItem().getItem() instanceof CasterItem ? 75 : 80;
        final var SCALED_ITEM = scaleItem - WAND_ITEM_OFFSET;
        final var OFFSET_Y = 91;

        var minX = startX + ITEM_OFFSET_X + 70 - SHIFT_X;
        var minY = startY + ITEM_OFFSET_Y - 94;
        var maxX = startX + ITEM_OFFSET_X + 58;
        var width = this.width - SHIFT_X * 2;
        var height = this.height - (140 - SCALED_ITEM);
        var posX = startX + 23;
        var posY = startY + ITEM_OFFSET_Y - 106;

        bezelMaker(guiGraphics, posX + 1, posY+1, 200, OFFSET_Y-1, 32, null);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -10);
        guiGraphics.enableScissor(minX, minY, maxX, minY + 172);

        var stand = EntityType.ARMOR_STAND.create(getMinecraft().level);

        if (getItem().getItem() instanceof ArmorItem armorItem && stand != null) {
            stand.setItemSlot(armorItem.getEquipmentSlot(), getItem());
            stand.setInvisible(true);

            var yOffset = switch (armorItem.getEquipmentSlot()) {
                case HEAD -> 70;
                case CHEST -> 8;
                case LEGS -> -30;
                case FEET -> -60;
                default -> 20;
            };

            renderEntityInInventoryFollowsMouse(guiGraphics, this.leftPos - 48, this.topPos, this.leftPos + 78, this.height / 2 + yOffset, 44, 0.0625F, mouseX, mouseY, stand,  320.0F);
        } else {
            SharedUI.renderItem(guiGraphics, width, height, getItem(), SCALED_ITEM, mouseX, mouseY, 16);
        }

        if (this.element != null) {
            var heightOffset = OFFSET_Y - 41;
            var colorA = groupFade();

            SharedUI.boxMaker(guiGraphics, minX, minY, 32, heightOffset, borderColour, colorA, borderColour);
        }

        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
    }
}
