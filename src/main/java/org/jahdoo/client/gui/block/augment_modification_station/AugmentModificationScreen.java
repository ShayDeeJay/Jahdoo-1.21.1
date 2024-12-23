package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.client.SharedUI;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.networking.packet.client2server.AugmentModificationChargeC2S;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static net.minecraft.sounds.SoundEvents.APPLY_EFFECT_TRIAL_OMEN;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.IconLocations.*;
import static org.jahdoo.client.gui.ToggleComponent.*;
import static org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationData.*;
import static org.jahdoo.items.augments.AugmentItemHelper.getModifierContextSingle;
import static org.jahdoo.items.augments.AugmentRatingSystem.calculateRatingNext;
import static org.jahdoo.networking.packet.client2server.AugmentModificationChargeC2S.chargeCoreSides;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.utils.ModHelpers.doubleFormattedDouble;
import static org.jahdoo.utils.ModHelpers.withStyleComponent;

public class AugmentModificationScreen extends AbstractContainerScreen<AugmentModificationMenu> {
    public static WidgetSprites WIDGET = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final AugmentModificationMenu augmentModificationMenu;
    private final ItemStack item;
    private double yScroll;
    private int selectedY;
    private Component upgradeValue;
    public Inventory inventory;
    boolean showInventory;
    private Component key;

    public AugmentModificationScreen(AugmentModificationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.augmentModificationMenu = pMenu;
        this.item = pMenu.getAugmentEntity().getInteractionSlot();
        this.inventory = pPlayerInventory;
        this.switchVisibility();
    }

    @Override
    protected void init() {
        super.init();
        this.displayAugmentProperties();
        this.keyboardButton();
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public AugmentModificationEntity entity(){
        return this.augmentModificationMenu.getAugmentEntity();
    }

    public void displayAugmentProperties(){
        if(item.isEmpty()) return;
        var components = getComponents(item);
        var spacer = new AtomicInteger();
        var compNew = components.subList(1, components.size()-2);
        int width = this.width / 2;

        for (Component component : compNew){
            if(!component.equals(Component.literal(" ")) && !component.getString().contains("Unique")){
                String regex = ".*\\d.*";
                var ySpacer = (this.height / 2 - 85) + spacer.get();
                int selectedY1 = (int) (ySpacer + this.yScroll);
                buildPropertiesWithHighlight(component, width, selectedY1);
                if (Pattern.matches(regex, component.getString()) && !component.getString().contains(")")) {
                    var getTag = this.item.get(DataComponentRegistry.WAND_ABILITY_HOLDER);
                    if (getTag == null) return;
                    var x = getAbilityModifiers(component, getTag);
                    var correctAdjustment = x.isHigherBetter() ? x.actualValue() == x.highestValue() : x.actualValue() == x.lowestValue();
                    var nexUpgrade = x.isHigherBetter() ? x.actualValue() + x.step() == x.highestValue() : x.actualValue() - x.step() == x.lowestValue();
                    upgradeButton(component, width, ySpacer, nexUpgrade, correctAdjustment, x);
                }
                spacer.set(spacer.get() + (component.getString().contains(")") || !Pattern.matches(regex, component.getString()) ? 17 : 10));
            }
        }
    }

    private void buildPropertiesWithHighlight(Component component, int width, int selectedY1) {
        var rebuild = Component.empty();
        if (component.getString().contains("|")) {
            for (Component component1 : component.toFlatList().subList(0, 2)) rebuild.append(component1);
            rebuild.append(ModHelpers.withStyleComponent(component.toFlatList().getLast().getString(), -1129857));
            this.addRenderableOnly(textRenderable(width - 139, selectedY1, rebuild, this.getMinecraft()));
        } else {
            this.addRenderableOnly(textRenderable(width - 139, selectedY1, component, this.getMinecraft()));
        }
    }

    private void upgradeButton(Component component, int width, int ySpacer, boolean nexUpgrade, boolean correctAdjustment, AbilityHolder.AbilityModifiers x) {
        var canPurchase = canPurchase(calculateRatingNext(x));
        int posX = width + 72;
        int posY = (int) (ySpacer + 11 + this.yScroll);
        this.addRenderableWidget(
            menuButtonSound(
                posX, posY,
                (press) -> {
                    if(canPurchase) doOnClick(component, item, nexUpgrade, posX, posY);
                },
                correctAdjustment || !canPurchase ? UPGRADE_DISABLED : UPGRADE, 22,
                correctAdjustment || !this.isInHitbox(posX, posY) || !canPurchase,
                correctAdjustment || !canPurchase ? 0 : 8, WIDGET,
                !correctAdjustment  && this.isInHitbox(posX, posY) && canPurchase,
                () -> {
                    var getHighest = x.isHigherBetter() ? x.actualValue() + x.step() : x.actualValue() - x.step();
                    var original = getModifierContextSingle(extractName(component.getString()), String.valueOf(doubleFormattedDouble(getHighest)), 1);
                    this.upgradeValue = withStyleComponent("↑ " + original.getString(), -7092917);
                    this.selectedY = correctAdjustment ? 0 : ySpacer;
                    this.key = component;
                }
            )
        );
    }

    private static AbilityHolder.AbilityModifiers getAbilityModifiers(Component component, WandAbilityHolder getTag) {
        if(component == null) return new AbilityHolder.AbilityModifiers(0,0,0,0,0,true);
        var abilityKey = getTag.abilityProperties().keySet().stream().findFirst().get();
        return getTag.abilityProperties().get(abilityKey).abilityProperties().get(extractName(component.getString()));
    }

    private void doOnClick(Component component, ItemStack itemStack, boolean correctAdjustment, int posX, int posY){
        if(this.isInHitbox(posX, posY)){
            var getTag = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER);
            if(getTag == null) return;
            var abilityKey = getTag.abilityProperties().keySet().stream().findFirst().get();
            if(component == null) return;
            var localHolder = getAbilityModifiers(component, getTag);
            this.chargeCoreType(this.getChargeableCore());
            updateAugmentConfig(
                extractName(component.getString()), localHolder, 0, abilityKey, getTag,
                (myHolder) ->  this.item.set(WAND_ABILITY_HOLDER, myHolder),entity()
            );
            if(correctAdjustment){
                ModHelpers.getLocalSound(this.getMinecraft().level, entity().getBlockPos(), APPLY_EFFECT_TRIAL_OMEN, 1, 2);
            }
            this.rebuildWidgets();
        }
    }

