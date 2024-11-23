package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.networking.packet.client2server.SyncComponentBlockC2S;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.client.gui.IconLocations.*;
import static org.jahdoo.client.gui.ToggleComponent.*;
import static org.jahdoo.items.augments.AugmentItemHelper.getModifierContext;
import static org.jahdoo.items.augments.AugmentRatingSystem.FORMAT;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.utils.ModHelpers.withStyleComponent;

public class AugmentModificationScreen extends AbstractContainerScreen<AugmentModificationMenu> {
    WidgetSprites widget = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final AugmentModificationMenu augmentModificationMenu;
    private WandAbilityHolder holder;
    private ItemStack item;
    private double yScroll;
    private int selectedY;
    private Component upgradeValue;

    public AugmentModificationScreen(AugmentModificationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.augmentModificationMenu = pMenu;
        this.item = pMenu.getAutomationEntity().getInteractionSlot();
        this.holder = pMenu.getAutomationEntity().getInteractionSlot().get(WAND_ABILITY_HOLDER.get());
    }

    @Override
    protected void init() {
        super.init();
        this.displayAugmentProperties();
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public AugmentModificationEntity entity(){
        return this.augmentModificationMenu.getAutomationEntity();
    }

    public void displayAugmentProperties(){
        if(item.isEmpty()) return;
        var components = getComponents();
        var spacer = new AtomicInteger();
        var compNew = components.subList(1, components.size()-2);
        int width = this.width / 2;

        for (Component component : compNew){
            String regex = ".*\\d.*";
            var ySpacer = (this.height / 2 - 85) + spacer.get();
            int selectedY1 = (int) (ySpacer + this.yScroll);
            this.addRenderableOnly(textRenderable(width - 139, selectedY1, component, this.getMinecraft()));

            if(Pattern.matches(regex, component.getString()) && !component.getString().contains(")")){
                var getTag = this.item.get(DataComponentRegistry.WAND_ABILITY_HOLDER);
                if(getTag == null) return;
                var abilityKey = getTag.abilityProperties().keySet().stream().findFirst().get();
                var x = getTag.abilityProperties().get(abilityKey).abilityProperties().get(extractName(component.getString()));
                var correctAdjustment = x.isHigherBetter() ? x.actualValue() == x.highestValue() : x.actualValue() == x.lowestValue();
                var nexUpgrade = x.isHigherBetter() ? x.actualValue() + x.step() == x.highestValue() : x.actualValue() - x.step() == x.lowestValue();

                this.addRenderableWidget(
                    menuButtonSound(width + 72, (int) (ySpacer + 10 + this.yScroll),
                        (press) -> doOnClick(component, item, nexUpgrade),
                        correctAdjustment ? UPGRADE_DISABLED : UPGRADE , 22, correctAdjustment,
                        correctAdjustment ? 0 : 8, widget, !correctAdjustment,
                        () -> {
                            var getHighest = x.isHigherBetter() ? x.actualValue() + x.step() : x.actualValue() - x.step();
                            var original = getModifierContext(extractName(component.getString()), ModHelpers.roundNonWholeString(getHighest), 1);
                            var next = getModifierContext(extractName(component.getString()), ModHelpers.roundNonWholeString(x.actualValue()), 1);
                            this.upgradeValue = withStyleComponent(next.getString(), -18612)
                                .copy()
                                .append(withStyleComponent(" ▶ ", -1))
                                .append(withStyleComponent(original.getString(), -7092917 ));
                            this.selectedY = correctAdjustment ? 0 : (int) (ySpacer + this.yScroll);
                        }
                    )
                );
            }
            spacer.set(spacer.get() + (component.getString().contains(")") || !Pattern.matches(regex, component.getString())? 15 : 10));
        }
    }

    private List<Component> getComponents(){
        var components = new ArrayList<Component>();
        AugmentItemHelper.getHoverText(item, components, true);
        return components;
    }

    private void doOnClick(Component component, ItemStack itemStack, boolean correctAdjustment){
        var getTag = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER);
        if(getTag == null) return;
        var abilityKey = getTag.abilityProperties().keySet().stream().findFirst().get();
        var localHolder = getTag.abilityProperties().get(abilityKey).abilityProperties().get(extractName(component.getString()));
        this.updateAugmentConfig(extractName(component.getString()), localHolder, 0, abilityKey);
        if(correctAdjustment){
            this.getMinecraft().level.playLocalSound(entity().getBlockPos(), SoundEvents.APPLY_EFFECT_TRIAL_OMEN, SoundSource.BLOCKS, 1f, 2f, false);
        }
    }

