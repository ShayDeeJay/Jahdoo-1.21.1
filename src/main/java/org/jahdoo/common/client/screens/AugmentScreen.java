package org.jahdoo.common.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.button.AbilityIconButton;
import org.jahdoo.common.client.button.ToggleComponent;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.client2server.AbilityHolderC2SP;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.button.ToggleComponent.textWithBackgroundLarge;
import static org.jahdoo.common.client.screens.AbilityModificationScreen.headerWithBorder;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.COOLDOWN;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.MANA_COST;
import static org.jahdoo.trial_nexus.ability.AbilityComponentHelper.getModifierContextSingle;

public class AugmentScreen extends Screen  {

    private AbilityHolder holder;
    private final Ability ability;
    private final Screen previousScreen;
    private double yScroll;

    public AugmentScreen(Player player, Screen previousScreen, Ability ability) {
        super(Component.literal("Augment Menu"));
        this.holder = CasterData.entityHolderWithSelected(player);
        this.ability = ability;
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
//        int listSize = this.getModifiableList().size();
//        if (listSize > 4) {
//            var maxScroll = -20;
//            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, maxScroll * (listSize-4)));
//            this.rebuildWidgets();
//        }
        int listSize = this.getModifiableList().size();
        int entryHeight = 40;
        int visibleEntries = 5; // You can adjust based on screen size
        int totalHeight = listSize * entryHeight;
        int visibleHeight = visibleEntries * entryHeight;

        if (listSize > visibleEntries) {
            double maxScroll = totalHeight - visibleHeight;
            this.yScroll = Math.max(-maxScroll, Math.min(0, this.yScroll + dragY));
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
                var value = getModifierContextSingle(e, MathHelpers.roundNonWholeString(v.setValue()), 1).getString();
                if(e.contains("Toggle")){
//                    System.out.println(e);
//                    System.out.println(v.);
                    if(v.actualValue() == v.highestValue()){
                        buildBooleanComponent(this.width / 2 - 70, verticalSpacing.get() - 8, e, () -> buttonReduce(e, v), () -> buttonIncrease(e, v), value);
                        verticalSpacing.set(verticalSpacing.get() + 36);
                    }
                } else {
                    buildCarouselComponent(this.width / 2 - 70, verticalSpacing.get() - 8, e, () -> buttonReduce(e, v), () -> buttonIncrease(e, v), value);
                    verticalSpacing.set(verticalSpacing.get() + 36);
                }
            }
        );
    }

    public void buildCarouselComponent(int posX, int posY, String label, Runnable onLeft, Runnable onRight, String value){
        var widget = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        var adjustX = 2;
        this.addRenderableOnly(textWithBackgroundLarge(posX + 25 + adjustX, (int) (posY + yScroll),  TextHelpers.withStyleComponent(value, ElementReg.utility().textColourB()), this.getMinecraft(), Component.literal(label), 10, true));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 10 + adjustX, (int) (posY + yScroll), (press) -> onLeft.run(), DIRECTION_ARROW_BACK, 22, false,0, widget, true));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 104 + adjustX, (int) (posY+ yScroll), (press) -> onRight.run(), DIRECTION_ARROW_FORWARD,  22,  false, 0, widget, true));
    }

    public void buildBooleanComponent(int posX, int posY, String label, Runnable onLeft, Runnable onRight, String value){
        var widget = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        var adjustX = 2;
        var locked = Objects.equals(value, "False");
        var height = 22;

        this.addRenderableOnly(textWithBackgroundLarge(posX + 25 + adjustX, (int) (posY + yScroll), Component.empty(), this.getMinecraft(), Component.literal(label), 10, false));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 44 + adjustX, (int) (posY + yScroll) + 2, (press) -> onLeft.run(), POWER_OFF, height, locked,0,  widget, true));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 69 + adjustX, (int) (posY+ yScroll) + 2, (press) -> onRight.run(), POWER_ON, height,  !locked, 0, widget, true));
    }

    @Override
    protected void init() {
        var initialVerticalOffset = this.height / 2 - 50;
        var verticalSpacing = new AtomicInteger(initialVerticalOffset);
        navigationButtons();
        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.enableScissor(3, height/2 - 68, width - 3, height/2 + 110);
                }
            }
        );
        displayButtons(getModifiableList(), verticalSpacing);
        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.disableScissor();
                }
            }
        );
    }

    private LinkedHashMap<String, AbilityData.AbilityModifiers> getModifiableList() {
        var mainHolderValues = holder.data();
        var copy = new HashMap<>(mainHolderValues.abilityProperties());
        copy.remove(MANA_COST);
        copy.remove(COOLDOWN);

        return copy.entrySet().stream()
            .sorted(
                Comparator.comparing(
                    Map.Entry<String, AbilityData.AbilityModifiers>::getKey,
                    Comparator.comparing((String key) -> key.startsWith("Toggle") ? 1 : 0) // Put "Toggle" last
                        .thenComparing(Comparator.naturalOrder()) // Then sort alphabetically
                )
            )
            .filter(s -> s.getValue().actualValue() >= s.getValue().lowestValue() + s.getValue().step())
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                )
            );
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void renderBlurredBackground(float partialTick) {
        super.renderBlurredBackground(partialTick);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        SharedUI.boxMaker(graphics, this.width/2 - 131, this.height/2 - 70, 16, this.previousScreen == null ? 16 : 25);
        SharedUI.setCustomBackground(this.height, this.width, graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        headerWithBorder(graphics, ability.getElemenType(), this.width, this.height, ability, holder, getMinecraft());
    }

    private void updateAugmentConfig(String e, AbilityData.AbilityModifiers v, double i) {
        var newHolder = new AbilityData(new HashMap<>(holder.data().abilityProperties()));
        var abilityModifier = new AbilityData.AbilityModifiers(v.actualValue(), v.highestValue(), v.lowestValue(), v.step(), i, v.baseCost(), v.isHigherBetter());
        newHolder.abilityProperties().put(e, abilityModifier);

        this.holder = new AbilityHolder(holder.abilityName(), newHolder);
        PacketDistributor.sendToServer(new AbilityHolderC2SP(holder, 0));
        this.rebuildWidgets();
    }
}
