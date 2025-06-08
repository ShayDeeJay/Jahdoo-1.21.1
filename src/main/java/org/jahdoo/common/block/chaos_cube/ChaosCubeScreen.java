package org.jahdoo.common.block.chaos_cube;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.AbstractBlockAbility;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Maths;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static org.jahdoo.common.block.chaos_cube.ChaosCubeHelpers.selectDirection;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.button.ToggleComponent.menuButton;
import static org.jahdoo.common.client.button.ToggleComponent.textWithBackground;
import static org.jahdoo.common.registers.AttachmentReg.MODULAR_CHAOS_CUBE;
import static org.jahdoo.trial_nexus.attachments.ChaosCubeData.*;
import static org.jahdoo.trial_nexus.utils.ColourStore.BORDER_COLOUR;
import static org.jahdoo.trial_nexus.utils.ColourStore.BOX_COLOUR;
import static org.jahdoo.trial_nexus.utils.Helpers.colourByPercent;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;

public class ChaosCubeScreen extends AbstractContainerScreen<ChaosCubeMenu> {

    private static final int IMAGE_SIZE = 256;
    private final ChaosCubeMenu modularChaosCubeMenu;
    private boolean input;
    private boolean output;

    public ChaosCubeScreen(ChaosCubeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.modularChaosCubeMenu = menu;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partial, int mouseX, int mouseY) {}

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public ChaosCubeEntity entity(){
        return this.modularChaosCubeMenu.getAutomationEntity();
    }

    private void toggleChained(ChaosCubeEntity entity){
        ChaosCubeHelpers.toggleChained(entity);
        this.rebuildWidgets();
    }

    private void selectDirectionActive(int posX, int posY){
        var autoBlock = entity().getData(MODULAR_CHAOS_CUBE);
        buildDirectionWidgets(posX - 109, posY - 100, "Direction", entity().direction(), (button) -> selectDirection(entity(), autoBlock.updateActionDirection(button)),autoBlock.action());
    }

    private void extendMenu(Runnable switchB){
        switchB.run();
        this.rebuildWidgets();
    }

    private void directionWidget(Consumer<BlockPos> posConsumer, Pair<ResourceLocation, BlockPos> button) {
        posConsumer.accept(button.getSecond());
        this.rebuildWidgets();
    }

    public static Optional<AbstractBlockAbility> isContainerAccessor(String id){
        var get = AbilityReg.getFirstSpellByTypeId(id);
        if(get.isPresent() && get.get() instanceof AbstractBlockAbility accessor){
            return Optional.of(accessor);
        }
        return Optional.empty();
    }

