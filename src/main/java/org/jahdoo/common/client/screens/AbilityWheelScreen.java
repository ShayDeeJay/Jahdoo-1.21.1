package org.jahdoo.common.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.button.AbilityIconButton;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.jahdoo.ascension.utils.Helpers.syncSelectedAbility;
import static org.jahdoo.common.client.Icons.COG;
import static org.jahdoo.common.items.augments.AugmentItemHelper.getAugmentModificationScreenWand;
import static org.jahdoo.common.items.augments.AugmentItemHelper.isConfigAbility;

@EventBusSubscriber(Dist.CLIENT)
public class AbilityWheelScreen extends Screen  {

    private int slots;
    private boolean switchState;
    private float localTick = 60;
    private static final int RADIAL_SIZE = 150;
    private static final int RADIUS = (int) (8.4 * ((double) RADIAL_SIZE / 20) - 4);
    private int buttonSize;
    private final List<AbilityIconButton> buttons = new ArrayList<>();

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {}

    public float easeInOutCubic(float t) {
        return t < 0.2f ? 4 * t * t * t : (float) (1 - (float) Math.pow(-2 * t + 2, 3) / 1.2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public AbilityWheelScreen() {
        super(Component.literal("Ability Menu"));
        Helpers.syncAbilities();
    }

    private void onClick(List<String> abilityHolder, int finalI, Player player) {
        var updateAbility = abilityHolder.get(finalI);
        syncSelectedAbility(player, updateAbility);
    }

    private void onHoverClick(List<String> abilityHolder, int finalI, Player player) {
        if(!switchState){
            player.playSound(SoundReg.SELECT.get(), 1f, 1.4f);
            switchState = true;
            onClick(abilityHolder, finalI, player);
        }
    }

    private int posToSlice(double mouseX, double mouseY){
        var buttons = this.slots;
        var angle = Math.atan2(mouseY, mouseX) + (Math.PI / buttons) + Math.PI / 2 ;
        var index = Mth.floor(angle * buttons / (2 * Math.PI));
        if(index < 0) index += buttons;
        return index;
    }

    public static List<String> getAllAbilities(ItemStack wand){
        var player = Minecraft.getInstance().player;
        var names = new ArrayList<String>();
        var data = player.getData(AttachmentReg.CASTER_DATA);

        for (var unlockedAbility : data.getUnlockedAbilities()) {
            names.add(unlockedAbility.abilityName());
        }
        return names;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        var x = mouseX - (double) this.width /2;
        var y = mouseY - (double) this.height /2;
        var distance = x * x + y * y;
        var radius = RADIUS * 1.6;
        var innerRadius = RADIUS * 0.14;
        if(distance > radius * radius || distance < innerRadius * innerRadius) return;
        var index = posToSlice(x, y);
        for (AbilityIconButton button : this.buttons) button.setFocused(false);
        if(index < this.buttons.size()) this.buttons.get(index).setFocused(true);
    }

    private void showSlotIndex(int finalI, int buttonX, int buttonY) {
        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    SharedUI.drawStringWithBackground(
                        guiGraphics, Minecraft.getInstance().font,
                        Component.literal(String.valueOf(finalI + 1)),
                        buttonX + 13, buttonY + 9,
                        1, ColourStore.SUB_HEADER_COLOUR, true
                    );
                }
            }
        );
    }

    private void getSelectedAbilityName(GuiGraphics guiGraphics) {
        int x = (this.width / 2);
        int y = (this.height / 2) - 5;
        if (localTick >= (RADIAL_SIZE - 20)) {
//            var getAbilityId = DataComponentHelper.getAbilityTypeWand(getMinecraft().player);
//            var ability
//            if(!getAbility.isEmpty()){
//                SharedUI.getAbilityNameWithColour(getAbility.getFirst(), holder, x, y - 90, true);
//                int width = (int) (getAbilityId.getPath().intern().length() * 3.5);
//                SharedUI.boxMaker(guiGraphics, x - width, y - 96, width, 10);
//            }
        }
    }

    @Override
    protected void init() {
        var player = this.getMinecraft().player;
        this.buttons.clear();
        if(player == null) return;
        var wand = Helpers.getUsedItem(player);
        var castingData = player.getData(AttachmentReg.CASTER_DATA);
        var abilityHolder = getAllAbilities(wand);
        var totalSlots = abilityHolder.size();
        int centerX = this.width / 2 + 2;
        int centerY = this.height / 2 + 2;
        double angleOffset = -Math.PI / 2.0;

        this.slots = totalSlots;
        this.buttonSize = RADIAL_SIZE / (5 + (totalSlots/4));

        for (int i = 0; i < totalSlots; i++) {
            double angle = angleOffset + 2 * Math.PI * i / totalSlots; // Calculate angle for each position
            int buttonX = (int) (centerX + RADIUS * Math.cos(angle)) - buttonSize / 2;
            int buttonY = (int) (centerY + RADIUS * Math.sin(angle)) - buttonSize / 2;

            if (!abilityHolder.isEmpty() && !AbilityReg.getSpellsByTypeId(abilityHolder.get(i)).isEmpty()) {
                abilityButton(abilityHolder, i, buttonX, buttonY, i, castingData, player);
            } else {
                showSlotIndex(i, buttonX, buttonY);
            }
        }
    }

    private void abilityButton(
        List<String> abilityHolder,
        int i,
        int buttonX,
        int buttonY,
        int finalI,
        CastingData castingData,
        Player player
    ) {
        var selectedAbility = AbilityReg.getFirstSpellByTypeId(castingData.getSelectedAbility());
        var ability = AbilityReg.getSpellsByTypeId(abilityHolder.get(i)).getFirst();
        var iconResource = ability.getAbilityIconLocation();
        var abilityButton = new WidgetSprites(iconResource, iconResource);
        var isSelected = Objects.equals(selectedAbility.isPresent() ? selectedAbility.get().getAbilityName() : "", ability.getAbilityName());
        var widget = new AbilityIconButton(buttonX - 2, buttonY - 2, abilityButton, buttonSize, pButton -> {}, isSelected,
            () -> onHoverClick(abilityHolder, finalI, player)
        );

        if(isSelected){
            selectedAbility.ifPresent(abilityRegistrars -> showConfig(castingData, player, abilityRegistrars, buttonX + 20, buttonY - 10));
        }
        this.addRenderableWidget(widget);
        buttons.add(widget);
    }

    private void setRadialTexture(GuiGraphics guiGraphics, int easedValue, float fade){
        int xRadial = (this.width - easedValue) / 2;
        int yRadial = (this.height - easedValue) / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fade);
        var atlasLocation = Helpers.res("textures/gui/ability_wheel_background.png");
        guiGraphics.blit(atlasLocation, xRadial, yRadial, 0, 0, easedValue, easedValue, easedValue, easedValue);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
        RenderSystem.disableBlend();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        var normalizedTick =  this.localTick / RADIAL_SIZE;
        var easedTick = easeInOutCubic(normalizedTick);
        var easedValue = (int) (easedTick * RADIAL_SIZE);
        var fps = getMinecraft().getFps();
        var tick = (this.localTick + (Math.max(12 - (fps/10), 2))) + delta ;
        var fade = this.localTick / 130;
        this.localTick = Math.min(tick, RADIAL_SIZE);
        setRadialTexture(guiGraphics, easedValue, fade > 0.7 ? fade : 0);

        if(switchState){
            this.rebuildWidgets();
            this.switchState = false;
        }
        if(this.localTick >= RADIAL_SIZE) {
            this.getSelectedAbilityName(guiGraphics);
            super.render(guiGraphics, mouseX, mouseY, delta);
        }
    }

    private void showConfig(CastingData castingData, Player player, Ability selectedAbility, int posX, int posY) {
        var configButton = new WidgetSprites(COG, COG);
        var configButtonSize = 20;
        var itemStack = Helpers.getUsedItem(player);
        if (selectedAbility.getElemenType() == ElementReg.utility()) {
            var filterOutBase = isConfigAbility(selectedAbility, castingData.getSelectedAbility(), itemStack);
            if(filterOutBase){
                this.addRenderableWidget(
                    new AbilityIconButton(
                        posX, posY,
                        configButton,
                        configButtonSize,
                        pButton -> this.getMinecraft().setScreen(getAugmentModificationScreenWand(itemStack, this)),
                        false,
                        () -> { }
                    )
                );
            }
        }
    }

    @SubscribeEvent
    public static void updateInputEvent(MovementInputUpdateEvent event) {
        if (Minecraft.getInstance().screen instanceof AbilityWheelScreen) {
            var settings = Minecraft.getInstance().options;
            var eInput = event.getInput();
            var window = Minecraft.getInstance().getWindow().getWindow();

            eInput.up = InputConstants.isKeyDown(window, settings.keyUp.getKey().getValue());
            eInput.down = InputConstants.isKeyDown(window, settings.keyDown.getKey().getValue());
            eInput.left = InputConstants.isKeyDown(window, settings.keyLeft.getKey().getValue());
            eInput.right = InputConstants.isKeyDown(window, settings.keyRight.getKey().getValue());

            eInput.forwardImpulse = eInput.up == eInput.down ? 0.0F : (eInput.up ? 1.0F : -1.0F);
            eInput.leftImpulse = eInput.left == eInput.right ? 0.0F : (eInput.left ? 1.0F : -1.0F);
            eInput.jumping = InputConstants.isKeyDown(window, settings.keyJump.getKey().getValue());
            eInput.shiftKeyDown = InputConstants.isKeyDown(window, settings.keyShift.getKey().getValue());

            if (Minecraft.getInstance().player.isMovingSlowly()) {
                eInput.leftImpulse = (float) ((double) eInput.leftImpulse * 0.3D);
                eInput.forwardImpulse = (float) ((double) eInput.forwardImpulse * 0.3D);
            }
        }
    }

}
