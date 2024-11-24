package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.client.gui.IconLocations;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static net.minecraft.sounds.SoundEvents.APPLY_EFFECT_TRIAL_OMEN;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.gui.IconLocations.*;
import static org.jahdoo.client.gui.ToggleComponent.*;
import static org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationData.getAbstractElement;
import static org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationData.updateAugmentConfig;
import static org.jahdoo.client.gui.block.wand_block.WandBlockScreen.hoveredOverlayInventory;
import static org.jahdoo.items.augments.AugmentItemHelper.getModifierContext;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.utils.ModHelpers.withStyleComponent;

public class AugmentModificationScreen extends AbstractContainerScreen<AugmentModificationMenu> {
    public static final int BORDER_COLOUR = -10066330;
    WidgetSprites widget = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final AugmentModificationMenu augmentModificationMenu;
    private final ItemStack item;
    private double yScroll;
    private int selectedY;
    private Component upgradeValue;
    public Inventory inventory;
    boolean showInventory;

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
        int posX = width + 72;
        int posY = (int) (ySpacer + 11 + this.yScroll);
        this.addRenderableWidget(
            menuButtonSound(
                posX,
                posY,
                (press) -> doOnClick(component, item, nexUpgrade, posX, posY),
                correctAdjustment ? UPGRADE_DISABLED : UPGRADE,
                22,
                correctAdjustment || !this.isInHitbox(posX, posY),
                correctAdjustment ? 0 : 8,
                widget,
                !correctAdjustment  && this.isInHitbox(posX, posY),
                () -> {
                    var getHighest = x.isHigherBetter() ? x.actualValue() + x.step() : x.actualValue() - x.step();
                    var original = getModifierContext(extractName(component.getString()), ModHelpers.roundNonWholeString(getHighest), 1);
                    this.upgradeValue = withStyleComponent("↑ " + original.getString(), -7092917);
                    this.selectedY = correctAdjustment ? 0 : ySpacer;
                }
            )
        );
    }

    private static AbilityHolder.AbilityModifiers getAbilityModifiers(Component component, WandAbilityHolder getTag) {
        var abilityKey = getTag.abilityProperties().keySet().stream().findFirst().get();
        return getTag.abilityProperties().get(abilityKey).abilityProperties().get(extractName(component.getString()));
    }

    private void doOnClick(Component component, ItemStack itemStack, boolean correctAdjustment, int posX, int posY){
        if(this.isInHitbox(posX, posY)){
            var getTag = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER);
            if(getTag == null) return;
            var abilityKey = getTag.abilityProperties().keySet().stream().findFirst().get();
            var localHolder = getAbilityModifiers(component, getTag);
            updateAugmentConfig(extractName(component.getString()), localHolder, 0, abilityKey, getTag, (myHolder) -> {
                this.item.set(WAND_ABILITY_HOLDER, myHolder);
                this.rebuildWidgets();
            },entity());
            if(correctAdjustment){
                ModHelpers.getLocalSound(getMinecraft().level, entity().getBlockPos(), APPLY_EFFECT_TRIAL_OMEN, 1, 2);
            }
        }
    }

    private void switchVisibility() {
        for (Slot slot : this.menu.slots) {
            if(slot instanceof InventorySlots inventorySlots){
                inventorySlots.setActive(showInventory);
            }
        }
    }

    public static String extractName(String input) {
        if (input == null || !input.contains("|")) return "";
        return input.split("\\|")[0].trim();
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
        var fromColour = -804253680;
        var toColour = -804253680;
        var borderColour = BORDER_COLOUR;

        guiGraphics.fillGradient(widthFrom, heightFrom, widthTo, heightTo, fromColour, toColour);
        guiGraphics.hLine(width-100, width + 99, this.height/2 - 70, borderColour);
        guiGraphics.renderOutline(widthFrom, heightFrom, widthTo - widthFrom, heightTo - heightFrom, borderColour);
        guiGraphics.enableScissor(0, heightFrom + 50, this.width, heightTo - 5);
    }

    private void keyboardButton() {
        this.addRenderableWidget(
            menuButton(this.width/2 - 133, this.height/2 + 45, (press) -> inventoryHandler(), COG, 24, false, 0, widget, true)
        );
    }

    public void inventoryHandler(){
        showInventory = !showInventory;
        switchVisibility();
        this.yScroll = 0;
        this.rebuildWidgets();
        this.selectedY = 0;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(isInHitbox(mouseX, mouseY)){
            windowMoveVertical(scrollY * 4);
        };
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(isInHitbox(mouseX, mouseY)){
            windowMoveVertical(dragY);
        };
        return true;
    }

    public boolean isInHitbox(double mouseX, double mouseY){
        var width = this.width/2;
        var height = this.height/2;
        var widthOffset = 100;
        var heightOffset = 115;
        var widthFrom = width - widthOffset;
        var heightFrom = height - heightOffset;
        var widthTo = width + widthOffset;
        var heightTo = height + heightOffset;
        return mouseX > widthFrom && mouseX < widthTo && mouseY > heightFrom + 50 && mouseY < heightTo - (showInventory ?  120 : 5);
    }

    private void windowMoveVertical(double dragY) {
        int size = getComponents(item)
            .stream()
            .filter(component -> component.getString().contains("|"))
            .toList()
            .size();
        if (size > 6 || size > 2 && showInventory) {
            int b = 7 * size * size - 135 * size + 578;
            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, b + (!showInventory ? 0 : -120)));
            this.rebuildWidgets();
            this.selectedY = 0;
        } else this.yScroll = 0;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.setCustomBackground(guiGraphics);
        selectedBoxUpgrade(guiGraphics, mouseX, mouseY);
        selectedBox(guiGraphics, mouseX, mouseY);

        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        guiGraphics.disableScissor();
        header(guiGraphics, mouseX, mouseY, pPartialTick);

        abilityIcon(guiGraphics, this.augmentModificationMenu.getAugmentEntity().inputItemHandler.getStackInSlot(0), this.width - 155, this.height - 180, 109, 40);
        guiGraphics.pose().popPose();
        guiGraphics.pose().translate(0,0,10);
        var adjustX = 18;
        var adjustY = -27;
        int startX = this.width / 2 - 140;
        int startY = this.height/2 + 22;
        if(showInventory){
            boxMaker(guiGraphics, startX + 140, startY + 38, 100, 55, BORDER_COLOUR);
            boxMaker(guiGraphics, startX + 140, startY + 38, 100, 55, BORDER_COLOUR);
            renderInventoryBackground(guiGraphics, this, 256, 24);
        }
        guiGraphics.pose().pushPose();
        boxMaker(guiGraphics, startX + adjustX, startY + adjustY, 19, 50, BORDER_COLOUR);
        Result result = new Result(adjustX, adjustY);
        var spacer = new AtomicInteger();
        for(ResourceLocation location : getOverlays()){
            guiGraphics.blit(GUI_ITEM_SLOT, this.width/2 - 156 + result.adjustX(), (this.height/2) + spacer.get() - 23 + result.adjustY(), 0,0,32,32,32,32);
            guiGraphics.blit(location, this.width/2 - 156 + result.adjustX(), (this.height/2) + spacer.get() - 23 + result.adjustY(), 0,0,32,32,32,32);
            spacer.set(spacer.get() + 30);
        }
    }

    private record Result(int adjustX, int adjustY) {
    }

    private List<ResourceLocation> getOverlays(){
        return List.of(CORE, ADVANCED_AUGMENT_CORE, AUGMENT_HYPER_CORE);
    }

    private void selectedBox(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(this.isInHitbox(mouseX, mouseY)){
            if(this.selectedY > 0) {
                boxMaker(guiGraphics, this.width/2, (int) (this.selectedY + 22 + yScroll), 97, 14, getAbstractElement(entity()).textColourSecondary());
            }
        }
    }

    private void selectedBoxUpgrade(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(this.isInHitbox(mouseX, mouseY)){
            if (this.selectedY > 0) {
                int startX = this.width / 2 + 136;
                int startY = this.selectedY + 22;
                boxMaker(guiGraphics, startX, (int) (startY + yScroll), 35, 14, getAbstractElement(entity()).textColourSecondary());
                guiGraphics.drawCenteredString(this.font, this.upgradeValue, startX, (int) (startY - 4 + yScroll), 0);
            }
        }

    }

    private void header(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        var yOff = 115;
        int xOff = this.width/2 - 105;
        textRenderable(xOff, (this.height/2 - (yOff - 10)), getComponents(item).getFirst(), this.getMinecraft()).render(guiGraphics, mouseX, mouseY, pPartialTick);
        textRenderable(xOff, (this.height/2 - yOff), AugmentItemHelper.getHoverName(item), this.getMinecraft()).render(guiGraphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
