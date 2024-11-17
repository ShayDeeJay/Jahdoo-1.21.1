package org.jahdoo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.jahdoo.client.gui.IconLocations;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.all_magic.AbilityBuilder.*;

public class SharedUI {

    public static void renderInventoryBackground(GuiGraphics guiGraphics, Screen screen, int IMAGE_SIZE, int yOffset){
        var x = (screen.width - IMAGE_SIZE) / 2;
        var y = (screen.height - IMAGE_SIZE) / 2;
        guiGraphics.blit(
            ModHelpers.res("textures/gui/wand_gui.png"),
            x, y + yOffset - 44,
            0,0, IMAGE_SIZE, IMAGE_SIZE
        );
    }

    public static void getAbilityNameWithColour(
        AbstractAbility abstractAbility,
        GuiGraphics guiGraphics,
        int posX,
        int posY,
        boolean isCenteredString
    ){
        var player = Minecraft.getInstance().player;
        if(player != null){
            Font font = Minecraft.getInstance().font;
            var element = getElementColour(abstractAbility, player.getMainHandItem());
            if (isCenteredString) {
                guiGraphics.drawCenteredString(font, abstractAbility.getAbilityName(), posX, posY, element);
                return;
            }
            guiGraphics.drawString(font, abstractAbility.getAbilityName(), posX, posY, element, false);
        }
    }

    public static int getElementColour(
        AbstractAbility abstractAbility,
        ItemStack itemStack
    ){
        int element;
        if (abstractAbility.isMultiType()) {
            var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abstractAbility.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            var abstractElement = ElementRegistry.getElementByTypeId((int) abilityModifiers.actualValue());

            if(!abstractElement.isEmpty()){
                element = abstractElement.getFirst().textColourPrimary();
            } else {
                element = -1;
            }
        } else {
            element = abstractAbility.getElemenType().textColourPrimary();
        }

        return element;
    }

    public static AbstractElement getElementWithType(
        AbstractAbility abstractAbility,
        ItemStack itemStack
    ){
        AbstractElement element = ElementRegistry.MYSTIC.get();
        if (abstractAbility.isMultiType()) {

            var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abstractAbility.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            var abstractElement = ElementRegistry.getElementByTypeId((int) abilityModifiers.actualValue());
            if(!abstractElement.isEmpty()) element = abstractElement.getFirst();

        } else {
            element = abstractAbility.getElemenType();
        }

        return element;
    }

    public static void setSlotTexture(GuiGraphics guiGraphics, int slotX, int slotY, int imageSize, String index){
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0, 100);
        drawStringWithBackground(
            guiGraphics,
            Minecraft.getInstance().font,
            Component.literal(index),
            slotX + 16, slotY - 5,
            -14145496, -6250336,
            true
        );
        guiGraphics.pose().popPose();
        guiGraphics.blit(IconLocations.GUI_AUGMENT_SLOT_V2, slotX, slotY, 0, 0, imageSize, imageSize, imageSize , imageSize);
    }

    public static void abilityIcon(GuiGraphics guiGraphics, ItemStack cachedItem, int width, int height, int offset, int localImageSize){
        var verticalOffset = 38 + offset;
        var shrinkBy = 16;
        var imageWithShrink = localImageSize - shrinkBy;
        var posX = (width - localImageSize) / 2 ;
        var posY = (height - localImageSize) / 2 - 150 + verticalOffset;
        var posX1 = (width - imageWithShrink) / 2 ;
        var posY1 = (height - imageWithShrink) / 2 - 150 + verticalOffset;

        guiGraphics.blit(
            ModHelpers.res("textures/gui/gui_button.png"),
            posX, posY, 0, 0, localImageSize, localImageSize, localImageSize, localImageSize
        );

        if(cachedItem == null) return;
        var abstractAbility = AbilityRegister.getSpellsByTypeId(DataComponentHelper.getAbilityTypeItemStack(cachedItem));

        if(!abstractAbility.isEmpty()){
            if (!abstractAbility.getFirst().getAbilityIconLocation().getPath().isEmpty()) {
                if(abstractAbility.getFirst().getAbilityIconLocation() != null){
                    guiGraphics.blit(
                        abstractAbility.getFirst().getAbilityIconLocation(),
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
