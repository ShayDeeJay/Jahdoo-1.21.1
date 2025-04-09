package org.jahdoo.common.block.augment_modification_station;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.button.AbilityIconButton;
import org.jahdoo.common.client.screens.AbilityUnlockScreen;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.items.augments.AugmentItemHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;
import static net.minecraft.sounds.SoundEvents.APPLY_EFFECT_TRIAL_OMEN;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;
import static org.jahdoo.common.block.augment_modification_station.AugmentModificationData.extractName;
import static org.jahdoo.common.block.augment_modification_station.AugmentModificationData.updateAugmentConfig;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.client.button.ToggleComponent.textRenderable;
import static org.jahdoo.common.items.augments.AugmentItemHelper.getModifierContextSingle;
import static org.jahdoo.common.registers.ElementReg.*;

public class AbilityModificationScreen extends Screen {

    public Inventory inventory;
    private List<Component> compValues = new ArrayList<>();
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

    public AbilityModificationScreen(
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

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.enableScissor(3, 90, width - 3, height - 4);
                }
            }
        );

        this.displayAugmentProperties();
        this.navigationButtons();

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.disableScissor();
                }
            }
        );

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

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

        var spacer = 0;
        var width = this.width / 2;
        var components = componentsWithBounds(ability, holder, getMinecraft().player);

        for (var comp : components){
            var mod = getAbilityModifiers(comp, this.holder);
            var regex = ".*\\d.*";
            var ySpacer = (this.height / 2 - 92) + spacer;
            var selectedY1 = (int) (ySpacer + this.yScroll);

            if(mod.highestValue() != -1){
                buildPropertiesWithHighlight(comp, width, selectedY1);

                if (Pattern.matches(regex, comp.getString()) && !comp.getString().contains(")")) {
                    var correctAdjustment = mod.isHigherBetter() ? mod.actualValue() == mod.highestValue() : mod.actualValue() == mod.lowestValue();
                    var nexUpgrade = mod.isHigherBetter() ? mod.actualValue() + mod.step() == mod.highestValue() : mod.actualValue() - mod.step() == mod.lowestValue();
                    upgradeButton(comp, width, ySpacer, nexUpgrade, correctAdjustment, mod);
                }

                spacer += (comp.getString().contains(")") || !Pattern.matches(regex, comp.getString()) ? 17 : 10);
            }
        }
    }

    public static List<Component> componentsWithBounds(Ability ability, AbilityHolder holder, Player player){
        var components = AugmentItemHelper.getAllAbilityModifiers(ability, holder, true, false, player);
        var compNew = components
            .subList(1, components.size())
            .stream()
            .filter(c -> getAbilityModifiers(c, holder).highestValue() != -1)
            .filter(c -> !c.equals(Component.literal(" ")) && !c.getString().contains("Unique") && !c.getString().contains("Rarity"));
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
        var posX = width + 72;
        var posY = (int) (ySpacer + 11 + this.yScroll);
        var getHighest = x.isHigherBetter() ? x.actualValue() + x.step() : x.actualValue() - x.step();
        var costMultiplier = x.isHigherBetter() ?
            (x.actualValue() - x.lowestValue()) / x.step() :
            (x.highestValue() - x.actualValue()) / x.step();
        var adjusted = (int) Math.max(x.baseCost(), x.baseCost() * ((costMultiplier/2) * 1.2));
        var canPurchase = CastingData.canPurchase(getMinecraft().player, adjusted);
        var buttonIcon = correctAdjustment || canPurchase ? UPGRADE_DISABLED : UPGRADE;
        var active = correctAdjustment ||  canPurchase;
        var scale = correctAdjustment ? 0 : 8;
        var showHover = !correctAdjustment && !canPurchase;

        this.addRenderableWidget(
            menuButtonSound(
                posX, posY,
                (press) -> doOnClick(component, holder, nexUpgrade, adjusted),
                buttonIcon, 22, active, scale, WIDGET, showHover,
                () -> onHover(component, ySpacer, active, getHighest, adjusted)
            )
        );
    }

    private void onHover(Component component, int ySpacer, boolean active, double getHighest, int adjusted) {
        if(!active){
            var original = getModifierContextSingle(extractName(component.getString()), valueOf(doubleFormattedDouble(getHighest)), 1);
            var prefixTier = withStyleComponent("Next Tier: ", HEADER_COLOUR);
            var upgradeTier = prefixTier.copy().append(withStyleComponent("↑ " + original.getString(), ABSORPTION_YELLOW));
            var prefixCost = withStyleComponent("Skill Points: ", HEADER_COLOUR);
            var upgradeCost = prefixCost.copy().append(withStyleComponent(adjusted + "", PERK_GREEN));

            compValues.add(upgradeTier);
            compValues.add(upgradeCost);
            this.selectedY = ySpacer;
        }
    }

    private static AbilityData.AbilityModifiers getAbilityModifiers(Component component, AbilityHolder getTag) {
        var defaultVal = new AbilityData.AbilityModifiers(0, 0, 0, 0, 0, 0, true);
        if(component == null) return defaultVal;
        var actualValue = getTag.data().abilityProperties().get(extractName(component.getString()));
        return actualValue == null ? defaultVal : actualValue;
    }

    private void doOnClick(Component component, AbilityHolder holder, boolean correctAdjustment, int cost){
        var player = getMinecraft().player;
        if(component == null || player == null) return;

        var pos = player.blockPosition();
        var level = getMinecraft().level;
        var abilityKey = holder.abilityName();
        var name = extractName(component.getString());

        this.holder = updateAugmentConfig(name, abilityKey, player, cost);

        if(correctAdjustment && level != null){
            Helpers.getLocalSound(level, pos, APPLY_EFFECT_TRIAL_OMEN, 1, 2);
        }

        this.rebuildWidgets();
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
        var size = componentsWithBounds(ability, holder, getMinecraft().player).size();
        if (size > 14) {
            int b = 7 * size * size - 135 * size + 578;
            this.yScroll = Math.min(0, Math.max(this.yScroll + dragY, b   -120));
            this.rebuildWidgets();
            this.selectedY = 0;
        } else this.yScroll = 0;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var i = this.height / 2;
        var i1 = this.width / 2;
        var startX = i1 - 140;
        var startY = i + 22;
        var element = ability.getElemenType();

        this.renderBlurredBackground(partialTick);

        backgroundWithStyle(graphics, i1, i, element);
        overlaySkillPoints(graphics, getMinecraft().player);
        SharedUI.boxMaker(graphics, this.width/2-131, this.height/2 - 70, 16, 25);
        selectedModifier(graphics, mouseX, mouseY);
        container(graphics, mouseX, mouseY, partialTick, startX, startY, element);
        headerWithBorder(graphics, element);
    }

    /**
     * Display main background with element colour and bezels
     * */
    private void backgroundWithStyle(@NotNull GuiGraphics graphics, int i1, int i, AbstractElement element) {
        var fade = SharedUI.getFadedColourBackground(0.9F);
        boxMaker(graphics, 3, 3, i1 - 3, i - 3, element.partColourFade(), fade, FastColor.ARGB32.color(60, element.partColourFade()));
        SharedUI.bezelMaker(graphics, -20 , -20, this.width - 20, this.height - 20, 60, null);
    }

    /**
     * Main container housing the modifiers and header
     * */
    private void container(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int startX, int startY, AbstractElement element) {
        var adjustX = 18;
        var adjustY = -27;
        SharedUI.setCustomBackground(this.height, this.width, graphics);
        bezelMaker(graphics, startX + adjustX + 9, startY + adjustY - 123, 193, 224, 32, element);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Display the header with box related to element
     * */
    private void headerWithBorder(@NotNull GuiGraphics graphics, AbstractElement element) {
        var backdrop = element == utility() ? GUI_BUTTON_UTILITY_SQUARE :
                       element == vitality() ? GUI_BUTTON_VITALITY_SQUARE :
                       element == mystic() ? GUI_BUTTON_MYSTIC_SQUARE :
                       element == frost() ? GUI_BUTTON_FROST_SQUARE : GUI_BUTTON_INFERNO_SQUARE;
        SharedUI.header(graphics, this.width, this.height, ability, holder, this.font, getMinecraft().player, backdrop);
    }

    /**
     * Skill points display
     * */
    private void overlaySkillPoints(GuiGraphics guiGraphics, LocalPlayer player) {
        var size = 24;
        var skillPoints = CastingData.getAbilityPoints(player);
        var fade1 = SharedUI.getFadedColourBackground(0.5F);
        var length = valueOf(skillPoints).length();
        var i1 = this.width / 2 + 99;
        var i2 = this.height / 2 - 120;
        boxMaker(guiGraphics, i1, 20 + i2, 15 + (length * length), 10, BORDER_COLOUR, fade1, fade1);
        guiGraphics.drawString(font, withStyleComponent(skillPoints + "", PERK_GREEN), i1 + 22, 26 + i2, -1);
        guiGraphics.blit(SKILL_POINT, i1 - 2, 18 + i2, 0, 0, size, size, size, size);
    }

    /**
     * Highlights the selected row with an outlined box and upgrade value tooltip
     * */
    private void selectedModifier(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(!this.compValues.isEmpty()){
            var colour = ability.getElemenType().textColourB();
            var semiTransLayer = getFadedColourBackground(0.9f);

            if (this.selectedY <= 0) return;

            guiGraphics.renderTooltip(font, compValues, Optional.empty(), (int) mouseX, (int) mouseY);
            guiGraphics.pose().pushPose();
            boxMaker(guiGraphics, this.width / 2 - 97, (int) (this.selectedY + 8 + yScroll), 97, 14, colour, semiTransLayer);
            guiGraphics.pose().popPose();
        }
        this.compValues = new ArrayList<>();
    }

    public void menuButton(ResourceLocation location, int posX, int posY, int size, Button.OnPress action) {
        var button = new WidgetSprites(location, location);
        this.addRenderableWidget(new AbilityIconButton(posX, posY, button, size, action, false, () -> {}));
    }

    private void navigationButtons() {
        var size = 32;
        var x = this.width/2 - 30;
        var y = this.height/2 - 70;
        this.menuButton(CLOSE, x - 101, y, size, (s) -> this.getMinecraft().setScreen(null));
        this.menuButton(DIRECTION_ARROW_BACK, x - 101, y + 20, size, (s) -> this.getMinecraft().setScreen(new AbilityUnlockScreen(size, panX, panY, zoomX, scaledSpacing, scaledXOffset, centerX, centerY)));
    }

}
