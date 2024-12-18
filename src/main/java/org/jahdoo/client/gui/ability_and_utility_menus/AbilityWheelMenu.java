package org.jahdoo.client.gui.ability_and_utility_menus;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.client.SharedUI;
import org.jahdoo.components.WandData;
import org.jahdoo.networking.packet.client2server.SelectedAbilityC2SPacket;
import org.jahdoo.networking.packet.client2server.StopUsingC2SPacket;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.components.DataComponentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.jahdoo.client.IconLocations.COG;
import static org.jahdoo.items.augments.AugmentItemHelper.*;

@EventBusSubscriber(Dist.CLIENT)
public class AbilityWheelMenu extends Screen  {
    private static final int RADIAL_SIZE = 150;
    private final int buttonSize = RADIAL_SIZE / 7 + 3 + 6;
    private float localTick = 60;
    private boolean switchState;
    private List<AbilityIconButton> buttons = new ArrayList<>();
    private int slots;
    private static final int RADIUS = (int) (8.4 * ((double) RADIAL_SIZE / 20) - 4); // Adjust radius

    public AbilityWheelMenu() {
        super(Component.literal("Ability Menu"));
    }


    public static List<String> getAllAbilities(ItemStack wand){
        var wandData = DataComponentRegistry.WAND_DATA.get();
        if(wand.has(wandData)) return wand.get(wandData).abilitySet();
        return new ArrayList<>();
    }

