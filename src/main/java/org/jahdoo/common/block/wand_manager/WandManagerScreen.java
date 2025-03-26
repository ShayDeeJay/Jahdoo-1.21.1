package org.jahdoo.common.block.wand_manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.client.slots.InventorySlots;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.common.items.wand.WandItemHelper;
import org.jahdoo.common.networking.client2server.ItemInBlockC2SP;
import org.jahdoo.common.networking.client2server.PlayerExpC2SP;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.Helpers.filterList;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.button.ToggleComponent.menuButton;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.registers.AttributeReg.replaceOrAddAttribute;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;
import static org.jahdoo.common.registers.ElementReg.fromWand;

public class WandManagerScreen extends AbstractContainerScreen<WandManagerMenu> {

    private static final WidgetSprites WIDGET = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final WandManagerMenu wandManager;
    private boolean showInventory;
    private boolean setView;
    private int scaleItem = 60;
    private AbstractElement element;
    private int borderColour;
    private boolean isHovering;

    public WandManagerScreen(WandManagerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        var element = fromWand(menu.getWandManagerEntity().getWandSlot().getItem());

        element.ifPresent(
            getElement -> {
                this.element = getElement;
                this.borderColour =  color(100, getElement.textColourA());
            }
        );
        this.wandManager = menu;
        this.switchVisibility();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partial, int mouseX, int mouseY) {}

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    private int getPotential() {
        return  RuneHolder.potential(getWand());
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public WandManagerEntity entity(){
        return this.wandManager.getWandManagerEntity();
    }

    public ItemStack getWand(){
        return this.wandManager.getWandManagerEntity().getWandSlot();
    }

    private static int groupFade() {
        return getFadedColourBackground(0.7f);
    }

    private void scaleItem() {
        var scale = 8;
        this.scaleItem = !setView ? Math.min(140, scaleItem + scale) : Math.max(62, scaleItem - scale);
    }

    private int getExperienceCost() {
        var potential = getPotential();
        return Math.max(10, 100 - potential);
    }

    @Override
    protected void init() {
        super.init();
        this.keyboardButton();
        this.upgradeButton();
    }

    public void inventoryHandler(){
        setView = !setView;
        showInventory = !showInventory;
        switchVisibility();
        this.rebuildWidgets();
    }

    private void hoverCarried(GuiGraphics guiGraphics, int x, int y){
        var carried = this.hoveredSlot == null || hoveredSlot.getItem().isEmpty() ? wandManager.getCarried() : hoveredSlot.getItem();
        if(carried.getItem() instanceof RuneItem){
            var getTooltip = this.getTooltipFromContainerItem(carried);
            if (!carried.isEmpty()) {
                guiGraphics.renderTooltip(font, getTooltip, Optional.empty(), x, y);
            }
        }
    }

    private void keyboardButton() {
        var resourceLocation = this.showInventory ? INFORMATION : INVENTORY;
        var posX = this.width / 2 - 136;
        var posY = this.height / 2 + 3;
        this.addRenderableWidget(
            menuButton(
                posX, posY, (press) -> inventoryHandler(), resourceLocation,
                24, false, 0, WIDGET, true
            )
        );
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

    private void overlayInventory(GuiGraphics guiGraphics, int startX, int startY) {
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

    private void upgradeButton() {
        var posX = this.width/2 - 39;
        var posY = this.height/2 + 53;

        this.addRenderableWidget(
            menuButtonSound(
                posX, posY, (press) -> reRollBaseModifiers(),
                REFRESH, 26, !this.canModify() || showInventory, 0, WIDGET, this.canModify(),
                () -> {
                    this.rebuildWidgets();
                    this.isHovering = true;
                }
            )
        );
    }

    private boolean canModify(){
        var player = this.getMinecraft().player;
        if(player == null) return false;
        var exp = player.experienceLevel;
        var potential = getPotential();
        var getMaxCost = getExperienceCost();
        return exp >= getMaxCost && potential > 0  ;
    }

    private void reRollContainer(GuiGraphics guiGraphics, int startX, int startY) {
        var startX1 = startX + 105;
        var startY1 = startY + 35;
        var colour = 0xb97700;
        var potential = getPotential() > 0;
        var reRoll = withStyleComponent(potential ? "Re-Roll" : "Unmodifiable", potential ? colour : ColourStore.HEADER_COLOUR);
        boxMaker(guiGraphics, startX1, startY1, potential ? 30 : 42, 9, BORDER_COLOUR, getFadedColourBackground(0.9f));
        guiGraphics.drawString(this.font, reRoll, startX + 126, startY + 40, 0);
    }

    private void remainingPotential(GuiGraphics guiGraphics, int shiftX, AtomicInteger spacer, int shiftY) {
        if (getWand().getItem() instanceof WandItem) {
            var itemModifiers = WandItemHelper.getItemModifiers(getWand(), getMinecraft().level);
            var potentialList = filterList(itemModifiers, "Potential");
            var sharedX = this.width / 2 - 30 + shiftX;
            for (Component component : potentialList) {
                var posY = this.height / 2 - 85 + spacer.get() + shiftY;
                guiGraphics.drawString(this.font, component, sharedX -1, posY + 2, 0);
            }
        }
    }

    public static Component getModifierRange(WandManagerMenu wandManagerMenu, String type){
        var wandSlot = wandManagerMenu.getWandManagerEntity().getWandSlot();
        var rarity = wandSlot.get(JAHDOO_RARITY);
        if(rarity != null){
            var attributes = JahdooRarity.getAllRarities().get(rarity).getAttributes();
            var first = switch (type) {
                case String s when s.contains("Cooldown") -> attributes.getCooldownRange();
                case String s when s.contains("Mana") -> attributes.getManaReductionRange();
                default -> attributes.getDamageRange();
            };
            var headerBuilder = "(" + first.getFirst() + "%" + " - " + first.getSecond() + "%" + ")";
            return withStyleComponent(headerBuilder, BORDER_COLOUR);
        }
        return Component.empty();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        var adjustX = 18;
        var adjustY = -27;
        var i = this.width / 2;
        var i1 = this.height / 2;
        var startX = i - 140;
        var startY = i1 + 22;

        scaleItem();
        reRollContainer(guiGraphics, startX, startY);
        augmentCoreSlots(guiGraphics, adjustX, adjustY, borderColour, this.width, this.height, groupFade());
        renderWand(guiGraphics, mouseX, mouseY, startX, startY);
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

    private void experienceCost(GuiGraphics guiGraphics, int mouseX, int mouseY, int i, int startY) {

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
            boxMaker(guiGraphics, mouseX - 26 + offsetX, mouseY + offsetY, 26, 13, BORDER_COLOUR, getFadedColourBackground(0.6f));
            drawStringWithBackground(guiGraphics, this.font, refinementPotential, mouseX + offsetX, mouseY + 15 + offsetY, 0, expColour, true);
            guiGraphics.drawCenteredString(font, "Exp Cost", mouseX + offsetX, mouseY + 4 + offsetY, -1);
            drawStringWithBackground(guiGraphics, this.font, expLvl, i, startY + 69, 0, 8453920, true);
            renderExperienceBar(guiGraphics, i - 91, startY + 78, this.getMinecraft());
        }

    }


    public void reRollBaseModifiers(){

        var wandItemCopy = getWand().copy();

        for (var modifier : wandItemCopy.getAttributeModifiers().modifiers()) {
            var id = modifier.modifier().id().getPath().intern();
            var attribute = modifier.attribute();
            var rarityId = wandItemCopy.get(JAHDOO_RARITY);
            if(rarityId != null){
                var ranges = JahdooRarity.getAllRarities().get(rarityId).getAttributes();
                var value = switch (id) {
                    case String s when s.contains("cooldown.cooldown_reduction") -> ranges.getRandomCooldown();
                    case String s when s.contains("mana.cost_reduction") -> ranges.getRandomManaReduction();
                    default -> ranges.getRandomDamage();
                };
                replaceOrAddAttribute(wandItemCopy, attribute.getRegisteredName(), attribute, value, EquipmentSlot.MAINHAND, false);
            }
        }

        var max = Math.max(0, RuneHolder.potential(wandItemCopy) - 20);
        RuneHolder.createRefinementPotential(wandItemCopy, max);
        PacketDistributor.sendToServer(new ItemInBlockC2SP(wandItemCopy, wandManager.getWandManagerEntity().getBlockPos()));

        var player = Minecraft.getInstance().player;
        if(player != null){
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
            PacketDistributor.sendToServer(new PlayerExpC2SP(player.experienceLevel - getExperienceCost()));
        }

    }

    private void renderWand(GuiGraphics guiGraphics, int mouseX, int mouseY, int startX, int startY) {

        var i = 40;
        var i1 = -17;
        var shiftX1 = 75;
        var minX = startX + i + 70 - shiftX1;
        var minY = startY + i1 - 94;
        var maxX = startX + i + 70 + 60;
        var width1 = this.width - shiftX1 * 2;
        var height1 = this.height - (144 - scaleItem);
        var posX = startX + 23;
        var posY = startY + i1 - 106;
        var offsetY = 164 - (showInventory ? 80 : 0);

        guiGraphics.pose().pushPose();
        bezelMaker(guiGraphics, posX, posY, 52, offsetY, 32, this.element);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,-10);
        guiGraphics.enableScissor(minX, minY, maxX, minY + (!setView ? 172 : 92));
        renderItem(guiGraphics, width1, height1, getWand(), scaleItem, mouseX, mouseY, 16);

        if(this.element != null){
            var colorFade = color(100, borderColour);
            var heightOffset = 86 - (showInventory ? 40 : 0);
            var color = color(50, borderColour);
            var colorA = color(20, borderColour);
            boxMaker(guiGraphics, minX, minY, 30, heightOffset, getFadedColourBackground(0.4f));
            boxMaker(guiGraphics, minX, minY, 30, heightOffset, color, colorA, colorFade);
        }

        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
        wandProperties(guiGraphics, startX, i, startY, i1);

    }

    private void baseWandProperties(
        @NotNull GuiGraphics guiGraphics,
        int shiftX,
        AtomicInteger spacer,
        int shiftY,
        int startX,
        int startY
    ) {
        if(getWand().getItem() instanceof WandItem){
            var itemModifiers = WandItemHelper.getItemModifiers(getWand(), getMinecraft().level);
            var rarityAndSlots = filterList(itemModifiers, "Rarity", "Slots", "Potential");
            var modifiersAndHeader = filterList(itemModifiers, "%", "Applies");
            var widthHeader = 0;
            var widthProperties = 0;
            var sharedX = this.width / 2 - 30 + shiftX;

            for (Component components : rarityAndSlots) {
                var posY = this.height / 2 - 85 + spacer.get() + shiftY;
                guiGraphics.drawString(this.font, components, sharedX, posY, 0);
                spacer.set(spacer.get() + 12);
                if (widthHeader < font.width(components)) widthHeader = font.width(components);
            }

            var startX1 = startX + 102 + shiftX;
            var startY1 = startY - 111 + shiftY;
            var widthOffset = widthHeader / 2 + 8;
            boxMaker(guiGraphics, startX1, startY1, widthOffset, 20, borderColour, groupFade());

            for (Component components : modifiersAndHeader) {
                var posY = this.height / 2 - 74 + spacer.get() + shiftY;
                var skip = components.getString().contains("Applies");

                guiGraphics.drawString(this.font, components, sharedX, posY, 0);
                if(!skip){
                    var range = getModifierRange(wandManager, components.getString());
                    var posX = this.width / 2 - 28 + shiftX;
                    var posY1 = this.height / 2 - 64 + spacer.get() + shiftY;
                    guiGraphics.drawString(this.font, range, posX, posY1, ColourStore.HEADER_COLOUR);
                }
                spacer.set(spacer.get() + (!skip ? 24 : 15));
                if (widthProperties < font.width(components)) widthProperties = font.width(components);
            }

            var startY2 = startY - 69 + shiftY;
            var offset = widthProperties / 2 + 8;
            boxMaker(guiGraphics, startX1, startY2, offset, 65, borderColour, groupFade());
        }
    }

    private void wandProperties(GuiGraphics guiGraphics, int startX, int i, int startY, int i1) {
        var shiftY = 0;
        var shiftX = -5;
        var spacer = new AtomicInteger();

        if(!this.setView){
            baseWandProperties(guiGraphics, shiftX, spacer, shiftY, startX, startY);
        } else {
            remainingPotential(guiGraphics, shiftX, spacer, shiftY);
            var getRunes = RuneHolder.getRuneholder(getWand());
            handleSlotsInGridLayout(
                (slotX, slotY, index) -> {
                    for (ItemStack ignored : getRunes.runeSlots()) {
                        var size = 32;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0,0,100);
                        var x = slotX + wandManager.posX - 96;
                        var y = slotY - wandManager.posY - 9;
                        guiGraphics.blit(GUI_GENERAL_SLOT, x, y, 0,0, size, size, size, size);
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

            var startX1 = startX + i + 62 + shiftX;
            var startY1 = startY + i1 - 94 + shiftY;
            var heightOffset = 86 - (showInventory ? 40 : 0);
            boxMaker(guiGraphics, startX1, startY1, Math.max(10, 74), heightOffset, borderColour, groupFade());

            if(getRunes.runeSlots().isEmpty()){
                guiGraphics.drawCenteredString(this.font, "No Slots Available", startX1 + 74, startY1 + 40, ColourStore.SUB_HEADER_COLOUR);
            }
        }

        var startX1 = this.width / 2 - 38 + shiftX;
        var startY1 = this.height / 2 - 111 + shiftY;
        boxMaker(guiGraphics, startX1, startY1, 39, 10, borderColour, groupFade());


        var header = Component.literal((this.setView ? "Rune" : "Wand") + " Manager");
        var x = this.width / 2 - 34 + shiftX;
        var y1 = this.height / 2 - 105 + shiftY;
        guiGraphics.drawString(this.font, header, x, y1, ColourStore.SUB_HEADER_COLOUR);
    }

}
