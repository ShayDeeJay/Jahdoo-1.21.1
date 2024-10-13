package org.jahdoo.client.gui.ability_and_utility_menus;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.client.SharedUI;
import org.jahdoo.networking.packet.SelectedAbilityC2SPacket;
import org.jahdoo.networking.packet.StopUsingC2SPacket;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.DataComponentHelper;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class AbilityWheelMenu extends Screen  {
    public AbilityWheelMenu() {
        super(Component.literal("Ability Menu"));
    }

    private float localTick = 50;
    public static final int RADIAL_SIZE = 150;
    int buttonSize = RADIAL_SIZE / 7 + 3;

    public static List<String> getAllAbilities(ItemStack wand){
        return wand.get(DataComponentRegistry.WAND_DATA.get()).abilitySet();
    }

    @Override
    protected void init() {
        var player = this.getMinecraft().player;
        var wand = player.getMainHandItem();
        var abilityHolder = getAllAbilities(wand); // Define the number of positions around the circle
        var totalSlots = abilityHolder.size();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int radius = (int) (9 * (RADIAL_SIZE / 20) - 3.5); // Adjust the radius as needed
        double angleOffset = -Math.PI / 2.0; // Start from the top

        for (int i = 0; i < totalSlots; i++) {
            double angle = angleOffset + 2 * Math.PI * i / totalSlots; // Calculate angle for each position
            int buttonX = (int) (centerX + radius * Math.cos(angle)) - buttonSize / 2;
            int buttonY = (int) (centerY + radius * Math.sin(angle)) - buttonSize / 2;

            List<AbstractAbility> getCurrentAbility = AbilityRegister.getSpellsByTypeId(abilityHolder.get(i));
            if (!getCurrentAbility.isEmpty()) {
                ResourceLocation iconResource = getCurrentAbility.get(0).getAbilityIconLocation();
                WidgetSprites BUTTON = new WidgetSprites(iconResource, iconResource);
                int finalI = i;
                this.addRenderableWidget(
                    new ScreenButton(
                        buttonX, buttonY,
                        BUTTON,
                        buttonSize,
                        pButton -> {
                            PacketDistributor.sendToServer(new StopUsingC2SPacket());
                            PacketDistributor.sendToServer(new SelectedAbilityC2SPacket(abilityHolder.get(finalI)));
                        },
                        i
                    )
                );
            }
        }
    }

    //Yoinked from Ars Nouveau
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
        return t < 0.2f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
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
                SharedUI.getAbilityNameWithColour(getAbility.get(0), guiGraphics, x, y - 90, true);
            }
        }
    }

    private void setRadialTexture(GuiGraphics guiGraphics, int easedValue){
        int xRadial = (this.width - easedValue) / 2;
        int yRadial = (this.height - easedValue) / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.9F);
        guiGraphics.blit(GeneralHelpers.modResourceLocation("textures/gui/ability_wheel_background.png"), xRadial, yRadial, 0, 0, easedValue, easedValue, easedValue, easedValue);
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

        float normalizedTick = localTick / RADIAL_SIZE;
        float easedTick = easeInOutCubic(normalizedTick);
        int easedValue = (int) (easedTick * RADIAL_SIZE);
        this.getSelectedAbilityName(guiGraphics);
        setRadialTexture(guiGraphics, easedValue);
        if(localTick <= RADIAL_SIZE) this.localTick += 3f;
        if(localTick >= (RADIAL_SIZE - 20)) super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {}

}
