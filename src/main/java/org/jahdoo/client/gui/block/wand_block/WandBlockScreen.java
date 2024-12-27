package org.jahdoo.client.gui.block.wand_block;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.client.SharedUI;
import org.jahdoo.client.gui.block.augment_modification_station.InventorySlots;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.IconLocations.*;
import static org.jahdoo.client.IconLocations.INFORMATION;
import static org.jahdoo.client.gui.ToggleComponent.menuButton;
import static org.jahdoo.client.gui.ToggleComponent.textRenderable;
import static org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationScreen.WIDGET;
import static org.jahdoo.items.augments.AugmentItemHelper.getAllAbilityModifiers;
import static org.jahdoo.items.augments.AugmentItemHelper.shiftForDetails;

public class WandBlockScreen extends AbstractContainerScreen<WandBlockMenu> {
    private final long window = Minecraft.getInstance().getWindow().getWindow();
    private final Options settings = Minecraft.getInstance().options;
    private static final int IMAGE_SIZE = 256;
    private final WandBlockMenu wandBlockMenu;
    private ItemStack cachedItem;
    boolean showInventory;
    private double yScroll;

    public WandBlockScreen(WandBlockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.wandBlockMenu = pMenu;
        this.width = 50;
        this.height = 50;
        this.showInventory = true;
    }

    @Override
    protected void init() {
        super.init();
        inventorySlider();
    }

    private List<AbilityRegistrar> getAbilityFromRegistry(ItemStack comparable){
        return AbilityRegister.getSpellsByTypeId(DataComponentHelper.getAbilityTypeItemStack(comparable));
    }

    private ItemStack getMatchingItem(ItemStack comparable){
        for(int i = 1; i < this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getSlots(); i++){
            var currentAugment = this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getStackInSlot(i);
            var selectedAbility = this.getAbilityFromRegistry(comparable);
            if(selectedAbility.isEmpty()) return ItemStack.EMPTY;
            var wandAbilityHolder = currentAugment.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            if (wandAbilityHolder != null && wandAbilityHolder.abilityProperties().containsKey(selectedAbility.getFirst().setAbilityId())) {
                return currentAugment;
            }
        }
        return ItemStack.EMPTY;
    }

    private int getMatchingIndex(ItemStack comparable){
        if(comparable.is(ItemsRegister.AUGMENT_ITEM.get()) && DataComponentHelper.hasWandAbilitiesTag(comparable)){
            for (int i = 1; i < this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getSlots(); i++) {
                var currentAugment = this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getStackInSlot(i);
                var ability = this.getAbilityFromRegistry(comparable);
                var currentAbility = this.getAbilityFromRegistry(currentAugment);

                if(!ability.isEmpty() && !currentAbility.isEmpty()){
                    var wandAbilityHolder = currentAugment.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
                    if (wandAbilityHolder.abilityProperties().containsKey(ability.getFirst().setAbilityId())) {
                        return i - 1;
                    }
                }
            }
        }
        return -1;
    }

    private void inventorySlider() {
        this.addRenderableWidget(
            menuButton(this.width/2 + 96, this.height/2 + 45, (press) -> {
                this.showInventory = !this.showInventory;
                this.switchVisibility();
            }, this.showInventory ? INFORMATION : INVENTORY, 24, false, 0, WIDGET, true)
        );
    }

