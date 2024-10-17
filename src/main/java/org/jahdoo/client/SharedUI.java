package org.jahdoo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import java.util.List;
import static org.jahdoo.all_magic.AbilityBuilder.*;

public class SharedUI {

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

            WandAbilityHolder wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            AbilityHolder abilityHolder = wandAbilityHolder.abilityProperties().get(abstractAbility.setAbilityId());
            AbilityHolder.AbilityModifiers abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            List<AbstractElement> abstractElement = ElementRegistry.getElementByTypeId((int) abilityModifiers.actualValue());


            if(!abstractElement.isEmpty()){
                element = abstractElement.get(0).textColourPrimary();
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

            WandAbilityHolder wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            AbilityHolder abilityHolder = wandAbilityHolder.abilityProperties().get(abstractAbility.setAbilityId());
            AbilityHolder.AbilityModifiers abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            List<AbstractElement> abstractElement = ElementRegistry.getElementByTypeId((int) abilityModifiers.actualValue());


            if(!abstractElement.isEmpty()){
                element = abstractElement.get(0);
            }

        } else {
            element = abstractAbility.getElemenType();
        }

        return element;
    }


    public static void handleSlotsInGridLayout(
        TriConsumer<Integer, Integer, Integer> slotAction,
        int totalSlots,
        int sharedScreenWidth,
        int shareScreenHeight,
        int xSpacing,
        int ySpacing
    ) {

        // Calculate the center of the screen
        int centerX = sharedScreenWidth / 2;
        int centerY = shareScreenHeight / 2;

        if (totalSlots <= 5) {
            // All slots in a single row
            int startX = centerX - (totalSlots - 1) * xSpacing / 2; // Center the row

            for (int i = 0; i < totalSlots; i++) {
                int slotX = startX + i * xSpacing;
                int slotY = centerY; // Vertically center the single row

                // Perform the slot-related action (add slot or adjust texture)
                slotAction.accept(slotX + 80, slotY + 50, i);
            }
        } else {
            // Split into two rows
            int slotsInTopRow = (totalSlots + 1) / 2; // Top row gets the extra slot if totalSlots is odd
            int slotsInBottomRow = totalSlots / 2;    // Bottom row takes the remaining slots

            // Calculate the initial offset for the grid layout
            int startXTopRow = centerX - (slotsInTopRow - 1) * xSpacing / 2;
            int startXBottomRow = centerX - (slotsInBottomRow - 1) * xSpacing / 2;
            int startYTopRow = centerY - ySpacing / 2; // Center top row vertically
            int startYBottomRow = centerY + ySpacing / 2; // Center bottom row below top row

            for (int i = 0; i < totalSlots; i++) {
                int slotX, slotY;

                if (i < slotsInTopRow) { // Top row
                    slotX = startXTopRow + i * xSpacing;
                    slotY = startYTopRow;
                } else { // Bottom row
                    int col = i - slotsInTopRow;
                    slotX = startXBottomRow + col * xSpacing;
                    slotY = startYBottomRow;
                }

                // Perform the slot-related action (add slot or adjust texture)
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
