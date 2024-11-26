package org.jahdoo.client.gui.augment_menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.client.SharedUI;
import org.jahdoo.client.gui.ToggleComponent;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityIconButton;
import org.jahdoo.client.gui.block.modular_chaos_cube.ModularChaosCubeScreen;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.networking.packet.client2server.SyncComponentBlockC2S;
import org.jahdoo.networking.packet.client2server.SyncComponentC2S;
import org.jahdoo.utils.ModHelpers;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.jahdoo.ability.AbilityBuilder.COOLDOWN;
import static org.jahdoo.ability.AbilityBuilder.MANA_COST;
import static org.jahdoo.client.gui.IconLocations.*;
import static org.jahdoo.client.gui.ToggleComponent.textWithBackgroundLarge;
import static org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationScreen.BORDER_COLOUR;
import static org.jahdoo.items.augments.AugmentItemHelper.getModifierContext;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;

public class AugmentScreen extends Screen  {
    public static final ResourceLocation BUTTON_UNSELECTED = ModHelpers.res("textures/gui/gui_button_unselected.png");
    public static final ResourceLocation BUTTON_SELECTED = ModHelpers.res("textures/gui/gui_button_selected.png");
    public static final WidgetSprites BUTTON = new WidgetSprites(BUTTON_UNSELECTED, BUTTON_UNSELECTED);
    public static final WidgetSprites BUTTON2 = new WidgetSprites(BUTTON_SELECTED, BUTTON_SELECTED);
    private WandAbilityHolder holder;
    private final String abilityName;
    private final Screen previousScreen;
    private double yScroll;
    private ItemStack itemStack;

    public AugmentScreen(ItemStack itemStack, String abilityName, @Nullable Screen previousScreen) {
        super(Component.literal("Augment Menu"));
        this.holder = itemStack.get(WAND_ABILITY_HOLDER.get());
        this.itemStack = itemStack;
        this.abilityName = abilityName;
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        var initialVerticalOffset = this.height / 2 - 50;
        var verticalSpacing = new AtomicInteger(initialVerticalOffset);
        navigationButtons(verticalSpacing);
        displayButtons(getModifiableList(), verticalSpacing);
    }

    private LinkedHashMap<String, AbilityHolder.AbilityModifiers> getModifiableList() {
        var mainHolderValues = holder.abilityProperties().get(abilityName);
        var copy = new HashMap<>(mainHolderValues.abilityProperties());
        copy.remove(MANA_COST);
        copy.remove(COOLDOWN);
        return copy.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
            Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new
            )
        );
    }

    private void displayButtons(LinkedHashMap<String, AbilityHolder.AbilityModifiers> copy1, AtomicInteger verticalSpacing) {
        copy1.forEach(
            (e, v) -> {
                var value = getModifierContext(e, ModHelpers.roundNonWholeString(v.setValue()), 1).getString();
                buildCarouselComponent(
                    this.width/2 - 70, verticalSpacing.get(), e, ()-> buttonReduce(e, v), ()-> buttonIncrease(e,v), value
                );
                verticalSpacing.set(verticalSpacing.get() + 36);
            }
        );
    }

    private void navigationButtons(AtomicInteger verticalSpacing) {
        var size = 30;
        var x = this.width/2 - 30;
        var y =this.height/2 - 70;
        this.menuButton("textures/gui/gui_button_close_dark.png", x - 100, y, size, (s) -> this.getMinecraft().setScreen(null));
        if(this.previousScreen != null){
            this.menuButton("textures/gui/gui_button_back_dark.png", x - 100, y + 20, size, (s) -> this.getMinecraft().setScreen(previousScreen));
        }
    }

    public void buildCarouselComponent(int posX, int posY, String label, Runnable onLeft, Runnable onRight, String value){
        var widget = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
        var adjustX = 4;
        this.addRenderableOnly(textWithBackgroundLarge(posX + 25 + adjustX, (int) (posY + yScroll), Component.literal(value), this.getMinecraft(), Component.literal(label), 10));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 10 + adjustX, (int) (posY + yScroll), (press) -> onLeft.run(), DIRECTION_ARROW_BACK, 22, false,4, widget, false));
        this.addRenderableWidget(ToggleComponent.menuButton(posX + 104 + adjustX, (int) (posY+ yScroll), (press) -> onRight.run(), DIRECTION_ARROW_FORWARD,  22,  false, 4, widget, false));
    }

    public void menuButton(String textureLocation, int posX, int posY, int size, Button.OnPress action) {
        var location = ModHelpers.res(textureLocation);
        var button = new WidgetSprites(location, location);
        this.addRenderableWidget(new AbilityIconButton(posX, posY, button, size, action, false));
    }

    private void buttonReduce(String e, AbilityHolder.AbilityModifiers v) {
        var higher = v.setValue() > v.lowestValue();
        var lower = v.setValue() > v.actualValue();
        if(v.isHigherBetter() ? higher : lower){
            updateAugmentConfig(e, v, v.setValue() - v.step());
            this.rebuildWidgets();
        }
    }

    private void buttonIncrease(String e, AbilityHolder.AbilityModifiers v) {
        var higher = v.setValue() < v.actualValue();
        var lower = v.setValue() < v.highestValue();
        if(v.isHigherBetter() ? higher : lower){
            updateAugmentConfig(e, v, v.setValue() + v.step());
            this.rebuildWidgets();
        }
    }

    private void updateAugmentConfig(String e, AbilityHolder.AbilityModifiers v, double i) {
        var newWandHolder = new WandAbilityHolder(new HashMap<>(holder.abilityProperties()));
        var newHolder = new AbilityHolder(new HashMap<>(holder.abilityProperties().get(abilityName).abilityProperties()));
        var abilityModifier = new AbilityHolder.AbilityModifiers(v.actualValue(), v.highestValue(), v.lowestValue(), v.step(), i, v.isHigherBetter());
        newHolder.abilityProperties().put(e, abilityModifier);
        newWandHolder.abilityProperties().put(abilityName, newHolder);

        if(this.previousScreen != null && this.previousScreen instanceof ModularChaosCubeScreen screen){
            var pos = screen.entity().getBlockPos();
            PacketDistributor.sendToServer(new SyncComponentBlockC2S(newWandHolder, pos));
        } else {
            PacketDistributor.sendToServer(new SyncComponentC2S(newWandHolder));
        }

        this.holder = newWandHolder;
        this.rebuildWidgets();
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

    private void windowMoveVertical(double dragY) {
        if (this.getModifiableList().size() > 4) {
            int listSize = this.getModifiableList().size();
            double maxScroll = -(50 + Math.sqrt(listSize) * listSize * 6); // Adjust the multiplier (5) to fit your needs
            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, maxScroll));
            this.rebuildWidgets();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        SharedUI.boxMaker(guiGraphics, this.width/2-130, this.height/2 - 70, 16, this.previousScreen == null ? 15 : 25, BORDER_COLOUR);
        SharedUI.setCustomBackground(this.height, this.width, guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();
        SharedUI.header(guiGraphics, this.width, this.height, itemStack, this.font);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}
}