    @Override
    protected void init() {
        var player = this.getMinecraft().player;
        this.buttons.clear();
        if(player == null) return;
        var wand = player.getMainHandItem();
        var wandData = wand.get(DataComponentRegistry.WAND_DATA.get());
        var abilityHolder = getAllAbilities(wand); //number of positions
        var totalSlots = abilityHolder.size();
        int centerX = this.width / 2 + 2;
        int centerY = this.height / 2 + 2;
        double angleOffset = -Math.PI / 2.0; // Start position

        if(wandData == null) return;
        this.slots = wandData.abilitySlots();

        for (int i = 0; i < totalSlots; i++) {
            double angle = angleOffset + 2 * Math.PI * i / totalSlots; // Calculate angle for each position
            int buttonX = (int) (centerX + RADIUS * Math.cos(angle)) - buttonSize / 2;
            int buttonY = (int) (centerY + RADIUS * Math.sin(angle)) - buttonSize / 2;

            if (!abilityHolder.isEmpty() && !AbilityRegister.getSpellsByTypeId(abilityHolder.get(i)).isEmpty()) {
                abilityButton(abilityHolder, i, buttonX, buttonY, i, wandData, player);
            } else {
                showSlotIndex(i, buttonX, buttonY);
            }
        }
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

    private int posToSlice(double mouseX, double mouseY){
        var buttons = this.slots;
        var angle = Math.atan2(mouseY, mouseX) + (Math.PI / buttons) + Math.PI / 2 ;
        var index = Mth.floor(angle * buttons / (2 * Math.PI));
        if(index < 0) index += buttons;
        return index;
    }

    private void abilityButton(
        List<String> abilityHolder,
        int i,
        int buttonX,
        int buttonY,
        int finalI,
        WandData wandData,
        Player player
    ) {
        var selectedAbility = AbilityRegister.getFirstSpellByTypeId(wandData.selectedAbility());
        var ability = AbilityRegister.getSpellsByTypeId(abilityHolder.get(i)).getFirst();
        var iconResource = ability.getAbilityIconLocation();
        var abilityButton = new WidgetSprites(iconResource, iconResource);
        var isSelected = Objects.equals(selectedAbility.isPresent() ? selectedAbility.get().getAbilityName() : "", ability.getAbilityName());

        if(isSelected){
            selectedAbility.ifPresent(abilityRegistrars -> showConfig(wandData, player, abilityRegistrars, buttonX + 20, buttonY - 10));
        }

        var widget = new AbilityIconButton(
            buttonX - 2, buttonY - 2, abilityButton, buttonSize,
            pButton -> {},
            isSelected,
            () -> onHoverClick(abilityHolder, finalI, player)
        );

        this.addRenderableWidget(widget);

        buttons.add(widget);

    }

    private void onHoverClick(List<String> abilityHolder, int finalI, Player player) {
        if(!switchState){
            player.playSound(SoundRegister.SELECT.get(), 1f, 1.4f);
            switchState = true;
            onClick(abilityHolder, finalI, player);
        }
    }

    private void onClick(List<String> abilityHolder, int finalI, Player player) {
        var updateAbility = abilityHolder.get(finalI);
        DataComponentHelper.setAbilityTypeWand(player, updateAbility);
        PacketDistributor.sendToServer(new StopUsingC2SPacket());
        PacketDistributor.sendToServer(new SelectedAbilityC2SPacket(updateAbility));
    }

    private void showConfig(WandData wandData, Player player, AbilityRegistrar selectedAbility, int posX, int posY) {
        var configButton = new WidgetSprites(COG, COG);
        var configButtonSize = 20;
        var itemStack = player.getMainHandItem();
        if (selectedAbility.getElemenType() == ElementRegistry.UTILITY.get()) {
            var filterOutBase = isConfigAbility(selectedAbility, wandData.selectedAbility(), itemStack);
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

    private void showSlotIndex(int finalI, int buttonX, int buttonY) {
        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    SharedUI.drawStringWithBackground(
                        guiGraphics,
                        Minecraft.getInstance().font,
                        Component.literal(String.valueOf(finalI + 1)),
                        buttonX + 13,
                        buttonY + 9,
                        1, -1, true
                    );
                }
            }
        );
    }

//    Yoinked from Ars Nouveau
    @SubscribeEvent
    public static void updateInputEvent(MovementInputUpdateEvent event) {
        if (Minecraft.getInstance().screen instanceof AbilityWheelMenu) {
            Options settings = Minecraft.getInstance().options;
            Input eInput = event.getInput();
            long window = Minecraft.getInstance().getWindow().getWindow();

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


    public float easeInOutCubic(float t) {
        return t < 0.2f ? 4 * t * t * t : (float) (1 - (float) Math.pow(-2 * t + 2, 3) / 1.2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void getSelectedAbilityName(GuiGraphics guiGraphics) {
        int x = (this.width / 2);
        int y = (this.height / 2) - 5;
        if (localTick >= (RADIAL_SIZE - 20)) {
            var getAbilityId = DataComponentHelper.getAbilityTypeWand(getMinecraft().player);
            var getAbility = AbilityRegister.getSpellsByTypeId(getAbilityId.getPath().intern());
           if(!getAbility.isEmpty()){
                SharedUI.getAbilityNameWithColour(getAbility.getFirst(), guiGraphics, x, y - 90, true);
               int width = (int) (getAbilityId.getPath().intern().length() * 3.5);
               SharedUI.boxMaker(guiGraphics, x - width, y - 96, width, 10, SharedUI.BORDER_COLOUR);
           }
        }
    }

    private void setRadialTexture(GuiGraphics guiGraphics, int easedValue, float fade){
        int xRadial = (this.width - easedValue) / 2;
        int yRadial = (this.height - easedValue) / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fade);
        var atlasLocation = ModHelpers.res("textures/gui/ability_wheel_background.png");
        guiGraphics.blit(atlasLocation, xRadial, yRadial, 0, 0, easedValue, easedValue, easedValue, easedValue);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
        RenderSystem.disableBlend();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if(switchState){
            this.rebuildWidgets();
            this.switchState = false;
        }
        var normalizedTick =  this.localTick / RADIAL_SIZE;
        var easedTick = easeInOutCubic(normalizedTick);
        var easedValue = (int) (easedTick * RADIAL_SIZE);
        var fps = getMinecraft().getFps();
        var tick = (this.localTick + (Math.max(12 - (fps/10), 2))) + delta ;
        var fade = this.localTick / 130;
        this.localTick = Math.min(tick, RADIAL_SIZE);
        setRadialTexture(guiGraphics, easedValue, fade > 0.7 ? fade : 0);

        if(this.localTick >= RADIAL_SIZE) {
            this.getSelectedAbilityName(guiGraphics);
            super.render(guiGraphics, mouseX, mouseY, delta);
        }


    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {}

}
