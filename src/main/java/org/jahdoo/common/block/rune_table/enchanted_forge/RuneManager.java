package org.jahdoo.common.block.rune_table.enchanted_forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.common.block.rune_table.DivineForgeEntity;
import org.jahdoo.common.block.rune_table.RuneTableMenu;
import org.jahdoo.common.client.overlay.WalletOverlay;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.items.CoinSack;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.block.rune_table.enchanted_forge.RuneTableScreen.groupFade;
import static org.jahdoo.common.client.Icons.GUI_GENERAL_SLOT;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.handleSlotsInGridLayout;
import static org.jahdoo.common.client.slots.RuneSlot.removeCurrencyCost;
import static org.jahdoo.common.client.slots.RuneSlot.removeRuneCost;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.getGearData;

public class RuneManager {

    public static boolean isShiftDown(Minecraft getMinecraft) {
        return InputConstants.isKeyDown(getMinecraft.getWindow().getWindow(), getMinecraft.options.keyShift.getKey().getValue());
    }

    public static void runeSlotTexture(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        int width,
        int height,
        boolean showInventory,
        Slot slot,
        Minecraft minecraft,
        int coinCost,
        int borderColour,
        RuneTableMenu runeTableMenu
    ) {
        var shiftY = 0;
        var shiftX = -5;
        var spacer = new AtomicInteger();
        var entity = runeTableMenu.tableEntity();
        var item = entity.inputItemHandler.getStackInSlot(0);
        var font = minecraft.font;

//        remainingPotential(guiGraphics, shiftX, spacer, shiftY, mouseX, mouseY, width, height, showInventory, slot, minecraft, runeTableMenu, coinCost, borderColour);
        var getRunes = getGearData(item);

        handleSlotsInGridLayout(
            (slotX, slotY, index) -> {
                if(showInventory){
                    for (ItemStack ignored : getRunes.runeSlots()) {
                        var size = 32;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0, 0, 100);
                        var x = slotX + runeTableMenu.posX - 96;
                        var y = slotY - runeTableMenu.posY - 9;
                        guiGraphics.blit(GUI_GENERAL_SLOT, x-3, y+8, 0, 0, size, size, size, size);
                        spacer.set(spacer.get() + runeTableMenu.runeYSpacer);
                        guiGraphics.pose().popPose();
                    }
                }
            },
            getRunes.runeSlots().size(),
            width,
            height,
            runeTableMenu.offSetX,
            runeTableMenu.offSetY
        );

        var startX11 = width/2 + shiftX - 38;
        var startY11 = height/2 + shiftY - 89;
        var heightOffset = 86 - 36;
        boxMaker(guiGraphics, startX11, startY11, Math.max(10, 74), heightOffset, borderColour, groupFade());

        if(getRunes.runeSlots().isEmpty() && showInventory){
            guiGraphics.drawCenteredString(font, "No Slots Available", startX11 + 74, startY11 + 46, SUB_HEADER_COLOUR);
        }

    }


    public static void runeToolTip(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        Slot hoveredSlot,
        int coinCost,
        int borderColour,
        DivineForgeEntity entity,
        Minecraft minecraft
    ) {
        if(hoveredSlot instanceof RuneSlot && !hoveredSlot.getItem().isEmpty()){
            var item = hoveredSlot.getItem();
            var coreItemType = removeRuneCost(item);
            coinCost = removeCurrencyCost(item);

            var cost = new ItemStack(coreItemType);
            CoreData.setFilled(cost);

            var prefix = "Extraction Cost: ";
            var text = cost.getHoverName().getString();
            var quantityCost = withStyleComponent("1", borderColour);
            var list = new ArrayList<Component>();

            list.add(withStyleComponent(prefix, borderColour));
            var coreAvailableColour = entity.checkAndChargeCores(coreItemType, false) ? SUB_HEADER_COLOUR : NEGATIVE_RED;

            list.add(withStyleComponent(text + ": ", coreAvailableColour).copy().append(quantityCost));
            CoinSack.coinToolTip(list, coinCost);

            if(!isShiftDown(minecraft)){
                list.add(withStyleComponent("Hold", HEADER_COLOUR).copy().append(withStyleComponent(" [Shift] ", OFF_WHITE)));
            }

            var toolTipSpacer = isShiftDown(minecraft) ? ((list.size() * 10) + 10) : 0;
            guiGraphics.renderTooltip(minecraft.font, list, Optional.empty(), mouseX, mouseY - toolTipSpacer);
        }
        WalletOverlay.renderWallet(guiGraphics, minecraft, 1, 10, -20, true, coinCost > 0 ? PlayerWallet.CurrencyConverter.convertToCoins(coinCost) : null);
    }


    public static void hoverCarried(GuiGraphics guiGraphics, int x, int y, Slot hoveredSlot, RuneTableMenu runeTableMenu, Minecraft minecraft){
        var carried = hoveredSlot == null || hoveredSlot.getItem().isEmpty() ? runeTableMenu.getCarried() : hoveredSlot.getItem();
        var isRuneSlot = hoveredSlot instanceof RuneSlot;
        var isNonRuneSlot = !carried.isEmpty() && !isRuneSlot;
        var isRuneWithShift = isRuneSlot && isShiftDown(minecraft);

        if (isNonRuneSlot || isRuneWithShift) {
            guiGraphics.renderTooltip(minecraft.font, carried, x, y);
        }
    }
}
