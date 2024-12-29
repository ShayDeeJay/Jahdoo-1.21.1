package org.jahdoo.client.gui.block.wand_manager_table;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.block.wand_block_manager.WandManagerTableEntity;
import org.jahdoo.client.SharedUI;
import org.jahdoo.client.gui.block.augment_modification_station.AugmentCoreSlot;
import org.jahdoo.client.gui.block.augment_modification_station.InventorySlots;
import org.jahdoo.components.WandData;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.items.wand.WandItemHelper;
import org.jahdoo.networking.packet.client2server.PlayerExperienceC2SPacket;
import org.jahdoo.networking.packet.client2server.WandDataC2SPacket;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.client.IconLocations.*;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.SharedUI.getFadedColourBackground;
import static org.jahdoo.client.gui.ToggleComponent.*;
import static org.jahdoo.registers.AttributesRegister.replaceOrAddAttribute;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;
import static org.jahdoo.utils.ModHelpers.filterList;

public class WandManagerScreen extends AbstractContainerScreen<WandManagerMenu> {
    public static WidgetSprites WIDGET = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final WandManagerMenu wandManager;
    boolean showInventory;
    boolean setView;
    int scaleItem = 60;
    AbstractElement element;
    int borderColour;
    boolean isHovering;

    public WandManagerScreen(WandManagerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        var element = ElementRegistry.getElementByWandType(pMenu.getWandManagerEntity().getWandSlot().getItem());
        var color = FastColor.ARGB32.color(100, element.getFirst().textColourPrimary());
        this.wandManager = pMenu;
        this.switchVisibility();
        this.element = element.getFirst();
        this.borderColour = element.isEmpty() ? BORDER_COLOUR : color;
    }