    private void togglePower(ChaosCubeEntity entity){
        ChaosCubeHelpers.togglePower(entity);
        entity.activateConnectedBlocks();
        this.rebuildWidgets();
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

    public void buildCarouselComponent(int posX, int posY, String label){
        var widget = new WidgetSprites(BLANK, BLANK);
        this.addRenderableOnly(textWithBackground(posX + 22, posY, Component.literal(String.valueOf(getSpeed(entity()))), this.getMinecraft(), Component.literal(label)));
        var size = 40;
        var i = size / 4;
        var setX = posX - i;
        var setY = posY - i;
        this.addRenderableWidget(menuButton(setX + 41, setY + 6, (press) -> decreaseSpeed(), DIRECTION_ARROW_BACK, size, false,8, widget, false));
        this.addRenderableWidget(menuButton(setX + 79, setY + 6, (press) -> increaseSpeed(), DIRECTION_ARROW_FORWARD, size,  false, 8, widget, false));
    }

    private void modifyAugmentProperties(int posX, int posY){
        var currentPower = getActive(entity());
        var chained = getChained(entity());

        var power = "Power: " + (currentPower ? "On" : "Off");
        var linked = "Linkage: " + (chained ? "On" : "Off");

        this.addRenderableWidget(menuButton(posX + 76, posY - 112, (press) -> togglePower(entity()), currentPower ? POWER_ON : POWER_OFF, 20, 0, power));
        this.addRenderableWidget(menuButton(posX + 56, posY - 112, (press) -> toggleChained(entity()), chained ? CHAINED : UNCHAINED, 20, 0, linked));

    }

    private void direction(int posX, int posY, boolean isInput, String label, Consumer<BlockPos> buttons, Runnable switchB, BlockPos blockPos) {
        isContainerAccessor(entity().getHolder().abilityName()).ifPresent(
            accessor -> {
                var isInsert = Objects.equals(label, "Insert");
                if(isInsert ? accessor.isInputUser() : accessor.isOutputUser()){
                    var sharedY = posY + 4;
                    var sharedX = posX + 4;
                    if(isInput) buildDirectionWidgets(sharedX + 94, sharedY - 68, label, entity().direction(), buttons, blockPos);
                    this.addRenderableWidget(
                        menuButton(posX + 87, posY - 52, (press) -> extendMenu(switchB), isInsert ? DIRECTION_ARROW_BACK : DIRECTION_ARROW_FORWARD, "Eject", 24)
                    );

                    this.addRenderableOnly(
                        new Overlay() {
                            @Override
                            public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                                var pose = guiGraphics.pose();
                                pose.pushPose();
                                pose.translate(0,0,-1);
                                if(isInput){
                                    SharedUI.boxMaker(
                                        guiGraphics,
                                        sharedX + 106, sharedY - 80, 26, 34,
                                        BORDER_COLOUR,
                                        fadeBlack(0.5F)
                                    );
                                }
                                pose.popPose();
                            }
                        }
                    );
                }
            }
        );
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
    protected void init() {
        super.init();
        var posX = this.width / 2;
        var posY = this.height / 2 ;
        this.modifyAugmentProperties(posX, posY);
        buildCarouselComponent(posX - 70, posY - 100, "Speed");
        var autoBlock = entity().getData(MODULAR_CHAOS_CUBE);

        direction(posX, posY, this.input, "Insert",
            (button) -> selectDirection(entity(), autoBlock.updateInput(button)),
            () -> this.input = !input, entity().getData(MODULAR_CHAOS_CUBE).input()
        );

        direction(posX, posY, this.output, "Eject",
            (button) -> selectDirection(entity(), autoBlock.updateOutput(button)),
            () -> this.output = !output, entity().getData(MODULAR_CHAOS_CUBE).output()
        );

        selectDirectionActive(posX, posY);
        entity().setChanged();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        this.setCustomBackground(guiGraphics);
        var i = this.width / 2;
        var i1 = this.height / 2;
        var fillColour = -15020954;
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        var offsetX = 220;
        var offsetY = -38;

        renderTankFill(guiGraphics, i, offsetX, i1, offsetY, fillColour, mouseX, mouseY);

        abilityIcon(guiGraphics, this.entity().getHolder().abilityName(), width, height - 44, 109, 50, 12);
        renderInventoryBackground(guiGraphics, this, IMAGE_SIZE, 24, true);
    }

    private void renderTankFill(@NotNull GuiGraphics guiGraphics, int i, int offsetX, int i1, int offsetY, int fillColour, int mouseX, int mouseY) {
        var tanks = entity().getTankEntity();
        if(tanks != null){
            var xSet = i - 50;
            var ySet = i1 - 8;
            var startX = xSet - 150 + offsetX;
            var startY = ySet + offsetY + 2;
            var barWidth = 6;
            var barHeight = 19;
            var fromX = mouseX > startX;
            var toX = mouseX < (startX + (barWidth * 2));
            var fromY = mouseY > startY;
            var toY = mouseY < (startY + (barHeight * 2));

            if(fromX && toX && fromY && toY) {
                var tooltipLines = new ArrayList<Component>();
                var tooltipLines2 = new ArrayList<Component>();
                var current = tanks.getCount();
                var max = tanks.getMaxSlotSizeInput();
                var tracker = current + "/" + max;
                var colour = colourByPercent(max, current, true);
                var colourSet = ElementReg.utility().partColourB();

                if(entity().getHolder() != null){
                    var abilityModifiers = entity().getHolder().data().abilityProperties().get(AbilityBuilder.MANA_COST);

                    if(abilityModifiers != null){
                        var operationCost = abilityModifiers.setValue();
                        var roundCost = Maths.roundNonWholeString(operationCost);
                        tooltipLines2.add(withStyleComponent("Cost: ", colourSet).copy().append(withStyleComponent(roundCost, ColourStore.SUB_HEADER_COLOUR)));
                    }
                }
                tooltipLines.add(withStyleComponent("Tank: ", colourSet).copy().append(withStyleComponent(tracker, colour)));
                guiGraphics.renderTooltip(font, tooltipLines, Optional.empty(), mouseX, mouseY + 4);
                guiGraphics.renderTooltip(font, tooltipLines2, Optional.empty(), mouseX, mouseY + 21);
            }

            SharedUI.boxMaker(
                guiGraphics,
                startX,
                startY,
                barWidth,
                barHeight,
                BORDER_COLOUR,
                BOX_COLOUR
            );

            int heightOffset = (int) ((float) (barHeight - 2) / 64 * this.entity().getNexiteCount());
            if(heightOffset >= 1){
                SharedUI.boxMaker(
                    guiGraphics,
                    xSet - 148 + offsetX,
                    ySet + 37 + offsetY,
                    4,
                    -heightOffset+1,
                    fillColour,
                    fillColour
                );
            }
        }
    }

    public void buildDirectionWidgets(
        int posX,
        int posY,
        String label,
        List<Pair<ResourceLocation, BlockPos>> copy,
        Consumer<BlockPos> posConsumer,
        BlockPos isThis
    ) {
        this.addRenderableOnly(textWithBackground(posX-10, posY, this.getMinecraft(), Component.literal(label)));
        var modifiableCopy = new ArrayList<>(copy);

        int[][] layoutPositions = {
            {28}, {14, 28, 42}, {28, 42}
        };

        int[] rowOffsets = {0, 14, 28};
        for (int i = 0; i < modifiableCopy.size(); i++) {
            var row = (i == 0) ? 0 : (i < 4 ? 1 : 2);
            var column = (row == 0) ? 0 : (i - (row == 1 ? 1 : 4));
            var buttonX = posX + layoutPositions[row][column];
            var buttonY = posY + 4 + rowOffsets[row];
            var button = modifiableCopy.get(i);

            this.addRenderableWidget(
                menuButton(
                    buttonX, buttonY, (press) -> directionWidget(posConsumer, button), button.getFirst(), 20,
                    isThis.equals(button.getSecond()), 0, new WidgetSprites(GUI_BUTTON, GUI_BUTTON)
                )
            );
        }
    }
}
