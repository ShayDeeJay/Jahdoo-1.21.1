package org.jahdoo.common.block.augment_modification_station;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.screens.AbilityUnlockScreen;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.ElementReg;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static net.minecraft.sounds.SoundEvents.APPLY_EFFECT_TRIAL_OMEN;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;
import static org.jahdoo.common.block.augment_modification_station.AugmentModificationData.extractName;
import static org.jahdoo.common.block.augment_modification_station.AugmentModificationData.updateAugmentConfig;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.client.button.ToggleComponent.textRenderable;
import static org.jahdoo.common.items.augments.AugmentItemHelper.getModifierContextSingle;

public class AugmentModificationScreen extends Screen {

    public Inventory inventory;
    private Component upgradeValue;
    private double yScroll;
    private int selectedY;
    private final Ability ability;
    private AbilityHolder holder;
    public static WidgetSprites WIDGET = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);

    int size;
    double panX;
    double panY;
    double zoomX;
    double scaledSpacing;
    double scaledXOffset; // Scale X spacing
    double centerX; // Screen center
    double centerY; // Screen center

    public AugmentModificationScreen(
        AbilityHolder holder,
        Ability ability,
        int size,
        double panX,
        double panY,
        double zoomX,
        double scaledSpacing,
        double scaledXOffset,
        double centerX,
        double centerY
    ) {
        super(Component.empty());
        this.holder = holder;
        this.ability = ability;
        this.size = size;
        this.panX = panX;
        this.panY = panY;
        this.zoomX = zoomX;
        this.scaledSpacing = scaledSpacing;
        this.scaledXOffset = scaledXOffset;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    protected void init() {
        super.init();
        this.displayAugmentProperties();
    }

    @Override
    public void onClose() {
        getMinecraft().setScreen(new AbilityUnlockScreen(size, panX, panY, zoomX, scaledSpacing, scaledXOffset, centerX, centerY));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void displayAugmentProperties(){
        if(holder == null) return;

        var spacer = new AtomicInteger();
        var width = this.width / 2;
        var components = componentsWithBounds(ability, holder, this.getMinecraft().level);

        for (Component comp : components){
            var mod = getAbilityModifiers(comp, this.holder);

            if(mod.highestValue() != -1){
                var regex = ".*\\d.*";
                var ySpacer = (this.height / 2 - 106) + spacer.get();
                var selectedY1 = (int) (ySpacer + this.yScroll);

                buildPropertiesWithHighlight(comp, width, selectedY1);

                if (Pattern.matches(regex, comp.getString()) && !comp.getString().contains(")")) {
                    var correctAdjustment = mod.isHigherBetter() ? mod.actualValue() == mod.highestValue() : mod.actualValue() == mod.lowestValue();
                    var nexUpgrade = mod.isHigherBetter() ? mod.actualValue() + mod.step() == mod.highestValue() : mod.actualValue() - mod.step() == mod.lowestValue();

                    upgradeButton(comp, width, ySpacer, nexUpgrade, correctAdjustment, mod);
                }

                spacer.set(spacer.get() + (comp.getString().contains(")") || !Pattern.matches(regex, comp.getString()) ? 17 : 10));
            }
        }
    }

    public static List<Component> componentsWithBounds(Ability ability, AbilityHolder holder, Level level){
        var components = getComponents(ability, holder, level);
        var compNew = components
            .subList(1, components.size()).stream().filter(component -> getAbilityModifiers(component, holder).highestValue() != -1)
            .filter(component -> !component.equals(Component.literal(" ")) && !component.getString().contains("Unique"));
        return compNew.toList();
    }

    private void buildPropertiesWithHighlight(Component component, int width, int selectedY1) {
        var rebuild = Component.empty();
        if (component.getString().contains("|")) {
            for (Component component1 : component.toFlatList().subList(0, 2)) rebuild.append(component1);
            rebuild.append(Helpers.withStyleComponent(component.toFlatList().getLast().getString(), -1129857));
            this.addRenderableOnly(textRenderable(width - 139, selectedY1, rebuild, this.getMinecraft()));
        } else {
            this.addRenderableOnly(textRenderable(width - 139, selectedY1, component, this.getMinecraft()));
        }
    }

    private void upgradeButton(Component component, int width, int ySpacer, boolean nexUpgrade, boolean correctAdjustment, AbilityData.AbilityModifiers x) {
        int posX = width + 72;
        int posY = (int) (ySpacer + 11 + this.yScroll);
        this.addRenderableWidget(
            menuButtonSound(
                posX, posY,
                (press) -> doOnClick(component, holder, nexUpgrade, posX, posY),
                correctAdjustment  ? UPGRADE_DISABLED : UPGRADE, 22,
                correctAdjustment || !this.isInHitbox(posX, posY) ,
                correctAdjustment  ? 0 : 8, WIDGET,
                !correctAdjustment  && this.isInHitbox(posX, posY),
                () -> {
                    var getHighest = x.isHigherBetter() ? x.actualValue() + x.step() : x.actualValue() - x.step();
                    var original = getModifierContextSingle(extractName(component.getString()), String.valueOf(doubleFormattedDouble(getHighest)), 1);
                    this.upgradeValue = withStyleComponent("↑ " + original.getString(), -7092917);
                    this.selectedY = correctAdjustment ? 0 : ySpacer;
                }
            )
        );
    }

    private static AbilityData.AbilityModifiers getAbilityModifiers(Component component, AbilityHolder getTag) {
        var defaultVal = new AbilityData.AbilityModifiers(0, 0, 0, 0, 0,true);
        if(component == null) return defaultVal;
        var actualValue = getTag.data().abilityProperties().get(extractName(component.getString()));
        return actualValue == null ? defaultVal : actualValue;
    }

    private void doOnClick(Component component, AbilityHolder holder, boolean correctAdjustment, int posX, int posY){
        if(this.isInHitbox(posX, posY)){
            if(component == null) return;
            var pos = getMinecraft().player.blockPosition();
            var level = getMinecraft().level;
            var abilityKey = holder.abilityName();

            this.holder = updateAugmentConfig(extractName(component.getString()), abilityKey, getMinecraft().player);

            if(correctAdjustment && level != null){
                Helpers.getLocalSound(level, pos, APPLY_EFFECT_TRIAL_OMEN, 1, 2);
            }

            this.rebuildWidgets();
        }
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

    public boolean isInHitbox(double mouseX, double mouseY){
        var widthOffset = 100;
        var heightOffset = 115;
        int i = width / 2;
        int i1 = height / 2;
        var widthFrom = i - widthOffset;
        var heightFrom = i1 - heightOffset;
        var widthTo = i + widthOffset;
        var heightTo = i1 + heightOffset - 5;
        return mouseX > widthFrom && mouseX < widthTo && mouseY > heightFrom + 35 && mouseY < heightTo -  5;
    }

    private void windowMoveVertical(double dragY) {
        var size = componentsWithBounds(ability, holder, this.getMinecraft().level).size();
        if (size > 14) {
            int b = 7 * size * size - 135 * size + 578;
            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, b   -120));
            this.rebuildWidgets();
            this.selectedY = 0;
        } else this.yScroll = 0;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var adjustX = 18;
        var adjustY = -27;
        var startX = this.width / 2 - 140;
        var startY = this.height/2 + 22;

        this.renderBlurredBackground(partialTick);
        SharedUI.setCustomBackground(this.height, this.width, guiGraphics);
        selectedBoxUpgrade(guiGraphics, mouseX, mouseY);
        selectedBox(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();

        SharedUI.header(guiGraphics, this.width, this.height, ability, holder, this.font, this.getMinecraft().level);
        ElementReg.fromId(ability.getElemenType().id()).ifPresent(
            element -> bezelMaker(guiGraphics, startX + adjustX + 9, startY + adjustY - 123, 193, 224, 32, element)
        );
    }

    private void selectedBox(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(!this.isInHitbox(mouseX, mouseY)) return;
        if(this.selectedY <= 0) return;

        var colour = ability.getElemenType().textColourB();
        var semiTransLayer = getFadedColourBackground(0.9f);

        boxMaker(guiGraphics, this.width/2 - 97, (int) (this.selectedY + 8 + yScroll), 97, 14, colour, semiTransLayer);
    }

    private void selectedBoxUpgrade(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(!this.isInHitbox(mouseX, mouseY)) return;
        if(this.selectedY <= 0) return;

        var startX = this.width / 2 + 102;
        var startY = this.selectedY + 8;
        var colourBorder = ability.getElemenType().textColourB();
        var semiTransLayer = getFadedColourBackground(0.8f);

        boxMaker(guiGraphics, startX , (int) (startY + yScroll), 35, 14, colourBorder, semiTransLayer);
        boxMaker(guiGraphics, startX, (int) (startY + yScroll), 35, 14, colourBorder, semiTransLayer);
        boxMaker(guiGraphics, startX + 68, (int) (startY + yScroll), 14, 14, colourBorder, semiTransLayer);
        boxMaker(guiGraphics, startX + 68, (int) (startY + yScroll), 14, 14, colourBorder, semiTransLayer);
        guiGraphics.drawCenteredString(this.font, this.upgradeValue, startX + 34, (int) (startY + 10 + yScroll), 0);
    }

}
