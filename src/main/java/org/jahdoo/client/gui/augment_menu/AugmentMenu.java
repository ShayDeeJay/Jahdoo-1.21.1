package org.jahdoo.client.gui.augment_menu;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityIconButton;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.networking.packet.SyncPlayerItemComponentsPacket;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.all_magic.AbilityBuilder.COOLDOWN;
import static org.jahdoo.all_magic.AbilityBuilder.MANA_COST;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;

public class AugmentMenu extends Screen  {
    public static final ResourceLocation BUTTON_UNSELECTED = GeneralHelpers.modResourceLocation("textures/gui/gui_button_unselected.png");
    public static final ResourceLocation BUTTON_SELECTED = GeneralHelpers.modResourceLocation("textures/gui/gui_button_selected.png");
    private final WidgetSprites BUTTON = new WidgetSprites(BUTTON_UNSELECTED, BUTTON_UNSELECTED);
    private final WidgetSprites BUTTON2 = new WidgetSprites(BUTTON_SELECTED, BUTTON_SELECTED);
    private final WandAbilityHolder holder;
    private final String abilityName;
    private final Screen previousScreen;

    public AugmentMenu(ItemStack itemStack, String abilityName, @Nullable Screen previousScreen) {
        super(Component.literal("Augment Menu"));
        this.holder = itemStack.get(WAND_ABILITY_HOLDER.get());
        this.abilityName = abilityName;
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        var mainHolderValues = holder.abilityProperties().get(abilityName);
        var copy = new HashMap<>(mainHolderValues.abilityProperties());
        copy.remove(MANA_COST);
        copy.remove(COOLDOWN);
        var newWandHolder = new WandAbilityHolder(holder.abilityProperties());
        var newHolder = new AbilityHolder(holder.abilityProperties().get(abilityName).abilityProperties());
        var buttonWidth = 32;
        var buttonHeight = 54;
        var totalRows = copy.size();
        var totalHeight = totalRows * buttonHeight;
        var initialVerticalOffset = (this.height - totalHeight) / 2;
        var verticalSpacing = new AtomicInteger(initialVerticalOffset);
        navigationButtons(verticalSpacing);
        copy.forEach(
            (e, v) -> {
                var buttonCount = (int) ((v.highestValue() - v.lowestValue()) / v.step()) + 1;
                var totalWidth = buttonWidth * buttonCount;
                var initialHorizontalOffset = (this.width - totalWidth) / 2;
                var horizontalSpacing = new AtomicInteger(initialHorizontalOffset);
                overlayHeader(e, verticalSpacing.get() + 10);
                for (double i = v.lowestValue(); i <= v.highestValue(); i += v.step()) {
                    setButtonsRow(e, v, i, horizontalSpacing, verticalSpacing, buttonWidth, newHolder, newWandHolder);
                }
                verticalSpacing.set(verticalSpacing.get() + buttonHeight);
            }
        );
    }

    private void navigationButtons(AtomicInteger verticalSpacing) {
        var size = 30;
        var x = this.width/2 - 30;
        var y = verticalSpacing.get() - size - 20;
        this.menuButton("textures/gui/gui_button_close_dark.png", x, y, size, (s) -> this.getMinecraft().setScreen(null));
        this.menuButton("textures/gui/gui_button_back_dark.png", x + 30, y, size, (s) -> this.getMinecraft().setScreen(previousScreen));
    }

    private void overlayHeader(String e, int verticalSpacing) {
        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int widpos, int i1, float v) {
                    guiGraphics.drawCenteredString(font, e, width/2 , verticalSpacing -22, -1);
                }
            }
        );
    }

    public void menuButton(String textureLocation, int posX, int posY, int size, Button.OnPress action) {
        var location = GeneralHelpers.modResourceLocation(textureLocation);
        var button = new WidgetSprites(location, location);
        this.addRenderableWidget(new AbilityIconButton(posX, posY, button, size, action, false));
    }

    private void setButtonsRow(String e, AbilityHolder.AbilityModifiers v, double i, AtomicInteger horizontalSpacing, AtomicInteger verticalSpacing, int buttonWidth, AbilityHolder newHolder, WandAbilityHolder newWandHolder) {

        this.addRenderableWidget(
            new AugmentIconButton(
                horizontalSpacing.get(), verticalSpacing.get(),
                i == v.setValue() ? BUTTON2 : BUTTON, buttonWidth,
                (s) -> {
                    var abilityModifier = new AbilityHolder.AbilityModifiers(v.actualValue(), v.highestValue(), v.lowestValue(), v.step(), i, v.isHigherBetter());
                    newHolder.abilityProperties().put(e, abilityModifier);
                    newWandHolder.abilityProperties().put(abilityName, newHolder);
                    PacketDistributor.sendToServer(new SyncPlayerItemComponentsPacket(newWandHolder));
                    this.rebuildWidgets();
                },
                GeneralHelpers.roundNonWhole(i),
                i == v.setValue(),
                i <= v.actualValue()
            )
        );

        horizontalSpacing.set(horizontalSpacing.get() + buttonWidth);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
    }

}