    private void updateAugmentConfig(String e, AbilityHolder.AbilityModifiers v, double i, String abilityName) {
        var newWandHolder = new WandAbilityHolder(new HashMap<>(holder.abilityProperties()));
        var newHolder = new AbilityHolder(new HashMap<>(holder.abilityProperties().get(abilityName).abilityProperties()));
        var correctAdjustment = v.isHigherBetter() ? v.actualValue() + v.step() : v.actualValue() - v.step();
        var valueWithinRange = v.isHigherBetter() && v.actualValue() < v.highestValue() ? correctAdjustment : !v.isHigherBetter() && v.actualValue() > v.lowestValue() ? correctAdjustment : v.actualValue();
        var abilityModifier = new AbilityHolder.AbilityModifiers(valueWithinRange, v.highestValue(), v.lowestValue(), v.step(), i, v.isHigherBetter());
        newHolder.abilityProperties().put(e, abilityModifier);
        newWandHolder.abilityProperties().put(abilityName, newHolder);
        PacketDistributor.sendToServer(new SyncComponentBlockC2S(newWandHolder, this.entity().getBlockPos()));
        this.holder = newWandHolder;
        this.item.set(WAND_ABILITY_HOLDER, newWandHolder);
        this.rebuildWidgets();
    }


    public static String extractName(String input) {
        if (input == null || !input.contains("|")) return "";
        return input.split("\\|")[0].trim();
    }

    private void boxMaker(GuiGraphics guiGraphics, int startX, int startY, int widthOffset, int heightOffset, int colourMain, int colourBorder){
        var widthFrom = startX - widthOffset;
        var heightFrom = startY - heightOffset;
        var widthTo = startX + widthOffset;
        var heightTo = startY + heightOffset;
        var fromColour = -804253680;
        var toColour = -804253680;
        guiGraphics.fillGradient(widthFrom, heightFrom, widthTo, heightTo, fromColour, toColour);
        guiGraphics.renderOutline(widthFrom, heightFrom, widthTo - widthFrom, heightTo - heightFrom, colourBorder);
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
        guiGraphics.hLine(width-100, width + 99, this.height/2 - 70, borderColour);
        guiGraphics.renderOutline(widthFrom, heightFrom, widthTo - widthFrom, heightTo - heightFrom, borderColour);
        guiGraphics.enableScissor(0, heightFrom + 50, this.width, heightTo - 5);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        windowMoveVertical((scrollY * 6));
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        windowMoveVertical(dragY);
        return true;
    }

    private void windowMoveVertical(double dragY) {
        if(this.getComponents().size() > 18){
            int b = -(4 * this.getComponents().size());
            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, b));
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
        selectedBox(guiGraphics);
        selectedBoxUpgrade(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        guiGraphics.disableScissor();
        header(guiGraphics, mouseX, mouseY, pPartialTick);
        abilityIcon(guiGraphics, this.augmentModificationMenu.getAutomationEntity().inputItemHandler.getStackInSlot(0), this.width - 155, this.height - 180, 109, 40);
    }

    private void selectedBox(@NotNull GuiGraphics guiGraphics) {
        if(this.selectedY > 0) {
            int value = this.entity().getInteractionSlot().get(DataComponents.CUSTOM_MODEL_DATA).value();
            AbstractElement element = ElementRegistry.getElementByTypeId(value).getFirst();
            this.boxMaker(guiGraphics, this.width/2, this.selectedY + 21, 97, 14,0, element.textColourSecondary());
        }
    }

    private void selectedBoxUpgrade(@NotNull GuiGraphics guiGraphics) {
        if(this.selectedY > 0) {
            int value = this.entity().getInteractionSlot().get(DataComponents.CUSTOM_MODEL_DATA).value();
            AbstractElement element = ElementRegistry.getElementByTypeId(value).getFirst();
            int startX = this.width / 2 + 160;
            int startY = this.selectedY + 21;
            this.boxMaker(guiGraphics, startX, startY, 58, 14,0, element.textColourSecondary());
            guiGraphics.drawCenteredString(this.font, this.upgradeValue, startX, startY - 4, 0);
        }
    }

    private void header(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        var yOff = 115;
        int xOff = this.width/2 - 105;
        textRenderable(xOff, (this.height/2 - (yOff - 10)), this.getComponents().getFirst(), this.getMinecraft()).render(guiGraphics, mouseX, mouseY, pPartialTick);
        textRenderable(xOff, (this.height/2 - yOff), AugmentItemHelper.getHoverName(item), this.getMinecraft()).render(guiGraphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
