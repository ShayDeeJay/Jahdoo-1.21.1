package org.jahdoo.client.gui.automation_block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.all_abilities.ability_components.AbstractContainerAccessor;
import org.jahdoo.block.automation_block.AutomationBlock;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.capabilities.player_abilities.AutoBlock;
import org.jahdoo.client.gui.ToggleComponent;
import org.jahdoo.registers.AbilityRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.jahdoo.block.automation_block.AutomationBlockEntity.AUGMENT_SLOT;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.gui.IconLocations.*;
import static org.jahdoo.client.gui.ToggleComponent.*;
import static org.jahdoo.client.gui.automation_block.AutomationBlockData.selectDirection;
import static org.jahdoo.items.augments.AugmentItemHelper.*;
import static org.jahdoo.registers.AttachmentRegister.AUTO_BLOCK;

public class AutomationBlockScreen extends AbstractContainerScreen<AutomationBlockMenu> {
    private static final int IMAGE_SIZE = 256;
    private final AutomationBlockMenu automationBlockMenu;

    public AutomationBlockScreen(AutomationBlockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.automationBlockMenu = pMenu;
    }

    @Override
    protected void init() {
        super.init();
        int posX = this.width / 2;
        int posY = this.height / 2 ;
        this.modifyAugmentProperties(posX, posY);
        buildCarouselComponent(posX - 70, posY - 100, "Speed");
        selectDirectionActive(posX, posY);
        selectDirectionInput(posX, posY);
        selectDirectionOutput(posX, posY);
        entity().setChanged();
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public AutomationBlockEntity entity(){
        return this.automationBlockMenu.getAutomationEntity();
    }

    private void modifyAugmentProperties(int posX, int posY){
        var currentPower = AutoBlock.getActive(entity());
        int size = 16;
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 76, posY - 110, (press) -> togglePower(entity()), currentPower ? POWER_OFF : POWER_ON, "", 20));
        if(!this.entity().inputItemHandler.getStackInSlot(0).isEmpty() && isValidAugmentUtil(entity().inputItemHandler.getStackInSlot(AUGMENT_SLOT)).isPresent()){
            this.addRenderableWidget(ToggleComponent.menuButton(posX + 14, posY - 11, (press) -> setModifyAugmentScreen(entity().inputItemHandler.getStackInSlot(0).copy()), COG, "", size));
        }
    }

    private void togglePower(AutomationBlockEntity entity){
        AutomationBlockData.togglePower(entity);
        this.rebuildWidgets();
    }

    private void selectDirectionActive(int posX, int posY){
        var autoBlock = entity().getData(AUTO_BLOCK);
        buildDirectionWidgets(posX - 109, posY - 100, "Direction", entity().direction(), (button) -> selectDirection(entity(), autoBlock.updateActionDirection(button)), entity().getData(AUTO_BLOCK).action());
    }


    private void selectDirectionInput(int posX, int posY){
        isContainerAccessor(entity().augmentSlot()).ifPresent(
            accessor -> {
                if(accessor.isInputUser()){
                    var autoBlock = entity().getData(AUTO_BLOCK);
                    buildDirectionWidgets(posX - 159, posY - 100, "Input", entity().direction(), (button) -> selectDirection(entity(), autoBlock.updateInput(button)), entity().getData(AUTO_BLOCK).input());
                }
            }
        );
    }

    private void selectDirectionOutput(int posX, int posY){
        isContainerAccessor(entity().augmentSlot()).ifPresent(
            accessor -> {
                if (accessor.isOutputUser()) {
                    var autoBlock = entity().getData(AUTO_BLOCK);
                    buildDirectionWidgets(posX - 60, posY - 100, "Eject", entity().direction(), (button) -> selectDirection(entity(), autoBlock.updateOutput(button)), entity().getData(AUTO_BLOCK).output());
                }
            }
        );
    }

    public static Optional<AbstractContainerAccessor> isContainerAccessor(ItemStack itemStack){
        var get = AbilityRegister.getFirstSpellFromAugment(itemStack);
        if(get.isPresent() && get.get() instanceof AbstractContainerAccessor accessor){
            return Optional.of(accessor);
        }
        return Optional.empty();
    }

    public void buildDirectionWidgets(
        int posX, int posY,
        String label,
        List<Pair<ResourceLocation, BlockPos>> copy,
        Consumer<BlockPos> posConsumer,
        BlockPos isThis
    ) {
        this.addRenderableOnly(textWithBackground(posX-10, posY, this.getMinecraft(), Component.literal(label)));
        List<Pair<ResourceLocation, BlockPos>> modifiableCopy = new ArrayList<>(copy);

        if (modifiableCopy.size() > 4) {
            Pair<ResourceLocation, BlockPos> temp = modifiableCopy.get(2);
            modifiableCopy.set(2, modifiableCopy.get(4));
            modifiableCopy.set(4, temp);
        }

        int[][] layoutPositions = {
                {28},
            {14, 28, 42},
                {28, 42}
        };

        int[] rowOffsets = {0, 14, 28};
        for (int i = 0; i < modifiableCopy.size(); i++) {
            int row = (i == 0) ? 0 : (i < 4 ? 1 : 2);
            int column = (row == 0) ? 0 : (i - (row == 1 ? 1 : 4));

            int buttonX = posX + layoutPositions[row][column];
            int buttonY = posY + 4 + rowOffsets[row];

            var button = modifiableCopy.get(i);

            this.addRenderableWidget(
                menuButton(
                    buttonX, buttonY,
                    (press) -> {
                        posConsumer.accept(button.getSecond());;
                        this.rebuildWidgets();
                    },
                    button.getFirst(), 18,
                    isThis.equals(button.getSecond()),
                    0
                )
            );
        }
    }

    private void setModifyAugmentScreen(ItemStack itemStack){
        setAugmentModificationScreen(itemStack, this);
    }

    private void increaseSpeed(){
        var autoBlock = entity().getData(AUTO_BLOCK);
        if(autoBlock.speed() < 100){
            selectDirection(entity(), autoBlock.updateSpeed(autoBlock.speed() + 5));
            this.rebuildWidgets();
        }
    }

    private void decreaseSpeed(){
        var autoBlock = entity().getData(AUTO_BLOCK);
        if(autoBlock.speed() > 5) {
            selectDirection(entity(), autoBlock.updateSpeed(autoBlock.speed() - 5));
            this.rebuildWidgets();
        }
    }

    public void buildCarouselComponent(
        int posX, int posY,
        String label
    ){
        this.addRenderableOnly(textWithBackground(posX + 22, posY, Component.literal(String.valueOf(AutoBlock.getSpeed(entity()))), this.getMinecraft(), Component.literal(label)));
        this.addRenderableWidget(menuButton(posX + 33, posY, (press) -> decreaseSpeed(), DIRECTION_ARROW_BACK));
        this.addRenderableWidget(menuButton(posX + 75, posY, (press) -> increaseSpeed(), DIRECTION_ARROW_FORWARD));
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
        var borderColour = -10066330;

        guiGraphics.fillGradient(widthFrom, heightFrom, widthTo, heightTo, fromColour, toColour);
        guiGraphics.renderOutline(widthFrom, heightFrom, widthTo - widthFrom, heightTo - heightFrom, borderColour);
    }

    public void renderSlotWithLabel(GuiGraphics guiGraphics, int i, int i1, String label){
        guiGraphics.drawCenteredString(this.font, Component.literal(label), i + 16, i1 - 8, -1);
        guiGraphics.blit(GUI_GENERAL_SLOT, i, i1, 0, 0, 32, 32,32, 32);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        this.setCustomBackground(guiGraphics);
        int i = this.width / 2;
        int i1 = this.height / 2;
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        abilityIcon(guiGraphics, this.automationBlockMenu.getAutomationEntity().inputItemHandler.getStackInSlot(0), width, height, 109);
        renderInventoryBackground(guiGraphics, this, IMAGE_SIZE, 24);
        setSlotTexture(guiGraphics, i - 128, i1 - 160, IMAGE_SIZE);
    }


    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