    @Override
    protected void init() {
        super.init();
        this.keyboardButton();
        this.upgradeButton();
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

    public void reRollBaseModifiers(){
        var wandItemCopy = getWand().copy();
        var getData = wandItemCopy.get(WAND_DATA);
        if (getData != null) {

            for (var modifier : wandItemCopy.getAttributeModifiers().modifiers()) {
                var id = modifier.modifier().id().getPath().intern();
                var attribute = modifier.attribute();
                var rarityId = WandData.wandData(wandItemCopy).rarityId();
                var ranges = JahdooRarity.getAllRarities().get(rarityId).getAttributes();
                var value = switch (id){
                    case String s when s.contains("cooldown.cooldown_reduction") -> ranges.getRandomCooldown();
                    case String s when s.contains("mana.cost_reduction") -> ranges.getRandomManaReduction();
                    default -> ranges.getRandomDamage();
                };
                replaceOrAddAttribute(wandItemCopy, id, attribute, value, EquipmentSlot.MAINHAND, false);
            }
            WandData.createRefinementPotential(wandItemCopy, Math.max(0, getData.refinementPotential() - 20));
            PacketDistributor.sendToServer(new WandDataC2SPacket(wandItemCopy, wandManager.getWandManagerEntity().getBlockPos()));
            var player = Minecraft.getInstance().player;
            if(player != null){
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
                PacketDistributor.sendToServer(new PlayerExperienceC2SPacket(player.experienceLevel - getExperienceCost()));
            }
        }
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
        var posX = this.width / 2 - 136;
        var posY = this.height / 2 + 3;
        this.addRenderableWidget(
            menuButton(
                posX, posY, (press) -> inventoryHandler(), resourceLocation,
                24, false, 0, WIDGET, true
            )
        );
    }

    public void inventoryHandler(){
        setView = !setView;
        showInventory = !showInventory;
        switchVisibility();
        this.rebuildWidgets();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    private void renderWand(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int startX, int startY) {
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
            var colorA = FastColor.ARGB32.color(0, borderColour);
            var colorFade = FastColor.ARGB32.color(100, borderColour);
            var heightOffset = 86 - (showInventory ? 40 : 0);
            var color = FastColor.ARGB32.color(50, borderColour);
            boxMaker(guiGraphics, minX, minY, 30, heightOffset, getFadedColourBackground(0.4f));
            boxMakerTest(guiGraphics, minX, minY, 30, heightOffset, color, colorA, colorFade);
        }
        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
        wandProperties(guiGraphics, startX, i, startY, i1);

    }

    public ItemStack getWand(){
        return this.wandManager.getWandManagerEntity().getWandSlot();
    }

    private void wandProperties(@NotNull GuiGraphics guiGraphics, int startX, int i, int startY, int i1) {
        var shiftY = 0;
        var shiftX = -5;
        var spacer = new AtomicInteger();

        if(!this.setView){
            baseWandProperties(guiGraphics, shiftX, spacer, shiftY, startX, startY);
        } else {
            var getRunes = WandData.wandData(getWand());
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
        }

        var startX1 = this.width / 2 - 38 + shiftX;
        var startY1 = this.height / 2 - 111 + shiftY;
        boxMaker(guiGraphics, startX1, startY1, 39, 10, borderColour, groupFade());


        var header = Component.literal((this.setView ? "Rune" : "Wand") + " Manager");
        var x = this.width / 2 - 34 + shiftX;
        var y1 = this.height / 2 - 105 + shiftY;
        guiGraphics.drawString(this.font, header, x, y1, ColourStore.SUB_HEADER_COLOUR);
    }

    private static int groupFade() {
        return getFadedColourBackground(0.7f);
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
            var itemModifiers = WandItemHelper.getItemModifiers(getWand());
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

    public static Component getModifierRange(WandManagerMenu wandManagerMenu, String type){
        var rarity = WandData.wandData(wandManagerMenu.getWandManagerEntity().getWandSlot()).rarityId();
        var attributes = JahdooRarity.getAllRarities().get(rarity).getAttributes();
        var first = switch (type){
            case String s when s.contains("Cooldown") -> attributes.getCooldownRange();
            case String s when s.contains("Mana") -> attributes.getManaReductionRange();
            default -> attributes.getDamageRange();
        };
        var headerBuilder = "(" + first.getFirst() + "%" + " - " + first.getSecond() + "%" + ")";
        return ModHelpers.withStyleComponent(headerBuilder, BORDER_COLOUR);
    }

    private void coreSlots(@NotNull GuiGraphics guiGraphics, int adjustX, int adjustY) {
        var spacer = new AtomicInteger();
        var posX = this.width / 2;
        var posY = this.height / 2;
        var startX = posX + adjustX - 159;
        var startY = posY - 62 + adjustY;
        boxMaker(guiGraphics, startX, startY, 17, 46, borderColour, groupFade());
        for(ResourceLocation location : getOverlays()){
            var offsetX = 158;
            var offsetY = 60;
            var x = posX - offsetX + adjustX;
            var y = posY + spacer.get() - offsetY + adjustY;
            guiGraphics.blit(GUI_ITEM_SLOT, x, y, 0,0,32,32,32,32);
            guiGraphics.blit(location, x, y, 0,0,32,32,32,32);
            spacer.set(spacer.get() + 28);
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

    private List<ResourceLocation> getOverlays(){
        return List.of(CORE, ADVANCED_AUGMENT_CORE, AUGMENT_HYPER_CORE);
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
        reRollContainer(guiGraphics, startX, startY);
        coreSlots(guiGraphics, adjustX, adjustY);
        renderWand(guiGraphics, mouseX, mouseY, startX, startY);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        overlayInventory(guiGraphics, startX, startY);
        extracted(guiGraphics, mouseX, mouseY, i, startY);

        this.isHovering = false;
    }

    private void extracted(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int i, int startY) {
        var player = this.getMinecraft().player;
        if(player == null) return;
        var exp = player.experienceLevel;
        var getMaxCost = getExperienceCost();
        var expColour = exp >= getMaxCost ? 8453920 : -2070938;
        var refinementPotential = Component.literal(String.valueOf(getMaxCost));
        var expLvl = Component.literal(String.valueOf(exp));
        var offsetX = 0;
        var offsetY = -27;
        var potential = WandData.wandData(getWand()).refinementPotential();
        if(!showInventory && isHovering && potential > 0){
            SharedUI.boxMaker(guiGraphics, mouseX - 26 + offsetX, mouseY + offsetY, 26, 13, BORDER_COLOUR, getFadedColourBackground(0.6f));
            SharedUI.drawStringWithBackground(guiGraphics, this.font, refinementPotential, mouseX + offsetX, mouseY + 15 + offsetY, 0, expColour, true);
            guiGraphics.drawCenteredString(font, "Exp Cost", mouseX + offsetX, mouseY + 4 + offsetY, -1);
            SharedUI.drawStringWithBackground(guiGraphics, this.font, expLvl, i, startY + 69, 0, 8453920, true);
            renderExperienceBar(guiGraphics, i - 91, startY + 78);
        }
    }

    private boolean canModify(){
        var player = this.getMinecraft().player;
        if(player == null) return false;
        var exp = player.experienceLevel;
        var potential = WandData.wandData(getWand()).refinementPotential();
        var getMaxCost = getExperienceCost();
        return exp >= getMaxCost && potential > 0  ;
    }

    private int getExperienceCost() {
        var potential = WandData.wandData(getWand()).refinementPotential();
        return Math.max(10, 100 - potential);
    }

    private void renderExperienceBar(GuiGraphics guiGraphics, int x, int l) {
        var expBackground = ResourceLocation.withDefaultNamespace("hud/experience_bar_background");
        var expProgress = ResourceLocation.withDefaultNamespace("hud/experience_bar_progress");
        if(this.minecraft == null || this.minecraft.player == null) return;
        this.minecraft.getProfiler().push("expBar");
        int i = this.minecraft.player.getXpNeededForNextLevel();
        if (i > 0) {
            int k = (int)(this.minecraft.player.experienceProgress * 183.0F);
            guiGraphics.blitSprite(expBackground, x, l, 182, 5);
            if (k > 0) guiGraphics.blitSprite(expProgress, 182, 5, 0, 0, x, l, k, 5);
        }
        this.minecraft.getProfiler().pop();
    }

    private void reRollContainer(@NotNull GuiGraphics guiGraphics, int startX, int startY) {
        var startX1 = startX + 105;
        var startY1 = startY + 35;
        var colour = 0xb97700;
        var reRoll = ModHelpers.withStyleComponent("Re-Roll", colour);
        boxMaker(guiGraphics, startX1, startY1, 32, 9, BORDER_COLOUR, getFadedColourBackground(0.9f));
        guiGraphics.drawCenteredString(this.font, reRoll, startX + 146, startY + 40, 0);
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
