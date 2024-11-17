package org.jahdoo.client.gui.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.capabilities.CastingData;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.components.DataComponentHelper;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.client.SharedUI.drawStringWithBackground;
import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class ManaBarOverlay implements LayeredDraw.Layer {
    float fadeIn;
    public static final ResourceLocation MANA_GUI = ModHelpers.res("textures/gui/mana_v4_textured.png");
    public static final ResourceLocation TYPE_OVERLAY = ModHelpers.res("textures/gui/man_type_overlay.png");
    public static final ResourceLocation MANA_TYPE = ModHelpers.res("textures/gui/mana_with_type.png");
    private int types;
    AlignedGui alignedGui;

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, @NotNull DeltaTracker pDeltaTracker) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if(player == null || minecraft.options.hideGui) return;
        var manaBarWidth = 57;
        var abstractAbility = AbilityRegister.REGISTRY.get(DataComponentHelper.getAbilityTypeWand(player));
        var casterData = player.getData(CASTER_DATA);
        var manaPool = casterData.getManaPool();
        var maxMana = casterData.getMaxMana(player);
        var manaProgress = maxMana != 0 && manaPool != 0 ? (int) (manaPool * manaBarWidth / maxMana) : 0;

        this.alignedGuiInstance(pGuiGraphics);
        this.setFadeGui(player);
        this.alignedGui.setScale(2);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, -fadeIn, 0);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, fadeIn);

        //Container
        alignedGui.displayGuiLayer(1, 29, 120, 89, 29);
        alignedGui.displayGuiLayer(40, 25, 108, 52, 3);
        this.setTypeOverlay(alignedGui, player, manaProgress + 3);
        this.cooldownOverlay(abstractAbility, casterData, pGuiGraphics, minecraft);
        this.manaPoolCount(casterData, pGuiGraphics, minecraft);

        pGuiGraphics.pose().popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void manaPoolCount(CastingData casterData, GuiGraphics pGuiGraphics, Minecraft minecraft){
        var height = pGuiGraphics.guiHeight();
        var manaPoolCount = Component.literal(String.valueOf(Math.round(casterData.getManaPool())));
        var colourBack = -13816531;
        var colourText = ElementRegistry.getElementByTypeId(types);
        if(!colourText.isEmpty()){
            var pose = pGuiGraphics.pose();
            pose.pushPose();
            pose.translate(58, height - 16, 10D);
            pose.scale(0.5f,0.5f,0.5f);
            drawStringWithBackground(pGuiGraphics, minecraft.font, manaPoolCount, 0, 0, colourBack,  -4276546, true);
            pose.popPose();
        }
    }

    private void cooldownOverlay(AbstractAbility ability, CastingData casterData, GuiGraphics pGuiGraphics, Minecraft minecraft){
        if (ability != null) {
            alignedGui.displayGuiLayer(4, 26, 0, 0, 23, ability.getAbilityIconLocation());
            if (casterData.isAbilityOnCooldown(ability.setAbilityId())) {
                var cooldownCost = casterData.getStaticCooldown(ability.setAbilityId());
                var cooldownStatus = casterData.getCooldown(ability.setAbilityId());
                var cooldownOverlaySize = 20;
                if(cooldownCost > 0){
                    var currentOverlayHeight = ((cooldownStatus) * cooldownOverlaySize / cooldownCost);
                    alignedGui.displayGuiLayer(6, 5 + currentOverlayHeight, 89, cooldownOverlaySize, currentOverlayHeight);
                }
            }
        }
    }

    public void setTypeOverlay(AlignedGui alignedGui, Player player, int manaProgress){
        var type = player.getMainHandItem();
        var element = ElementRegistry.getElementByWandType(type.getItem());
        if(!element.isEmpty()) this.types = element.getFirst().getTypeId();

        if(types > 0){
            int[] typeOverlay = {103, 128, 78, 53, 28, 3};
            int[] manaOverlay = {35, 43, 27, 11, 19, 20};
            alignedGui.displayGuiLayer(0, 27, 0, typeOverlay[types - 1], 88, 25, TYPE_OVERLAY);
            alignedGui.displayGuiLayer(25, 18, 0, manaOverlay[types - 1], manaProgress, 8, MANA_TYPE);
        }
    }

    private void alignedGuiInstance(GuiGraphics pGuiGraphics){
        var width = pGuiGraphics.guiWidth();
        var height = pGuiGraphics.guiHeight();
        if (alignedGui == null || alignedGui.getScreenWidth() != width || alignedGui.getScreenWidth() != height) {
            alignedGui = new AlignedGui(pGuiGraphics, height, width);
        }
    }

    private void setFadeGui(Player player){
        var fadeAmount = 0.07f;
        var wandItem = player.getMainHandItem().getItem();
        if (wandItem instanceof WandItem) {
            if (this.fadeIn < 1) this.fadeIn += fadeAmount;
        } else {
            if (this.fadeIn > 0) this.fadeIn -= fadeAmount;
        }
    }


    public static class AlignedGui {
        GuiGraphics guiGraphics;
        private int shiftGuiX;
        private int shiftGuiY;
        private final int screenWidth;
        private final int screenHeight;
        private int scale;

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

        public int getScreenHeight() {
            return screenHeight;
        }

        private void setScale(int scale){
            this.scale = scale;
        }

        public int getScreenWidth() {
            return screenWidth;
        }
    }

}
