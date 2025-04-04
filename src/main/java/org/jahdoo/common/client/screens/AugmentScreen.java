package org.jahdoo.common.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.button.AbilityIconButton;
import org.jahdoo.common.client.button.ToggleComponent;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.jahdoo.ascension.ability.AbilityBuilder.COOLDOWN;
import static org.jahdoo.ascension.ability.AbilityBuilder.MANA_COST;
import static org.jahdoo.ascension.utils.Maths.roundNonWholeString;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.button.ToggleComponent.textWithBackgroundLarge;
import static org.jahdoo.common.items.augments.AugmentItemHelper.getModifierContextSingle;
import static org.jahdoo.common.registers.ComponentReg.ABILITY_HOLDER;

public class AugmentScreen extends Screen  {

    private AbilityHolder holder;
    private final String abilityName;
    private final Screen previousScreen;
    private double yScroll;
    private final ItemStack itemStack;

    public AugmentScreen(ItemStack itemStack, String abilityName, Screen previousScreen) {
        super(Component.literal("Augment Menu"));
        this.holder = itemStack.get(ABILITY_HOLDER.get());
        this.itemStack = itemStack;
        this.abilityName = abilityName;
        this.previousScreen = previousScreen;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        windowMoveVertical(scrollY * 4);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        windowMoveVertical(dragY);
        return true;
    }

    public void menuButton(ResourceLocation location, int posX, int posY, int size, Button.OnPress action) {
        var button = new WidgetSprites(location, location);
        this.addRenderableWidget(new AbilityIconButton(posX, posY, button, size, action, false, () -> {}));
    }

    private void buttonReduce(String e, AbilityData.AbilityModifiers v) {
        var min = Math.min(v.setValue() + v.step(), v.highestValue());
        var max = Math.max(v.setValue() - v.step(), v.lowestValue());
        updateAugmentConfig(e, v, v.isHigherBetter() ? max : min);
        this.rebuildWidgets();
    }

    private void buttonIncrease(String e, AbilityData.AbilityModifiers v) {
        var min = Math.max(v.setValue() - v.step(), v.actualValue());
        var max = Math.min(v.setValue() + v.step(), v.actualValue());
        updateAugmentConfig(e, v, v.isHigherBetter() ? max : min);
        this.rebuildWidgets();
    }

    private void windowMoveVertical(double dragY) {
        int listSize = this.getModifiableList().size();
        if (listSize > 4) {
            var maxScroll = -20;
            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, maxScroll * (listSize-4)));
            this.rebuildWidgets();
        }
    }

    private void navigationButtons() {
        var size = 32;
        var x = this.width/2 - 30;
        var y =this.height/2 - 70;
        this.menuButton(CLOSE, x - 101, y, size, (s) -> this.getMinecraft().setScreen(null));
        if(this.previousScreen != null){
            this.menuButton(DIRECTION_ARROW_BACK, x - 101, y + 20, size, (s) -> this.getMinecraft().setScreen(previousScreen));
        }
    }

    private void displayButtons(LinkedHashMap<String, AbilityData.AbilityModifiers> copy1, AtomicInteger verticalSpacing) {

        copy1.forEach(
            (e, v) -> {
                var value = getModifierContextSingle(e, roundNonWholeString(v.setValue()), 1).getString();
                buildCarouselComponent(this.width/2 - 70, verticalSpacing.get(), e, ()-> buttonReduce(e, v), ()-> buttonIncrease(e,v), value);
                verticalSpacing.set(verticalSpacing.get() + 36);
            }
        );
    }

    public void buildCarouselComponent(int posX, int posY, String label, Runnable onLeft, Runnable onRight, String value){
        var widget = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        var adjustX = 2;
        this.addRenderableOnly(textWithBackgroundLarge(posX + 25 + adjustX, (int) (posY + yScroll),  Helpers.withStyleComponent(value, -1381654), this.getMinecraft(), Component.literal(label), 10));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 10 + adjustX, (int) (posY + yScroll), (press) -> onLeft.run(), DIRECTION_ARROW_BACK, 22, false,0, widget, true));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 104 + adjustX, (int) (posY+ yScroll), (press) -> onRight.run(), DIRECTION_ARROW_FORWARD,  22,  false, 0, widget, true));
    }

    @Override
    protected void init() {
        var initialVerticalOffset = this.height / 2 - 50;
        var verticalSpacing = new AtomicInteger(initialVerticalOffset);
        navigationButtons();
        displayButtons(getModifiableList(), verticalSpacing);
    }

    private LinkedHashMap<String, AbilityData.AbilityModifiers> getModifiableList() {
        var mainHolderValues = holder.data();
        var copy = new HashMap<>(mainHolderValues.abilityProperties());
        copy.remove(MANA_COST);
        copy.remove(COOLDOWN);
        return copy.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
            Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new
            )
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        SharedUI.boxMaker(guiGraphics, this.width/2-131, this.height/2 - 70, 16, this.previousScreen == null ? 16 : 25);
        SharedUI.setCustomBackground(this.height, this.width, guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();
//        SharedUI.header(guiGraphics, this.width, this.height, itemStack, this.font, this.getMinecraft().level);

    }

    private void updateAugmentConfig(String e, AbilityData.AbilityModifiers v, double i) {

        var newHolder = new AbilityData(new HashMap<>(holder.data().abilityProperties()));
        var abilityModifier = new AbilityData.AbilityModifiers(v.actualValue(), v.highestValue(), v.lowestValue(), v.step(), i, v.isHigherBetter());
        newHolder.abilityProperties().put(e, abilityModifier);
        //TODO AS NEEDS PACKET FIX
        /*
        if(this.previousScreen != null && this.previousScreen instanceof ChaosCubeScreen screen){
            var pos = screen.entity().getBlockPos();
            PacketDistributor.sendToServer(new SyncComponentBlockC2S(newWandHolder, pos));
        } else {
            PacketDistributor.sendToServer(new SyncComponentC2S(newWandHolder));
        }

        this.holder = newWandHolder;*/
        this.rebuildWidgets();
    }
}
