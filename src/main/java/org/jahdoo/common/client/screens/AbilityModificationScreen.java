package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.button.AbilityIconButton;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.client2server.AbilityHolderC2SP;
import org.jahdoo.common.networking.client2server.AbilityPointC2SP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.magic.Ability;
import org.jahdoo.trial_nexus.magic.AbilityComponentHelper;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;
import static net.minecraft.sounds.SoundEvents.APPLY_EFFECT_TRIAL_OMEN;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.client.button.ToggleComponent.textRenderable;
import static org.jahdoo.trial_nexus.magic.AbilityComponentHelper.*;
import static org.jahdoo.trial_nexus.utils.Icons.*;

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
        this.navigationButtons();

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.enableScissor(3, height/2 - 68, width - 3, height/2 + 110);
                }
            }
        );

        this.displayAbilityProperties();

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

    public void displayAbilityProperties(){
        if(holder == null) return;

        var spacer = 0;
        var width = this.width / 2;
        var components = componentsWithBounds(ability, holder, getMinecraft().player);

        for (var comp : components){
            var mod = getAbilityModifiers(comp, this.holder);
            var regex = ".*\\d.*";
            var ySpacer = (this.height / 2 - 84) + spacer;
            var selectedY1 = (int) (ySpacer + this.yScroll);

            if(mod.highestValue() != -1){
                buildPropertiesWithHighlight(comp, width, selectedY1);
                var isRange = Pattern.matches(regex, comp.getString());
                var getIsBool = comp.getString().contains("False");
                var canDisplay = isRange || getIsBool;
                if (canDisplay && !comp.getString().contains(")")) {
                    var correctAdjustment = mod.isHigherBetter() ? mod.actualValue() == mod.highestValue() : mod.actualValue() == mod.lowestValue();
                    var nexUpgrade = mod.isHigherBetter() ? mod.actualValue() + mod.step() == mod.highestValue() : mod.actualValue() - mod.step() == mod.lowestValue();
                    upgradeButton(comp, width, ySpacer, nexUpgrade, correctAdjustment, mod);
                }

                spacer += (comp.getString().contains(")") ? 17 : 10);
            }
        }
    }

    public static List<Component> componentsWithBounds(Ability ability, AbilityHolder holder, Player player){
        var components = AbilityComponentHelper.getAllAbilityModifiers(ability, holder, true, false, player);
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
            rebuild.append(TextHelpers.withStyleComponent(component.toFlatList().getLast().getString(), -1129857));
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
        var canPurchase = CasterData.canPurchaseAbility(getMinecraft().player, adjusted);
        var buttonIcon = correctAdjustment || canPurchase ? UPGRADE_DISABLED : UPGRADE;
        var active = correctAdjustment ||  canPurchase;
        var scale = correctAdjustment ? 0 : 8;
        var showHover = !correctAdjustment && !canPurchase;

        this.addRenderableWidget(
            menuButtonSound(
                posX, posY,
                (press) -> doOnClick(component, holder, nexUpgrade, adjusted),
                buttonIcon, 22, active, scale, WIDGET, showHover,
                () -> onHover(component, ySpacer, active, getHighest, adjusted),
                null
            )
        );
    }

    public static String extractName(String input) {
        if (input == null || !input.contains("|")) return "";
        return input.split("\\|")[0].trim();
    }

    private void onHover(Component component, int ySpacer, boolean active, double getHighest, int adjusted) {
        if(!active){
            var original = getModifierContextSingle(extractName(component.getString()), valueOf(MathHelpers.doubleFormattedDouble(getHighest)), 1);
            var prefixTier = TextHelpers.withStyleComponent("Next Tier: ", ColourHelpers.getHeaderColour());
            var upgradeTier = prefixTier.copy().append(TextHelpers.withStyleComponent("↑ " + original.getString(), ColourHelpers.getAbsorptionYellow()));
            var prefixCost = TextHelpers.withStyleComponent("Skill Points: ", ColourHelpers.getHeaderColour());
            var upgradeCost = prefixCost.copy().append(TextHelpers.withStyleComponent(adjusted + "", ColourHelpers.getPerkGreen()));

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

    public static AbilityHolder updateAugmentConfig(
        String name,
        String abilityName,
        Player player,
        int cost
    ) {

        var data = player.getData(AttachmentReg.CASTER_DATA);
        var properties = new LinkedHashMap<>(data.getHolder(abilityName).data().abilityProperties());
        var mod = properties.get(name);
        var higherBetter = mod.isHigherBetter();
        var actualValue = MathHelpers.doubleFormattedDouble(mod.actualValue());
        var step = MathHelpers.doubleFormattedDouble(mod.step());
        var highestValue = MathHelpers.doubleFormattedDouble(mod.highestValue());
        var lowestValue = MathHelpers.doubleFormattedDouble(mod.lowestValue());
        var baseCost = MathHelpers.doubleFormattedDouble(mod.baseCost());
        var correctAdjustment = higherBetter ? actualValue + step : actualValue - step;
        var valueWithinRange = higherBetter && actualValue < highestValue ? correctAdjustment : !higherBetter && actualValue > lowestValue ? correctAdjustment : actualValue;
        var abilityModifier = new AbilityData.AbilityModifiers(valueWithinRange, highestValue, lowestValue, step, valueWithinRange, baseCost, higherBetter);

        properties.replace(name, abilityModifier);

        var holders = new AbilityHolder(abilityName, new AbilityData(properties));

        sendToServer(new AbilityPointC2SP(cost));
        sendToServer(new AbilityHolderC2SP(holders, 0));
        data.decrementAbilityPoints(cost);
//        data.updateLoadout();
        return holders;
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
            JahdooHelpers.getLocalSound(level, pos, APPLY_EFFECT_TRIAL_OMEN, 1, 2);
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

        return mouseX > widthFrom && mouseX < widthTo && mouseY > heightFrom + 35 && mouseY < heightTo - 5;
    }

    private void windowMoveVertical(double dragY) {
        var components = componentsWithBounds(ability, holder, getMinecraft().player);
        int entryCount = components.size();

        if (entryCount < 14) {
            this.yScroll = 0;
            return;
        }

        // Approximate spacing: either 17 (with brackets) or 10 (regular), default average
        int averageSpacing = 12; // You can fine-tune this based on actual proportions
        int visibleArea = 145;   // The scissor height from init(): 110 - (-70) = 180, minus padding
        int totalContentHeight = entryCount * averageSpacing;

        double maxScroll = 0; // top (no scroll)
        double minScroll = Math.min(0, visibleArea - totalContentHeight); // bottom (fully scrolled)

        this.yScroll = Mth.clamp(this.yScroll + dragY, minScroll, maxScroll);
        this.rebuildWidgets();
        this.selectedY = 0;
    }

    /**
     * Display main background with element colour and bezels
     * */
    public static void backgroundWithStyle(@NotNull GuiGraphics graphics, AbstractElement element) {
        var fade = SharedUI.fadeBlack(0.9F);
        var width = graphics.guiWidth();
        var height = graphics.guiHeight();
        var i = height / 2;
        var i1 = width / 2;

        boxMaker(graphics, 3, 3, i1 - 3, i - 3, element.partColourFade(), fade, FastColor.ARGB32.color(60, element.partColourFade()));
        SharedUI.bezelMaker(graphics, -20 , -20, width - 20, height - 20, 60, null);
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
    public static void headerWithBorder(@NotNull GuiGraphics graphics, AbstractElement element, int width, int height, Ability ability, AbilityHolder holder, Minecraft minecraft) {
        SharedUI.header(graphics, width, height, ability, holder, minecraft.font, minecraft.player, element.backgroundTexture());
    }

    /**
     * Skill points display
     * */
    private void overlaySkillPoints(GuiGraphics guiGraphics, LocalPlayer player) {
        var size = 24;
        var skillPoints = CasterData.getAbilityPointData(player);
        var length = valueOf(skillPoints).length();
        var i1 = (this.width / 2 + 74) - (6 * length);
        var i2 = this.height / 2 - 134;

        guiGraphics.drawString(font, TextHelpers.withStyleComponent(skillPoints + "", ColourHelpers.getPerkGreen()), i1 + 20, 26 + i2, -1);
        guiGraphics.blit(SKILL_POINT, i1 - 2, 18 + i2, 0, 0, size, size, size, size);
    }

    /**
     * Highlights the selected row with an outlined box and upgrade value tooltip
     * */
    private void selectedModifier(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(!this.compValues.isEmpty()){
            var colour = ability.getElemenType().textColourB();
            var semiTransLayer = fadeBlack(0.9f);

            if (this.selectedY <= 0) return;

            guiGraphics.renderTooltip(font, compValues, Optional.empty(), (int) mouseX, (int) mouseY);
            guiGraphics.pose().pushPose();
            guiGraphics.enableScissor(3, height/2 - 70, width - 3, height/2 + 110);
            boxMaker(guiGraphics, this.width / 2 - 97, (int) (this.selectedY + 8 + yScroll), 97, 14, colour, semiTransLayer);
            guiGraphics.disableScissor();
            guiGraphics.pose().popPose();
        }
        this.compValues = new ArrayList<>();
    }

    public void menuButton(ResourceLocation location, int posX, int posY, int size, Button.OnPress action) {
        var button = new WidgetSprites(location, location);
        this.addRenderableWidget(new AbilityIconButton(posX, posY, button, size, action, false, () -> {}));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var i = this.height / 2;
        var i1 = this.width / 2;
        var startX = i1 - 140;
        var startY = i + 22;
        var mc = getMinecraft();

        this.renderBlurredBackground(partialTick);

        var element = ability.getElemenType();
        backgroundWithStyle(graphics, element);

        container(graphics, mouseX, mouseY, partialTick, startX, startY, element);
        selectedModifier(graphics, mouseX, mouseY);
        headerWithBorder(graphics, element, this.width, this.height, ability, holder, mc);
        overlaySkillPoints(graphics, mc.player);
    }

    private void navigationButtons() {
        var size = 32;
        var y = this.height/2 - 120;
        var mc = this.getMinecraft();

        this.menuButton(CLOSE, this.width - 36, 4, size, (s) -> mc.setScreen(null));
        this.menuButton(DIRECTION_ARROW_BACK, 4, 4, size, (s) -> mc.setScreen(new AbilityUnlockScreen(size, panX, panY, zoomX, scaledSpacing, scaledXOffset, centerX, centerY)));

        var guiScreen = abilityModificationScreen(mc.player, this, ability);

        if(isConfigAbility(mc.player, ability))
            this.menuButton(COG, this.width/2 + 84, y + 34, 12, (s) -> mc.setScreen(guiScreen));
    }

}
