package org.jahdoo.client.gui.modular_chaos_cube;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.all_abilities.ability_components.AbstractBlockAbility;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.capabilities.player_abilities.ModularChaosCubeProperties;
import org.jahdoo.client.gui.ToggleComponent;
import org.jahdoo.registers.AbilityRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity.AUGMENT_SLOT;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.gui.IconLocations.*;
import static org.jahdoo.client.gui.ToggleComponent.*;
import static org.jahdoo.client.gui.modular_chaos_cube.ModularChaosCubeData.selectDirection;
import static org.jahdoo.items.augments.AugmentItemHelper.*;
import static org.jahdoo.registers.AttachmentRegister.MODULAR_CHAOS_CUBE;

public class ModularChaosCubeScreen extends AbstractContainerScreen<ModularChaosCubeMenu> {
    private static final int IMAGE_SIZE = 256;
    private final ModularChaosCubeMenu modularChaosCubeMenu;
    private boolean input;
    private boolean output;


    public ModularChaosCubeScreen(ModularChaosCubeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.modularChaosCubeMenu = pMenu;
    }

    @Override
    protected void init() {
        super.init();
        int posX = this.width / 2;
        int posY = this.height / 2 ;
        this.modifyAugmentProperties(posX, posY);
        buildCarouselComponent(posX - 70, posY - 100, "Speed");
        var autoBlock = entity().getData(MODULAR_CHAOS_CUBE);
        direction(posX, posY, this.input, "Insert", (button) -> selectDirection(entity(), autoBlock.updateInput(button)), () -> this.input = !input, entity().getData(MODULAR_CHAOS_CUBE).input());
        direction(posX, posY, this.output, "Eject", (button) -> selectDirection(entity(), autoBlock.updateOutput(button)), () -> this.output = !output, entity().getData(MODULAR_CHAOS_CUBE).output());
        selectDirectionActive(posX, posY);
        entity().setChanged();
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public ModularChaosCubeEntity entity(){
        return this.modularChaosCubeMenu.getAutomationEntity();
    }

    private void modifyAugmentProperties(int posX, int posY){
        var currentPower = ModularChaosCubeProperties.getActive(entity());
        var chained = ModularChaosCubeProperties.getChained(entity());
        int size = 16;
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 76, posY - 112, (press) -> togglePower(entity()), currentPower ? POWER_OFF : POWER_ON, 20, 0));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 56, posY - 112, (press) -> toggleChained(entity()), chained ? CHAINED : UNCHAINED, 20, 0));

        if(!this.entity().inputItemHandler.getStackInSlot(0).isEmpty() && isValidAugmentUtil(entity().inputItemHandler.getStackInSlot(AUGMENT_SLOT)).isPresent()){
            this.addRenderableWidget(ToggleComponent.menuButton(posX + 14, posY - 23, (press) -> setModifyAugmentScreen(entity().inputItemHandler.getStackInSlot(0).copy()), COG, "", size));
        }
    }

    private void togglePower(ModularChaosCubeEntity entity){
        ModularChaosCubeData.togglePower(entity);
        entity.activateConnectedBlocks();
        this.rebuildWidgets();
    }

    private void toggleChained(ModularChaosCubeEntity entity){
        ModularChaosCubeData.toggleChained(entity);
        this.rebuildWidgets();
    }

    private void selectDirectionActive(int posX, int posY){
        var autoBlock = entity().getData(MODULAR_CHAOS_CUBE);
        buildDirectionWidgets(posX - 109, posY - 100, "Direction", entity().direction(), (button) -> selectDirection(entity(), autoBlock.updateActionDirection(button)), entity().getData(MODULAR_CHAOS_CUBE).action());
    }

    private void direction(int posX, int posY, boolean isInput, String label, Consumer<BlockPos> buttons, Runnable switchB, BlockPos blockPos) {
        isContainerAccessor(entity().augmentSlot()).ifPresent(
            accessor -> {
                var isInsert = Objects.equals(label, "Insert");
                if(isInsert ? accessor.isInputUser() : accessor.isOutputUser()){
                    if(isInput) buildDirectionWidgets(posX + 94, posY - 68, label, entity().direction(), buttons, blockPos);
                    this.addRenderableWidget(
                        menuButton(posX + 87, posY - 85, (press) -> extendMenu(switchB), isInsert ? DIRECTION_ARROW_BACK : DIRECTION_ARROW_FORWARD, "", 26)
                    );
                }
            }
        );
    }

    private void extendMenu(Runnable switchB){
        switchB.run();
        this.rebuildWidgets();
    }

    public static Optional<AbstractBlockAbility> isContainerAccessor(ItemStack itemStack){
        var get = AbilityRegister.getFirstSpellFromAugment(itemStack);
        if(get.isPresent() && get.get() instanceof AbstractBlockAbility accessor){
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
                    buttonX, buttonY, (press) -> directionWidget(posConsumer, button), button.getFirst(), 18,
                    isThis.equals(button.getSecond()), 0, new WidgetSprites(GUI_BUTTON, GUI_BUTTON)
                )
            );
        }
    }

    private void directionWidget(Consumer<BlockPos> posConsumer, Pair<ResourceLocation, BlockPos> button) {
        posConsumer.accept(button.getSecond());
        this.rebuildWidgets();
    }

    private void setModifyAugmentScreen(ItemStack itemStack){
        setAugmentModificationScreen(itemStack, this);
    }

    private void increaseSpeed(){
        var autoBlock = entity().getData(MODULAR_CHAOS_CUBE);
        if(autoBlock.speed() < 100){
            selectDirection(entity(), autoBlock.updateSpeed(autoBlock.speed() + 5));
            this.rebuildWidgets();
        }
    }

    private void decreaseSpeed(){
        var autoBlock = entity().getData(MODULAR_CHAOS_CUBE);
        if(autoBlock.speed() > 5) {
            selectDirection(entity(), autoBlock.updateSpeed(autoBlock.speed() - 5));
            this.rebuildWidgets();
        }
    }

    public void buildCarouselComponent(
        int posX, int posY,
        String label
    ){
        var widget = new WidgetSprites(BLANK, BLANK);
        this.addRenderableOnly(textWithBackground(posX + 22, posY, Component.literal(String.valueOf(ModularChaosCubeProperties.getSpeed(entity()))), this.getMinecraft(), Component.literal(label)));
        this.addRenderableWidget(menuButton(posX + 41, posY + 6, (press) -> decreaseSpeed(), DIRECTION_ARROW_BACK, 20, false,8, widget, false));
        this.addRenderableWidget(menuButton(posX + 79, posY + 6, (press) -> increaseSpeed(), DIRECTION_ARROW_FORWARD,  20,  false, 8, widget, false));
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

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        this.setCustomBackground(guiGraphics);
        int i = this.width / 2;
        int i1 = this.height / 2;
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        abilityIcon(guiGraphics, this.modularChaosCubeMenu.getAutomationEntity().inputItemHandler.getStackInSlot(0), width, height - 24, 109, 50);
        renderInventoryBackground(guiGraphics, this, IMAGE_SIZE, 24);
        setSlotTexture(guiGraphics, i - 16, i1 - 61, 32, "");
    }


    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
