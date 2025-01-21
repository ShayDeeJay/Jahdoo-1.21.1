package org.jahdoo.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.client.IconLocations.*;

public class SharedUI {

    public static final int BOX_COLOUR = -804253680;
    public static final int BORDER_COLOUR =  -12434878;

    public static List<ResourceLocation> getOverlays(){
        return List.of(CORE, ADVANCED_AUGMENT_CORE, AUGMENT_HYPER_CORE);
    }

    public static void augmentCoreSlots(@NotNull GuiGraphics guiGraphics, int adjustX, int adjustY, int borderColour, int width, int height, int fade) {
        var spacer = new AtomicInteger();
        var posX = width / 2;
        var posY = height / 2;
        var startX = posX + adjustX - 159;
        var startY = posY - 62 + adjustY;
        boxMaker(guiGraphics, startX, startY, 17, 46, borderColour, fade);
        for(ResourceLocation location : getOverlays()){
            var offsetX = 158;
            var offsetY = 60;
            var x = posX - offsetX + adjustX;
            var y = posY + spacer.get() - offsetY + adjustY;
            guiGraphics.blit(GUI_ITEM_SLOT, x, y, 0,0,32,32,32,32);
            guiGraphics.blit(location, x, y, 0,0,32,32,32,32);
            spacer.set(spacer.get() + 28);
        }
    }

    public static List<Component> getComponents(ItemStack item){
        var components = new ArrayList<Component>();
        AugmentItemHelper.getHoverText(item, components, true);
        return components;
    }

    public static void boxMaker(GuiGraphics guiGraphics, int startX, int startY, int widthOffset, int heightOffset, int colourBorder) {
        int widthTo = startX + widthOffset * 2;
        int heightTo = startY + heightOffset * 2;

        guiGraphics.fill(startX, startY, widthTo, heightTo, BOX_COLOUR);
        guiGraphics.renderOutline(startX, startY, widthTo - startX, heightTo - startY, BORDER_COLOUR);
    }

    public static void boxMaker(GuiGraphics guiGraphics, int startX, int startY, int widthOffset, int heightOffset, int colourBorder, int fillColour) {
        int widthTo = startX + widthOffset * 2;
        int heightTo = startY + heightOffset * 2;

        guiGraphics.fill(startX, startY, widthTo, heightTo, fillColour);
        guiGraphics.renderOutline(startX, startY, widthTo - startX, heightTo - startY, colourBorder);
    }


    public static void boxMaker(GuiGraphics guiGraphics, int startX, int startY, int widthOffset, int heightOffset, int colourBorder, int start, int end) {
        int widthTo = startX + widthOffset * 2;
        int heightTo = startY + heightOffset * 2;

        guiGraphics.fillGradient(startX, startY, widthTo, heightTo, start, end);
        guiGraphics.renderOutline(startX, startY, widthTo - startX, heightTo - startY, colourBorder);
    }

    public static void renderExperienceBar(GuiGraphics guiGraphics, int x, int l, Minecraft minecraft) {
        var expBackground = ResourceLocation.withDefaultNamespace("hud/experience_bar_background");
        var expProgress = ResourceLocation.withDefaultNamespace("hud/experience_bar_progress");
        if(minecraft == null || minecraft.player == null) return;
        minecraft.getProfiler().push("expBar");
        int i = minecraft.player.getXpNeededForNextLevel();
        if (i > 0) {
            int k = (int)(minecraft.player.experienceProgress * 183.0F);
            guiGraphics.blitSprite(expBackground, x, l, 182, 5);
            if (k > 0) guiGraphics.blitSprite(expProgress, 182, 5, 0, 0, x, l, k, 5);
        }
        minecraft.getProfiler().pop();
    }

    public static void bezelMaker(GuiGraphics guiGraphics, int posX, int posY, int offsetX, int offsetY, int size, @Nullable AbstractElement element) {
        guiGraphics.blit(IconLocations.BEZEL_1, posX + offsetX, posY, 0, 0, size, size, size, size);
        guiGraphics.blit(IconLocations.BEZEL_2, posX, posY, 0, 0, size, size, size, size);
        guiGraphics.blit(IconLocations.BEZEL_3, posX + 1 , posY + offsetY, 0, 0, size, size, size, size);
        guiGraphics.blit(IconLocations.BEZEL_4, posX + offsetX, posY + offsetY, 0, 0, size, size, size, size);
        if(element != null){
            int[] manaOverlay = {150, 125, 100, 75, 50, 25, 0};
            var index = element.getTypeId();
            var blitOffsetX = manaOverlay[index];
            var blitOffsetY = blitOffsetX + 17;
            var offsetXOL = 13;

            guiGraphics.blit(TYPE_OVERLAY, posX + 13, posY + offsetXOL, blitOffsetX, blitOffsetX, 8, 8, 25, 150);
            guiGraphics.blit(TYPE_OVERLAY, posX + offsetX + 11, posY + offsetXOL, blitOffsetY, blitOffsetX, 8, 8, 25, 150);
            guiGraphics.blit(TYPE_OVERLAY, posX + 13, posY + offsetY + 11, blitOffsetX, blitOffsetY, 8, 8, 25, 150);
            guiGraphics.blit(TYPE_OVERLAY, posX + offsetX + 11, posY + offsetY + 11, blitOffsetY, blitOffsetY, 8, 8, 25, 150);
        }
    }

