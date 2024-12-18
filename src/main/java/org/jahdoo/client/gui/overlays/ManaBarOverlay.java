package org.jahdoo.client.gui.overlays;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.attachments.CastingData;
import org.jahdoo.client.IconLocations;
import org.jahdoo.client.SharedUI;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.Config;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.components.DataComponentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.network.chat.Component.translatable;
import static org.jahdoo.client.SharedUI.BORDER_COLOUR;
import static org.jahdoo.client.SharedUI.drawStringWithBackground;
import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;
import static org.jahdoo.utils.ModHelpers.doubleFormattedDouble;
import static org.jahdoo.utils.ModHelpers.roundNonWholeString;

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

        attributeStats(pGuiGraphics, minecraft, player);

        var manaBarWidth = 57;
        var abilityRegistrars = AbilityRegister.REGISTRY.get(DataComponentHelper.getAbilityTypeWand(player));
        var casterData = player.getData(CASTER_DATA);
        var manaPool = casterData.getManaPool();
        var maxMana = casterData.getMaxMana(player);
        var manaProgress = maxMana != 0 && manaPool != 0 ? (int) (manaPool * manaBarWidth / maxMana) : 0;

        this.alignedGuiInstance(pGuiGraphics);
        this.setFadeGui(player);
        this.alignedGui.setScale(2);

        pGuiGraphics.pose().pushPose();
        RenderSystem.enableBlend();
        if(!Config.CUSTOM_UI.get()){
            pGuiGraphics.pose().translate(0, -fadeIn, 0);
            RenderSystem.setShaderColor(1f, 1f, 1f, fadeIn);
        }

        //Container
        alignedGui.displayGuiLayer(1, 29, 120, 89, 29);
        alignedGui.displayGuiLayer(40, 25, 108, 52, 3);

        this.setTypeOverlay(alignedGui, player, manaProgress + 3);
        this.cooldownOverlay(abilityRegistrars, casterData, pGuiGraphics, minecraft);
        this.manaPoolCount(casterData, pGuiGraphics, minecraft);
        if(Config.CUSTOM_UI.get()){
            Minecraft.getInstance().gui.renderSelectedItemName(pGuiGraphics, 94);
            playerStats(pGuiGraphics, player);
            experienceNumber(minecraft, pGuiGraphics, player);
            inventory(pGuiGraphics, player);
        }

        pGuiGraphics.pose().popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void attributeStats(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player) {
        if(!InputConstants.isKeyDown(minecraft.getWindow().getWindow(), InputConstants.KEY_TAB)) return;
        var attSpacer = new AtomicInteger();
        var syncableAttributes = player.getAttributes().getSyncableAttributes();
        var startX = 14;
        var startY = 14;

        for (AttributeInstance syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getValue() > 0) {
                var prefix = translatable(syncableAttribute.getAttribute().value().getDescriptionId());
                var readableValues = roundNonWholeString(doubleFormattedDouble(syncableAttribute.getValue()));
                var suffix = ModHelpers.withStyleComponent(" " + readableValues, -9882);
                if(modName.equals("jahdoo")){
                    var string = prefix.append(suffix);
                    pGuiGraphics.drawString(minecraft.font, string, startX, startY + attSpacer.get(), -1);
                    attSpacer.set(attSpacer.get() + 10);
                }
            }
        }

        SharedUI.boxMaker(pGuiGraphics, startX - 4, startY - 4, 100, attSpacer.get()/2 + 3, BORDER_COLOUR);
    }

