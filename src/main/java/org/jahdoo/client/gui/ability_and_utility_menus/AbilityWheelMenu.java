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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.client.SharedUI;
import org.jahdoo.components.WandData;
import org.jahdoo.networking.packet.client2server.SelectedAbilityC2SPacket;
import org.jahdoo.networking.packet.client2server.StopUsingC2SPacket;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.components.DataComponentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.jahdoo.client.gui.IconLocations.COG;
import static org.jahdoo.items.augments.AugmentItemHelper.*;

@EventBusSubscriber(Dist.CLIENT)
public class AbilityWheelMenu extends Screen  {
    private final int buttonSize = RADIAL_SIZE / 7 + 3;
    private static final int RADIAL_SIZE = 150;
    private float localTick = 60;

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
        if(player == null) return;
        var wand = player.getMainHandItem();
        var wandData = wand.get(DataComponentRegistry.WAND_DATA.get());
        var abilityHolder = getAllAbilities(wand); //number of positions
        var totalSlots = abilityHolder.size();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int radius = (int) (8.4 * ((double) RADIAL_SIZE / 20) - 3.3); // Adjust radius
        double angleOffset = -Math.PI / 2.0; // Start position

        for (int i = 0; i < totalSlots; i++) {
            double angle = angleOffset + 2 * Math.PI * i / totalSlots; // Calculate angle for each position
            int buttonX = (int) (centerX + radius * Math.cos(angle)) - buttonSize / 2;
            int buttonY = (int) (centerY + radius * Math.sin(angle)) - buttonSize / 2;

            if (!abilityHolder.isEmpty() && !AbilityRegister.getSpellsByTypeId(abilityHolder.get(i)).isEmpty()) {
                AbilityButton(abilityHolder, i, buttonX, buttonY, i, wandData, player);
            } else {
                showSlotIndex(i, buttonX, buttonY);
            }
        }
    }

    private void AbilityButton(
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

        this.addRenderableWidget(
            new AbilityIconButton(
                buttonX, buttonY, abilityButton, buttonSize,
                pButton -> {
                    var updateAbility = abilityHolder.get(finalI);
                    DataComponentHelper.setAbilityTypeWand(player, updateAbility);
                    PacketDistributor.sendToServer(new StopUsingC2SPacket());
                    PacketDistributor.sendToServer(new SelectedAbilityC2SPacket(updateAbility));
                    this.rebuildWidgets();
                },
                isSelected
            )
        );

        if(isSelected){
            selectedAbility.ifPresent(abstractAbility -> showConfig(wandData, player, abstractAbility, buttonX + 10, buttonY - 10));
        }
    }

    private void showConfig(WandData wandData, Player player, AbstractAbility selectedAbility, int posX, int posY) {
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
                        false
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
        int x = this.width / 2;
        int y = this.height / 2;
        if (localTick >= (RADIAL_SIZE - 20)) {
            ResourceLocation getAbilityId = DataComponentHelper.getAbilityTypeWand(getMinecraft().player);
            List<AbstractAbility> getAbility = AbilityRegister.getSpellsByTypeId(getAbilityId.getPath().intern());
           if(!getAbility.isEmpty()){
                SharedUI.getAbilityNameWithColour(getAbility.getFirst(), guiGraphics, x, y - 90, true);
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
        this.getSelectedAbilityName(guiGraphics);
        RenderSystem.disableBlend();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        float normalizedTick =  this.localTick / RADIAL_SIZE;
        float easedTick = easeInOutCubic(normalizedTick);
        int easedValue = (int) (easedTick * RADIAL_SIZE);
        float frameTimeNs = getMinecraft().getFrameTimeNs();
        float currentFrame = getMinecraft().getFps();
        var tick = this.localTick + Math.clamp(frameTimeNs / (currentFrame * 11000), 2, 20);
        var fade = this.localTick/120;
        this.localTick = Math.min(tick, RADIAL_SIZE);
        setRadialTexture(guiGraphics, easedValue, fade > 0.7 ? fade : 0);

        if(this.localTick >= RADIAL_SIZE) super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {}

}