    public static void renderItem(@NotNull GuiGraphics guiGraphics, double width, double height, Player player, ItemStack itemStack, float partialTicks, float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(width, height, 400);
        guiGraphics.pose().scale(scale, scale, -scale);

//        guiGraphics.pose().rotateAround(Axis.YP.rotationDegrees(-5), 0,0,0); // Horizontal rotation
        guiGraphics.pose().rotateAround(Axis.ZP.rotationDegrees(45), 0,0,0); // Horizontal rotation

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            255,
            OverlayTexture.NO_OVERLAY,
            guiGraphics.pose(),
            buffer,
            Minecraft.getInstance().level,
            1
        );

        guiGraphics.pose().popPose();
        Lighting.setupForFlatItems();
    }

    public static void renderItem(@NotNull GuiGraphics guiGraphics, double width, double height, ItemStack itemStack, float scale, float mouseX, float mouseY, float rotationSpeed) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(width/2, height/2, 5);
        guiGraphics.pose().scale(scale, scale, -scale);

        var degrees = ((float) guiGraphics.guiWidth() / 2 - mouseX) / rotationSpeed;
        var maxRotation = 6;
        degrees = Math.max(-maxRotation, Math.min(maxRotation, degrees));
        var degrees1 = -((float) guiGraphics.guiHeight() / 2 - mouseY) / rotationSpeed;
        degrees1 = Math.max(-maxRotation, Math.min(maxRotation, degrees1));

        guiGraphics.pose().rotateAround(Axis.YP.rotationDegrees(degrees), 0,0,0); // Horizontal rotation
        guiGraphics.pose().rotateAround(Axis.XP.rotationDegrees(degrees1 + 180), 0,0,0); // Horizontal rotation

        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            15728880,
            OverlayTexture.NO_OVERLAY,
            guiGraphics.pose(),
            buffer,
            Minecraft.getInstance().level,
            1
        );

        guiGraphics.pose().popPose();
        Lighting.setupLevel();
        Lighting.setupForEntityInInventory();
    }

    public static void setCustomBackground(int height, int width, GuiGraphics guiGraphics){
        var widthOffset = 100;
        var heightOffset = 115;
        int i = width / 2;
        int i1 = height / 2;
        var widthFrom = i - widthOffset;
        var heightFrom = i1 - heightOffset;
        var widthTo = i + widthOffset;
        var heightTo = i1 + heightOffset;

        guiGraphics.fill(widthFrom, heightFrom, widthTo, heightTo, BOX_COLOUR);
        guiGraphics.hLine(i -100, i + 99, i1 - 70, BORDER_COLOUR);
        guiGraphics.renderOutline(widthFrom, heightFrom, widthTo - widthFrom, heightTo - heightFrom, BORDER_COLOUR);
        guiGraphics.enableScissor(0, heightFrom + 50, width, heightTo - 5);
    }

    public static void header(@NotNull GuiGraphics guiGraphics, int width, int height, ItemStack itemStack, Font font) {
        var yOff = 102;
        int xOff = width/2 - 55;
        guiGraphics.drawString(font, getComponents(itemStack).getFirst(), xOff, (height/2 - (yOff - 10)), 0, true);
        guiGraphics.drawString(font, AugmentItemHelper.getHoverName(itemStack), xOff, (height/2 - yOff), 0, true);
        abilityIcon(guiGraphics, itemStack, width - 155, height - 180, 109, 40, 12);
    }

    public static void renderInventoryBackground(GuiGraphics guiGraphics, Screen screen, int IMAGE_SIZE, int yOffset, boolean show){
        if(show){
            var x = (screen.width - IMAGE_SIZE) / 2;
            var y = (screen.height - IMAGE_SIZE) / 2;
            guiGraphics.blit(
                ModHelpers.res("textures/gui/wand_gui.png"),
                x, y + yOffset - 44,
                0, 0, IMAGE_SIZE, IMAGE_SIZE
            );
        }
    }

    public static void getAbilityNameWithColour(
        AbilityRegistrar abilityRegistrar,
        GuiGraphics guiGraphics,
        int posX,
        int posY,
        boolean isCenteredString
    ){
        var player = Minecraft.getInstance().player;
        if(player != null){
            Font font = Minecraft.getInstance().font;
            var element = getElementColour(abilityRegistrar, player.getItemInHand(player.getUsedItemHand()));
            if (isCenteredString) {
                guiGraphics.drawCenteredString(font, abilityRegistrar.getAbilityName(), posX, posY, element);
                return;
            }
            guiGraphics.drawString(font, abilityRegistrar.getAbilityName(), posX, posY, element, false);
        }
    }

    public static int getElementColourAugment(
        ItemStack itemStack
    ){
        var key = DataComponentHelper.getKeyFromAugment(itemStack);
        var abilityRegistrars = AbilityRegister.getFirstSpellByTypeId(key).orElse(AbilityRegister.BOLTZ.get());
        int element;
        if (abilityRegistrars.isMultiType()) {
            var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityRegistrars.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            var abstractElement = ElementRegistry.getElementByTypeId((int) abilityModifiers.actualValue());
            element = !abstractElement.isEmpty() ? abstractElement.getFirst().textColourPrimary() : -1;

        } else {
            element = abilityRegistrars.getElemenType().textColourPrimary();
        }

        return element;
    }

    public static int getFadedColourBackground(float alpha){
        var minecraft = Minecraft.getInstance();
        return minecraft.options.getBackgroundColor(alpha);
    }

    public static int getElementIdAugment(
        ItemStack itemStack
    ){
        var key = DataComponentHelper.getKeyFromAugment(itemStack);
        var abilityRegistrars = AbilityRegister.getFirstSpellByTypeId(key).orElse(AbilityRegister.BOLTZ.get());
        int element;
        if (abilityRegistrars.isMultiType()) {
            var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityRegistrars.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            var abstractElement = ElementRegistry.getElementByTypeId((int) abilityModifiers.actualValue());
            element = !abstractElement.isEmpty() ? abstractElement.getFirst().getTypeId() : -1;

        } else {
            element = abilityRegistrars.getElemenType().getTypeId();
        }

        return element;
    }

    public static int getElementColour(
        AbilityRegistrar abilityRegistrars,
        ItemStack itemStack
    ){
        int element;
        if (abilityRegistrars.isMultiType()) {
            var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityRegistrars.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            var abstractElement = ElementRegistry.getElementByTypeId((int) abilityModifiers.actualValue());
            element = !abstractElement.isEmpty() ? abstractElement.getFirst().textColourSecondary() : -1;

        } else {
            element = abilityRegistrars.getElemenType().textColourSecondary();
        }

        return element;
    }

    public static AbstractElement getElementWithType(
        AbilityRegistrar abilityRegistrars,
        ItemStack itemStack
    ){
        AbstractElement element = ElementRegistry.MYSTIC.get();
        if (abilityRegistrars.isMultiType()) {

            var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityRegistrars.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            var abstractElement = ElementRegistry.getElementByTypeId((int) abilityModifiers.actualValue());
            if(!abstractElement.isEmpty()) element = abstractElement.getFirst();

        } else {
            element = abilityRegistrars.getElemenType();
        }

        return element;
    }

    public static void setSlotTexture(GuiGraphics guiGraphics, int slotX, int slotY, int imageSize, String index){
        guiGraphics.pose().pushPose();
        double x = slotX + 16.2;
        double y = slotY + 10.4;
        guiGraphics.pose().translate(x, y, 2);
        guiGraphics.pose().scale(0.7f,0.7f, 0.7f);
        guiGraphics.pose().translate(0.2, 0.2, 0.2);

        centeredStringNoShadow(guiGraphics, Minecraft.getInstance().font, Component.literal(index), 0, 0, -10329502, false);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 2);
        guiGraphics.blit(GUI_AUGMENT_SLOT, slotX, slotY, 0, 0, imageSize, imageSize, imageSize , imageSize);
        guiGraphics.pose().popPose();
    }

    public static void setSlotTexture(GuiGraphics guiGraphics, int slotX, int slotY, int imageSize, String index, ItemStack itemStack){
        guiGraphics.pose().pushPose();
        double x = slotX + 16.2;
        double y = slotY + 10.4;
        guiGraphics.pose().translate(x, y, 2);
        guiGraphics.pose().scale(0.7f,0.7f, 0.7f);
        guiGraphics.pose().translate(0.2, 0.2, 0.2);

        centeredStringNoShadow(guiGraphics, Minecraft.getInstance().font, Component.literal(index), 0, 0, -10329502, false);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 2);
        guiGraphics.blit(GUI_AUGMENT_SLOT, slotX, slotY, 0, 0, imageSize, imageSize, imageSize , imageSize);
        abilityIcon(guiGraphics, itemStack,slotX * 2 + 33, slotY * 2 + 213, 0, 26, 10);
        guiGraphics.pose().popPose();
    }

    public static void centeredStringNoShadow(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int color, boolean shadow) {
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        guiGraphics.drawString(font, formattedcharsequence, x - font.width(formattedcharsequence) / 2, y, color, shadow);
    }

    public static void abilityIcon(GuiGraphics guiGraphics, ItemStack cachedItem, int width, int height, int offset, int localImageSize, int shrinkBy){
        var verticalOffset = 38 + offset;
        var imageWithShrink = localImageSize - shrinkBy;
        var posX = (width - localImageSize) / 2 ;
        var posY = (height - localImageSize) / 2 - 150 + verticalOffset;
        var posX1 = (width - imageWithShrink) / 2 ;
        var posY1 = (height - imageWithShrink) / 2 - 150 + verticalOffset;

        guiGraphics.blit(
            GUI_GENERAL_SLOT,
            posX, posY, 0, 0, localImageSize, localImageSize, localImageSize, localImageSize
        );

        if(cachedItem == null) return;
        var abilityRegistrars = AbilityRegister.getSpellsByTypeId(DataComponentHelper.getAbilityTypeItemStack(cachedItem));

        if(!abilityRegistrars.isEmpty()){
            if (!abilityRegistrars.getFirst().getAbilityIconLocation().getPath().isEmpty()) {
                if(abilityRegistrars.getFirst().getAbilityIconLocation() != null){
                    guiGraphics.blit(
                        abilityRegistrars.getFirst().getAbilityIconLocation(),
                        posX1, posY1, 0, 0, imageWithShrink, imageWithShrink, imageWithShrink, imageWithShrink
                    );
                }
            }
        }
    }

    public static void handleSlotsInGridLayout(
        TriConsumer<Integer, Integer, Integer> slotAction,
        int totalSlots,
        int sharedScreenWidth,
        int shareScreenHeight,
        int xSpacing,
        int ySpacing
    ) {
        int centerX = sharedScreenWidth / 2;
        int centerY = shareScreenHeight / 2;

        if (totalSlots <= 5) {
            int startX = centerX - (totalSlots - 1) * xSpacing / 2; // Center the row

            for (int i = 0; i < totalSlots; i++) {
                int slotX = startX + i * xSpacing;
                int slotY = centerY;
                slotAction.accept(slotX + 80, slotY + 50, i);
            }
        } else {
            int slotsInTopRow = (totalSlots + 1) / 2;
            int slotsInBottomRow = totalSlots / 2;

            int startXTopRow = centerX - (slotsInTopRow - 1) * xSpacing / 2;
            int startXBottomRow = centerX - (slotsInBottomRow - 1) * xSpacing / 2;
            int startYTopRow = centerY - ySpacing / 2;
            int startYBottomRow = centerY + ySpacing / 2;

            for (int i = 0; i < totalSlots; i++) {
                int slotX, slotY;

                if (i < slotsInTopRow) {
                    slotX = startXTopRow + i * xSpacing;
                    slotY = startYTopRow;
                } else {
                    int col = i - slotsInTopRow;
                    slotX = startXBottomRow + col * xSpacing;
                    slotY = startYBottomRow;
                }

                slotAction.accept(slotX + 80, slotY + 50, i);
            }
        }
    }

    public static void drawStringWithBackground(GuiGraphics guiGraphics, Font pFont, Component pText, int pX, int pY, int backgroundColour, int textColour, boolean isCentered) {
        guiGraphics.pose().pushPose();
        FormattedCharSequence formattedcharsequence = pText.getVisualOrderText();
        String s = pText.getString();
        int i1 = isCentered ? pX - pFont.width(formattedcharsequence) / 2 : pX;
        guiGraphics.drawString(pFont, s, i1 + 1, pY, backgroundColour, false);
        guiGraphics.drawString(pFont, s, i1 - 1, pY, backgroundColour, false);
        guiGraphics.drawString(pFont, s, i1, pY + 1, backgroundColour, false);
        guiGraphics.drawString(pFont, s, i1, pY - 1, backgroundColour, false);
        guiGraphics.drawString(pFont, s, i1, pY, textColour, false);
        guiGraphics.pose().popPose();
    }

}