//    private static void attributeStats(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player) {
//        if(!InputConstants.isKeyDown(minecraft.getWindow().getWindow(), InputConstants.KEY_TAB)) return;
//        var attSpacer = new AtomicInteger();
//        var syncableAttributes = player.getAttributes().getSyncableAttributes();
//        var startX = 14;
//        var startY = 14;
//
//        for (AttributeInstance syncableAttribute : syncableAttributes) {
//            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
//            if (syncableAttribute.getValue() > 0) {
//                var prefix = translatable(syncableAttribute.getAttribute().value().getDescriptionId());
//                var readableValues = roundNonWholeString(doubleFormattedDouble(syncableAttribute.getValue()));
//                var suffix = ModHelpers.withStyleComponent(" " + readableValues, -9882);
//                if(modName.equals("jahdoo")){
//                    var string = prefix.append(suffix);
//                    pGuiGraphics.drawString(minecraft.font, string, startX, startY + attSpacer.get(), -1);
//                    attSpacer.set(attSpacer.get() + 10);
//                }
//            }
//        }
//
//        SharedUI.boxMaker(pGuiGraphics, startX - 4, startY - 4, 100, attSpacer.get()/2 + 3, BORDER_COLOUR);
//    }

    private static void inventory(@NotNull GuiGraphics pGuiGraphics, LocalPlayer player) {
        var selectedIndex = player.getInventory().selected;
        var current = player.getInventory().getItem(selectedIndex);
        int prevIndex = selectedIndex - 1 < 0 ? 8 : selectedIndex - 1;
        var previous = player.getInventory().getItem(prevIndex);
        int nextIndex = selectedIndex + 1 > 8 ? 0 : selectedIndex + 1;
        var next = player.getInventory().getItem(nextIndex);
        var y = pGuiGraphics.guiHeight() - 60;
        var x = pGuiGraphics.guiWidth() / 2 - 9;

        var unSelected = IconLocations.GUI_ITEM_SLOT;
        var alpha = 0.6f;
        var textColour = -7303024;
        renderSlot(pGuiGraphics, next, x + 20, y, unSelected, nextIndex + 1, textColour, alpha);
        renderSlot(pGuiGraphics, current, x , y, IconLocations.GUI_GENERAL_SLOT, selectedIndex + 1, -12698050, 1f);
        renderSlot(pGuiGraphics, previous, x - 20, y, unSelected, prevIndex + 1, textColour, alpha);

//        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(current.getCount()), x, y, -1);
//        pGuiGraphics.renderFakeItem(current, x, y);
//        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(previous.getCount()), x - 20, y, -1);
//        pGuiGraphics.renderFakeItem(previous, x - 20, y);
    }

    private static void renderSlot(@NotNull GuiGraphics pGuiGraphics, ItemStack next, int x, int y, ResourceLocation lit, int index, int textColour, float alpha) {
        var count = next.getCount();
        pGuiGraphics.renderFakeItem(next, x, y);
        int size = 24;
        inventoryIndex(pGuiGraphics, x, y, index,textColour);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        pGuiGraphics.blit(lit, x-4,y-4,0,0, size, size, size, size);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        if(count > 1){
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0,0,170);
            pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(count), x + 15, y + 10, -6710887);
            pGuiGraphics.pose().popPose();
        }
    }

    private static void inventoryIndex(@NotNull GuiGraphics pGuiGraphics, int x, int y, int index, int textColour) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(8.2,4.2,5d);
        SharedUI.centeredStringNoShadow(pGuiGraphics, Minecraft.getInstance().font,  Component.literal(String.valueOf(index)), x, y, textColour, false);
        pGuiGraphics.pose().popPose();
    }

    private void playerStats(GuiGraphics guiGraphics, LocalPlayer player) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0.3,0);
        var shiftX = -81;
        var shiftY = -12;
        var health = player.getHealth();
        var food = player.getFoodData();
        var goldenHearts = player.getAbsorptionAmount();
        var experienceProgress = player.experienceProgress;
        var experienceWidth = (int) (20 * experienceProgress);
        alignedGui.displayGuiLayer(31 + shiftX, 24 + shiftY, 70, 60, 8);
        alignedGui.displayGuiLayer(31 + shiftX, 31 + shiftY, 70, 60, 8);
        alignedGui.displayGuiLayer(31 + shiftX, 38 + shiftY, 70, 60, 8);
        layoutStatOverlays(goldenHearts, shiftX-1, shiftY + 1, 30);
        layoutStatOverlays(food.getSaturationLevel(), shiftX-1, shiftY - 6, 37);
        layoutStat(experienceWidth, shiftX, shiftY + 7, 44);
        layoutStat(food.getFoodLevel(), shiftX, shiftY - 7, 52);
        layoutStat(health, shiftX, shiftY, 60);
        guiGraphics.pose().popPose();

    }

    private void experienceNumber(Minecraft minecraft, GuiGraphics guiGraphics, LocalPlayer player) {
        var experienceLevel = player.experienceLevel;
        var experience = Component.literal(String.valueOf(experienceLevel));
        var i = guiGraphics.guiHeight();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate( this.alignedGui.shiftGuiX - 20.8,  i - this.alignedGui.shiftGuiY - 28.1, 10D);
        guiGraphics.pose().scale(0.5f,0.5f,0.5f);
        drawStringWithBackground(guiGraphics, minecraft.font, experience, 0, 0, 1,  8453920, true);
        guiGraphics.pose().popPose();
    }

    private void layoutStatOverlays(float health, int shiftX, int shiftY, int texture) {
        var spacer = new AtomicInteger();
        for(int i = 0; i < health; i+=2){
            alignedGui.displayGuiLayer(33 + shiftX + spacer.get(), 29 + shiftY, texture, 6, 6);
            spacer.set(spacer.get() + 5);
        }
    }

    private void layoutStat(float stat, int shiftX, int shiftY, int texture) {
        var spacer = new AtomicInteger();
        for(int i = 0; i < stat; i++){
            if(i%2==0){
                alignedGui.displayGuiLayer(34 + shiftX + spacer.get(), 28 + shiftY, texture, 2, 2);
            } else {
                alignedGui.displayGuiLayer(33 + shiftX + spacer.get(), 29 + shiftY, texture + 3, 4, 4);
                spacer.set(spacer.get() + 5);
            }
        }
    }

    private void manaPoolCount(CastingData casterData, GuiGraphics pGuiGraphics, Minecraft minecraft){
        var height = pGuiGraphics.guiHeight();
        var manaPoolCount = Component.literal(String.valueOf(Math.round(casterData.getManaPool())));
        var colourBack = -13816531;
        var colourText = ElementRegistry.getElementByTypeId(types);
        if(!colourText.isEmpty()){
            var pose = pGuiGraphics.pose();
            pose.pushPose();
            pose.translate(58 + this.alignedGui.shiftGuiX - 0.1, height - 16.1 - this.alignedGui.shiftGuiY, 10D);
            pose.scale(0.5f,0.5f,0.5f);
            drawStringWithBackground(pGuiGraphics, minecraft.font, manaPoolCount, 0, 0, colourBack,  -4276546, true);
            pose.popPose();
        }
    }

    private void cooldownOverlay(AbilityRegistrar ability, CastingData casterData, GuiGraphics pGuiGraphics, Minecraft minecraft){
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
            var alignedGui1 = new AlignedGui(pGuiGraphics, height, width);
            if(Config.CUSTOM_UI.get()){
                alignedGui1.offsetGui(width / 2 - 16, 10);
            }
            this.alignedGui = alignedGui1;
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
