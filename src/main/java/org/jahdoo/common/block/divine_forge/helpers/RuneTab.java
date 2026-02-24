package org.jahdoo.common.block.divine_forge.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.divine_forge.DivineForgeEntity;
import org.jahdoo.common.block.divine_forge.DivineForgeMenu;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.items.CoinSack;
import org.jahdoo.common.items.CoreItem;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.Optional;

import static com.mojang.blaze3d.platform.InputConstants.KEY_LSHIFT;
import static org.jahdoo.common.block.divine_forge.helpers.DivineForgeScreenShared.groupFade;
import static org.jahdoo.common.client.Icons.GUI_RUNE_SLOT;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.handleSlotsInGridLayout;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.slots.RuneSlot.removeCurrencyCost;
import static org.jahdoo.common.client.slots.RuneSlot.removeRuneCost;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.getGearData;

public class RuneTab {

    private static void handleSlots(
        GuiGraphics guiGraphics,
        boolean showInventory,
        int borderColour,
        DivineForgeMenu runeTableMenu,
        Integer slotX,
        Integer slotY,
        JahdooGearData getRunes,
        int spacer
    ) {
        if(showInventory){
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 100);

            var newSize = 10;
            var x = slotX + runeTableMenu.posX - 96;
            var y = slotY - runeTableMenu.posY - 9;
            var size = 32;

            guiGraphics.blit(GUI_RUNE_SLOT, x - 3, y + 8, 0, 0, size, size, size, size);

            if(!getRunes.runeSlots().get(spacer).isEmpty()) {
                boxMaker(guiGraphics, x + 3, y + 14, newSize, newSize, borderColour, 0);
            }

            guiGraphics.pose().popPose();
        }
    }

    public static int runeToolTip(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        Slot hoveredSlot,
        int borderColour,
        DivineForgeEntity entity,
        Minecraft minecraft
    ) {
        if(hoveredSlot instanceof RuneSlot && !hoveredSlot.getItem().isEmpty()){
            var item = hoveredSlot.getItem();
            var coreItemType = removeRuneCost(item);
            var coinCost = removeCurrencyCost(item);
            var shiftDown = ClientHelpers.isKeyDown(KEY_LSHIFT);

            var cost = new ItemStack(coreItemType);
            var prefix = "Extraction Cost: ";
            var text = cost.getHoverName().getString();
            var quantityCost = TextHelpers.withStyleComponent("1", borderColour);
            var list = new ArrayList<Component>();

            list.add(TextHelpers.withStyleComponent(prefix, borderColour));
            var coreAvailableColour = entity.checkAndChargeCores(coreItemType, false) ? CoreItem.getCoreColour(minecraft.level.getGameTime()) : ColourHelpers.getNegativeRed();
            list.add(TextHelpers.withStyleComponent("------------", uiColour()));

            list.add(TextHelpers.withStyleComponent(text + ": ", coreAvailableColour).copy().append(quantityCost));

            list.add(TextHelpers.withStyleComponent("------------", uiColour()));
            CoinSack.coinToolTip(list, coinCost);

            if(!shiftDown){
                list.add(TextHelpers.withStyleComponent("------------", uiColour()));
                var sibling = TextHelpers.withStyleComponentTrans("augmentHelper.jahdoo.shift", ColourHelpers.getOffWhite());
                list.add(TextHelpers.withStyleComponentTrans("augmentHelper.jahdoo.hold_details", ColourHelpers.getHeaderColour(), sibling));
            }

            var toolTipSpacer = shiftDown ? ((list.size() * 10) + 10) : 0;
            guiGraphics.renderTooltip(minecraft.font, list, Optional.empty(), mouseX, mouseY - toolTipSpacer);

            return coinCost;
        }

        return 0;
    }

    public static void runeSlotTexture(
        GuiGraphics guiGraphics,
        int width,
        int height,
        boolean showInventory,
        Minecraft minecraft,
        int borderColour,
        DivineForgeMenu runeTableMenu
    ) {
        var shiftY = 0;
        var shiftX = -5;
        var entity = runeTableMenu.tableEntity();
        var item = entity.getInputItemHandler().getStackInSlot(0);
        var font = minecraft.font;
        var getRunes = getGearData(item);

        handleSlotsInGridLayout(
            (slotX, slotY, index) -> handleSlots(guiGraphics, showInventory, borderColour, runeTableMenu, slotX, slotY, getRunes, index),
            getRunes.runeSlots().size(),
            width,
            height,
            runeTableMenu.offSetX - 6,
            runeTableMenu.offSetY - 2
        );

        var startX11 = width / 2 + shiftX - 38;
        var startY11 = height / 2 + shiftY - 89;
        var heightOffset = 86 - 36;

        boxMaker(guiGraphics, startX11, startY11, Math.max(10, 74), heightOffset, borderColour, groupFade());
        if(getRunes.runeSlots().isEmpty() && showInventory){
            guiGraphics.drawCenteredString(font, "No Slots Available", startX11 + 74, startY11 + 46, ColourHelpers.getSubHeaderColour());
        }
    }

}