    private void switchVisibility() {
        for (Slot slot : this.menu.slots) {
            if(slot instanceof InventorySlots inventorySlots){
                inventorySlots.setActive(showInventory);
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

    public void inventoryHandler(){
        showInventory = !showInventory;
        switchVisibility();
        this.yScroll = 0;
        this.rebuildWidgets();
        this.selectedY = 0;
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
        int size = getComponents(item)
            .stream()
            .filter(component -> component.getString().contains("|"))
            .toList()
            .size();
        if (size > 6 || size > 2 && showInventory) {
            int b = 7 * size * size - 135 * size + 578;
            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, b + (!showInventory ? -20 : -120)));
            this.rebuildWidgets();
            this.selectedY = 0;
        } else this.yScroll = 0;
    }

    private void chargeCoreType(Item item){
        ItemStack itemStack1 = new ItemStack(item);
        PacketDistributor.sendToServer(new AugmentModificationChargeC2S(entity().getBlockPos(), itemStack1));
        chargeCoreSides(entity(), itemStack1);
    }

    private boolean canPurchase(Item itemStack){
        var inputItemHandler = entity().inputItemHandler;
        for(int i = 1; i < inputItemHandler.getSlots(); i++){
            if(inputItemHandler.getStackInSlot(i).getItem() == itemStack) return true;
        }
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        SharedUI.setCustomBackground(this.height, this.width, guiGraphics);
        selectedBoxUpgrade(guiGraphics, mouseX, mouseY);
        selectedBox(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        guiGraphics.disableScissor();

        var adjustX = 18;
        var adjustY = -27;
        var startX = this.width / 2 - 140;
        var startY = this.height/2 + 22;

        SharedUI.header(guiGraphics, this.width, this.height, this.item, this.font);
        overlayInventory(guiGraphics, startX, startY);
        var element = ElementRegistry.getElementByTypeId(getElementIdAugment(this.item));
        if(!element.isEmpty()){
            boxMaker(guiGraphics, startX - 18 + adjustX, startY - 47 + adjustY, 18, 48, BORDER_COLOUR);
            SharedUI.bezelMaker(guiGraphics, startX + adjustX + 9, startY + adjustY - 123, 193, 224, 32, element.getFirst());
            coreSlots(guiGraphics, adjustX, adjustY);
        }

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
        guiGraphics.pose().translate(0,0,10);
        if(showInventory){
            var i = 40;
            var i1 = -17;
            boxMaker(guiGraphics, startX + i, startY + i1, 100, 55, BORDER_COLOUR, SharedUI.getFadedColourBackground(0.9f));
            renderInventoryBackground(guiGraphics, this, 256, 24, this.showInventory);
        }
        guiGraphics.pose().pushPose();
    }

    private List<ResourceLocation> getOverlays(){
        return List.of(CORE, ADVANCED_AUGMENT_CORE, AUGMENT_HYPER_CORE);
    }

    private void selectedBox(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(!this.isInHitbox(mouseX, mouseY)) return;
        if(this.selectedY <= 0) return;
        var colour = getAbstractElement(entity()).textColourSecondary();
        var semiTransLayer = getFadedColourBackground(0.8f);

        boxMaker(guiGraphics, this.width/2 - 97, (int) (this.selectedY + 8 + yScroll), 97, 14, colour, semiTransLayer);
    }

    private void selectedBoxUpgrade(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(!this.isInHitbox(mouseX, mouseY)) return;
        if(this.selectedY <= 0) return;
        var startX = this.width / 2 + 102;
        var startY = this.selectedY + 8;

        var colourBorder = getAbstractElement(entity()).textColourSecondary();
        var semiTransLayer = getFadedColourBackground(0.8f);
        boxMaker(guiGraphics, startX , (int) (startY + yScroll), 35, 14, colourBorder, semiTransLayer);
        boxMaker(guiGraphics, startX, (int) (startY + yScroll), 35, 14, colourBorder, semiTransLayer);
        boxMaker(guiGraphics, startX + 68, (int) (startY + yScroll), 14, 14, colourBorder, semiTransLayer);
        boxMaker(guiGraphics, startX + 68, (int) (startY + yScroll), 14, 14, colourBorder, semiTransLayer);
        guiGraphics.drawCenteredString(this.font, this.upgradeValue, startX + 34, (int) (startY + 10 + yScroll), 0);
        if(this.item != null && this.item.has(WAND_ABILITY_HOLDER)){
            if(this.upgradeValue != null){
                guiGraphics.renderFakeItem(new ItemStack(getChargeableCore()),  startX + 74, (int) (startY + 6 + yScroll));
            }
        }

    }

    public Item getChargeableCore() {
        return calculateRatingNext(getAbilityModifiers(this.key, entity().getInteractionSlot().get(WAND_ABILITY_HOLDER)));
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