    public  List<Component> getHoverText(ItemStack itemStack, ItemStack itemStack1){
        var toolTips = new ArrayList<Component>();
        if(itemStack.getComponents().isEmpty()) return toolTips;
        var abilityLocation = DataComponentHelper.getAbilityTypeItemStack(itemStack);
        var getElement = itemStack.get(CUSTOM_MODEL_DATA);

        if(getElement != null){
            var info = ElementRegistry.getElementByTypeId(Math.max(getElement.value(), 0));
            if (itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()) != null && !info.isEmpty()) {
                toolTips.add(AugmentItemHelper.getAbilityName(itemStack, info.getFirst()));
                toolTips.addAll(getAllAbilityModifiers(itemStack, itemStack1, abilityLocation, false));
                shiftForDetails(toolTips);
            }
        }
        return toolTips;
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int pX, int pY) {
        if(this.hoveredSlot == null) return;
        if(this.hoveredSlot.getItem().getItem() instanceof Augment) {
            if(this.wandBlockMenu.getCarried().isEmpty()){
                this.cachedItem = this.hoveredSlot.getItem();
                this.yScroll = 0;
            }
        }
        var matchedItem = this.getMatchingItem(this.hoveredSlot.getItem());
        var getTooltipMatched = getHoverText(matchedItem, this.hoveredSlot.getItem());
        var getTooltipHovered = getHoverText(this.hoveredSlot.getItem(), matchedItem);
        var xOffset = (this.getMaxLengthItem(getTooltipMatched) * 5) + 20;
        var keyDown = InputConstants.isKeyDown(window, settings.keyShift.getKey().getValue());

        if(getTooltipMatched.isEmpty() || this.hoveredSlot.index >= 36) {
            super.renderTooltip(guiGraphics, pX, pY + 20);
            return;
        }

        if(!matchedItem.isEmpty() && !keyDown){
            var colour =  ChatFormatting.DARK_GRAY.getColor();
            getTooltipHovered.add(ModHelpers.withStyleComponentTrans("blocks.jahdoo.screen.wand.compare", colour));
        }

        guiGraphics.renderTooltip(
            this.font,
            getTooltipHovered,
            this.hoveredSlot.getItem().getTooltipImage(),
            this.hoveredSlot.getItem(),
            this.hoveredSlot.index > 41 ? pX - xOffset : pX,
            pY + 20
        );

        var isValidItem = this.hoveredSlot.getItem().is(ItemsRegister.AUGMENT_ITEM.get());
        var isValidSlot = this.hoveredSlot.index < 36;
        if (!isValidItem || !isValidSlot) return;

        if (!matchedItem.isEmpty() && keyDown) {
            getTooltipMatched.add(1, Component.literal("Equipped").withStyle(style -> style.withColor(-8660735)));

            guiGraphics.renderTooltip(
                this.font,
                getTooltipMatched,
                matchedItem.getTooltipImage(),
                matchedItem,
                pX - ((this.getMaxLengthItem(getTooltipMatched) * 5) + 20),
                pY + 20
            );
        }
    }

    private int getMaxLengthItem(List<Component> components){
        var getMaxLength = new AtomicInteger();
        components.forEach(
            component -> {
                boolean b = component.getString().length() > getMaxLength.get();
                if(b) getMaxLength.set(component.getString().length());
            }
        );
        return getMaxLength.get();
    }

    private void setSlotTexturesGrid(GuiGraphics guiGraphics){
        var entity = this.wandBlockMenu.getWandBlockEntity();

        SharedUI.handleSlotsInGridLayout(
            (slotX, slotY, index) -> {
                int slotX1 = slotX - 32 / 2;
                int slotY1 = slotY - 32 / 2;
                adjustAllSlotRelated(guiGraphics, slotX - 96, slotY - 114 + wandBlockMenu.slotsY, index, slotX1, slotY1);
            },
            entity.getAllowedSlots(),
            this.width,
            this.height,
            wandBlockMenu.xSpacing,
            wandBlockMenu.ySpacing
        );
    }

    private void adjustAllSlotRelated(GuiGraphics guiGraphics, int slotX, int slotY, int i, int slotX1, int slotY1){
        var adjustGroupY = -1;
        setSlotTexture(
            guiGraphics,
            slotX + wandBlockMenu.xOffset,
          slotY + wandBlockMenu.yOffset + adjustGroupY,
            32,
            String.valueOf(i + 1)
        );

        this.setSlotHoveredBorder(
            guiGraphics,
            slotX + wandBlockMenu.xOffset,
            slotY + wandBlockMenu.yOffset + adjustGroupY,
            slotX1, slotY1, i
        );
    }

    private void setSlotHoveredBorder(GuiGraphics guiGraphics, int x, int y, int slotX1, int slotY1, int i){
        var conditionOne = this.hoveredSlot != null && this.hoveredSlot.index - 36 == i ;
        var conditionTwo = this.hoveredSlot != null && this.getMatchingIndex(this.hoveredSlot.getItem()) == i;
        var isKeyDown = InputConstants.isKeyDown(window, settings.keyShift.getKey().getValue());

        if(conditionOne || conditionTwo){
            if(conditionTwo && isKeyDown || conditionOne) {
                setTypeOverlay(guiGraphics, x + 2, y - 20);
            }
            if(this.hoveredSlot.index < 36){
                var imageWidth = 32;
                var imageHeight = 32;

                var slotX = this.hoveredSlot.x;
                var slotY = this.hoveredSlot.y;

                var drawX = slotX + (width / 2) - (imageWidth / 2);
                var drawY = slotY + (height / 2) - (imageHeight / 2);

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0,0,280);

                guiGraphics.blit(
                    ModHelpers.res("textures/gui/in_wand.png"),
                    slotX1 - 80,
                    slotY1 - 68 ,
                    0, 0, imageWidth, imageHeight, imageWidth, imageHeight
                );

                guiGraphics.blit(
                    ModHelpers.res("textures/gui/in_inventory.png"),
                    drawX - 80,
                    drawY - 83 ,
                    0, 0, imageWidth, imageHeight, imageWidth, imageHeight
                );

                guiGraphics.pose().popPose();
            }
        }
    }

    public void setTypeOverlayInventory(GuiGraphics guiGraphics, int positionX, int positionY){
        if(this.hoveredSlot != null && this.hoveredSlot.getItem().getItem() instanceof Augment){
            var type = this.hoveredSlot.getItem();
            if (type.has(CUSTOM_MODEL_DATA)) {
                var types = type.get(CUSTOM_MODEL_DATA).value();
                if (types > 0 && this.showInventory) {
                    int[] typeOverlay = {353, 441, 265, 177, 89, 1};
                    hoveredOverlayInventory(guiGraphics, positionX, positionY, typeOverlay[types - 1]);
                }
            }
        }
    }

    public void setTypeOverlay(GuiGraphics guiGraphics, int positionX, int positionY){
        var item = this.hoveredSlot;
        if(item != null && item.getItem().getItem() instanceof Augment){
            var type = item.getItem();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0,0,1);
            SharedUI.boxMaker(guiGraphics, positionX + 1, positionY + 21, 13, 15, -1);
            if (type.has(CUSTOM_MODEL_DATA)) {
                var types = type.get(CUSTOM_MODEL_DATA).value();
                if (types > 0) {
                    int[] typeOverlay = {129, 161, 97, 65, 33, 1};
                    hoveredOverlay(guiGraphics, positionX, positionY, typeOverlay[types - 1]);
                }
            } else {
                hoveredOverlay(guiGraphics, positionX, positionY, 193);
            }
            guiGraphics.pose().popPose();
        }
    }

    public static void hoveredOverlayInventory(GuiGraphics guiGraphics, int positionX, int positionY, int offset) {
        guiGraphics.blit(GUI_INVENTORY_OVERLAY, positionX + 41, positionY + 144, 0,1, offset, 174, 88, 176, 530);
    }

    private static void hoveredOverlay(GuiGraphics guiGraphics, int positionX, int positionY, int offset) {
        guiGraphics.blit(HOVERED_SLOT_OVERLAY, positionX, positionY + 20, 0,1, offset, 28, 32, 30, 226);
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
        var fromColour = -1072689136;
        var toColour = -804253680;
        var borderColour = -10066330;

        guiGraphics.fillGradient(widthFrom, heightFrom, widthTo, heightTo, fromColour, toColour);
        guiGraphics.renderOutline(widthFrom, heightFrom, widthTo - widthFrom, heightTo - heightFrom, borderColour);
        guiGraphics.hLine(width-100, width + 99, this.height/2 - 70, borderColour);
        guiGraphics.hLine(width-100, width + 99, this.height/2 + 4, borderColour);

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        this.setCustomBackground(guiGraphics);
        if(this.cachedItem == null || this.cachedItem.isEmpty() && !this.wandBlockMenu.getCarried().isEmpty()){
            this.cachedItem = this.wandBlockMenu.getCarried();
        }
        sideBar(guiGraphics, mouseX, mouseY, pPartialTick);
        renderInventoryBackground(guiGraphics, this, IMAGE_SIZE, this.wandBlockMenu.yOffset, this.showInventory);
        header(guiGraphics, mouseX, mouseY, pPartialTick);
        abilityIcon(guiGraphics, cachedItem, width -152, height - 29, wandBlockMenu.yOffset + 10, 44);
        this.setSlotTexturesGrid(guiGraphics);
        setTypeOverlayInventory(guiGraphics, (width - IMAGE_SIZE) / 2, (height - IMAGE_SIZE) / 2);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void switchVisibility() {
        for (Slot slot : this.menu.slots) {
            if(slot instanceof InventorySlots inventorySlots){
                inventorySlots.setActive(showInventory);
                this.rebuildWidgets();
            }
        }
    }

    public List<String> wrapText() {
        List<String> lines = new ArrayList<>();
        if(this.cachedItem == null) return lines;
        int maxCharsPerLine = 34;
        Optional<AbilityRegistrar> first = this.getAbilityFromRegistry(this.cachedItem).stream().findFirst();
        if(first.isPresent()){
            String[] words = first.get().getDescription().split(" ");
            StringBuilder currentLine = new StringBuilder();
            for (String word : words) {
                if (currentLine.length() + word.length() > maxCharsPerLine) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                if (!currentLine.isEmpty()) currentLine.append(" ");
                currentLine.append(word);
            }
            if (!currentLine.isEmpty()) lines.add(currentLine.toString());
        }
        return lines;
    }


    private void sideBar(GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        if(this.showInventory) return;
        var height = this.height/2;
        var heightOffset = 115;
        var heightFrom = height - heightOffset;
        var heightTo = height + heightOffset;

        guiGraphics.enableScissor(0, heightFrom + 120, this.width, heightTo - 5);
        var adjustX = -198;
        var adjustY = 120;
        if(this.cachedItem != null && !this.cachedItem.isEmpty()){
            var components = SharedUI.getComponents(this.cachedItem);
            var subComponents = new ArrayList<Component>();
            subComponents.add(ModHelpers.withStyleComponent("Description: ", -23281));
            for (String s : wrapText()) subComponents.add(ModHelpers.withStyleComponent(s, -1));
            subComponents.add(Component.literal(" "))    ;
            subComponents.addAll(components.subList(2, components.size() - 2));
            var spacer = new AtomicInteger();

            for(Component component : subComponents){
                int posX = this.width / 2 + 60 + adjustX;
                textRenderable(posX, (int) (((double) this.height / 2 - 118 + spacer.get()) + adjustY + yScroll), component, this.getMinecraft()).render(guiGraphics, mouseX, mouseY, pPartialTick);
                spacer.set(spacer.get() + 10);
            }
        }
        guiGraphics.disableScissor();
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

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(isInHitbox(mouseX, mouseY)) windowMoveVertical(dragY);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(isInHitbox(mouseX, mouseY)) windowMoveVertical(scrollY * 4);
        return true;
    }

    private void windowMoveVertical(double dragY) {
        int size = getComponents(this.cachedItem)
            .stream()
            .filter(component -> component.getString().contains("|"))
            .toList()
            .size();
        int b = 7 * size * size * this.wrapText().size();
        this.yScroll = Math.min(0, this.yScroll + dragY);
        this.rebuildWidgets();
    }

    private void header(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        if(this.cachedItem != null){
            var yOff = 115;
            int xOff = this.width / 2 - 105;
            textRenderable(xOff, (this.height / 2 - (yOff - 10)), getComponents(cachedItem).getFirst(), this.getMinecraft()).render(guiGraphics, mouseX, mouseY, pPartialTick);
            textRenderable(xOff, (this.height / 2 - yOff), AugmentItemHelper.getHoverName(cachedItem), this.getMinecraft()).render(guiGraphics, mouseX, mouseY, pPartialTick);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
