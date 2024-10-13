package org.jahdoo.client.gui.mana_ability_overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.DataComponentHelper;
import org.jahdoo.utils.KeyBinding;

import static org.jahdoo.client.SharedUI.drawStringWithBackground;
import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class ManaBarOverlay implements LayeredDraw.Layer {
    float fadeIn;
    public static final ResourceLocation MANA_GUI = GeneralHelpers.modResourceLocation("textures/gui/mana_v4_textured.png");
    public static final ResourceLocation TYPE_OVERLAY = GeneralHelpers.modResourceLocation("textures/gui/man_type_overlay.png");
    public static final ResourceLocation MANA_TYPE = GeneralHelpers.modResourceLocation("textures/gui/mana_with_type.png");
    private int types;

    float textFade;

    @Override
    public void render(GuiGraphics pGuiGraphics, DeltaTracker pDeltaTracker) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var screen = minecraft.screen;
        var width = pGuiGraphics.guiWidth();
        var height = pGuiGraphics.guiHeight();
        var manaBarWidth = 57;
        var alignedGui = new AlignedGui(pGuiGraphics, height, width);
        var abstractAbility = AbilityRegister.REGISTRY.get(DataComponentHelper.getAbilityTypeWand(player));

        if(player == null || screen != null) return;

        if (player.getMainHandItem().getItem() instanceof WandItem) {
            if (fadeIn < 1) fadeIn += 0.05f;
        } else {
            if (this.fadeIn > 0) fadeIn -= 0.05f;
        }

        var casterData = player.getData(CASTER_DATA);
        var manaPool = casterData.getManaPool();
        var maxMana = casterData.getMaxMana(player);
        var manaProgress = maxMana != 0 && manaPool != 0 ? (int) (manaPool * manaBarWidth / maxMana) : 0;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fadeIn);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, -fadeIn, 0);
        //Container
        alignedGui.displayGuiLayer(1, 29, 120, 89, 29);
        alignedGui.displayGuiLayer(40, 25, 108, 52, 3);
        this.setTypeOverlay(alignedGui, player, manaProgress + 3);

        if (abstractAbility != null) {

            alignedGui.displayGuiLayer(4, 26, 0, 0, 23, abstractAbility.getAbilityIconLocation());
            if (casterData.isAbilityOnCooldown(abstractAbility.setAbilityId())) {
                var cooldownCost = casterData.getStaticCooldown(abstractAbility.setAbilityId());
                var cooldownStatus = casterData.getCooldown(abstractAbility.setAbilityId());

                var cooldownOverlaySize = 20;
                var currentOverlayHeight = ((cooldownStatus) * cooldownOverlaySize / cooldownCost);
                alignedGui.displayGuiLayer(6, 5 + currentOverlayHeight, 89, cooldownOverlaySize, currentOverlayHeight);
            }
        }

        var manaPoolCount = Component.literal(String.valueOf(Math.round(casterData.getManaPool())));
        var colourBack = -13816531;
        var colourText = ElementRegistry.getElementByTypeId(types);

        if(!colourText.isEmpty()){
            drawStringWithBackground(pGuiGraphics, minecraft.font, manaPoolCount, 58, height - 25, colourBack, colourText.getFirst().textColourSecondary(), true);
        }

        RenderSystem.disableBlend();
        pGuiGraphics.pose().popPose();
    }


    public void setTypeOverlay(AlignedGui alignedGui, Player player, int manaProgress){
        var type = player.getMainHandItem();
        var element = ElementRegistry.getElementByWandType(type.getItem());
        if(!element.isEmpty()) this.types = element.get(0).getTypeId();

        if(types > 0){
            int[] typeOverlay = {103, 128, 78, 53, 28, 3};
            int[] manaOverlay = {35, 43, 27, 11, 19, 20};
            alignedGui.displayGuiLayer(0, 27, 0, typeOverlay[types - 1], 88, 25, TYPE_OVERLAY);
            alignedGui.displayGuiLayer(25, 18, 0, manaOverlay[types - 1], manaProgress, 8, MANA_TYPE);
        }
    }

    public static class AlignedGui {
        GuiGraphics guiGraphics;
        private int shiftGuiX;
        private int shiftGuiY;
        private final int screenWidth;
        private final int screenHeight;

        public AlignedGui(GuiGraphics guiGraphics, int screenHeight, int screenWidth){
            this.guiGraphics = guiGraphics;
            this.screenHeight = screenHeight;
            this.screenWidth = screenWidth;
        }

        public void displayGuiLayer(int xA, int yA, int offsetY, int barSizeXb, int barSizeYb){
            int positionX = xA + shiftGuiX;
            int positionY = screenHeight - yA - shiftGuiY;
            guiGraphics.blit(MANA_GUI, positionX, positionY, 77, offsetY, barSizeXb, barSizeYb);
        }

        public void displayGuiLayer(int xA, int yA, int offsetX, int offsetY, int iconSize, ResourceLocation resourceLocation){
            int positionX = xA + shiftGuiX;
            int positionY = screenHeight - yA - shiftGuiY;
            guiGraphics.blit(resourceLocation, positionX, positionY, offsetX, offsetY, iconSize, iconSize, iconSize, iconSize);
        }
        public void displayGuiLayer(int xA, int yA, int offsetX, int offsetY, int iconSizeX, int iconSizeY, ResourceLocation resourceLocation){
            int positionX = xA + shiftGuiX;
            int positionY = screenHeight - yA - shiftGuiY;
            guiGraphics.blit(resourceLocation, positionX, positionY, offsetX, offsetY, iconSizeX, iconSizeY);
        }

        public void offsetGui(int shiftGuiX, int shiftGuiY){
            this.shiftGuiX = shiftGuiX;
            this.shiftGuiY = shiftGuiY;
        }
    }

}
