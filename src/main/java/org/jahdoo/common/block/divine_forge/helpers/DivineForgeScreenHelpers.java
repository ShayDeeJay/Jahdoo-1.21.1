package org.jahdoo.common.block.divine_forge.helpers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.divine_forge.DivineForgeEntity;
import org.jahdoo.common.block.divine_forge.DivineForgeMenu;
import org.jahdoo.common.client.overlay.WalletOverlay;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.items.CoinSack;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.block.divine_forge.DivineForgeScreen.groupFade;
import static org.jahdoo.common.client.Icons.GUI_GENERAL_SLOT;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.handleSlotsInGridLayout;
import static org.jahdoo.common.client.slots.RuneSlot.removeCurrencyCost;
import static org.jahdoo.common.client.slots.RuneSlot.removeRuneCost;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.getGearData;

public class DivineForgeScreenHelpers {

    public static boolean isShiftDown() {
        return ClientHelpers.isKeyDown(InputConstants.KEY_LSHIFT);
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
        var spacer = new AtomicInteger();
        var entity = runeTableMenu.tableEntity();
        var item = entity.getInputItemHandler().getStackInSlot(0);
        var font = minecraft.font;
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
            guiGraphics.drawCenteredString(font, "No Slots Available", startX11 + 74, startY11 + 46, ColourHelpers.getSubHeaderColour());
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
            var quantityCost = TextHelpers.withStyleComponent("1", borderColour);
            var list = new ArrayList<Component>();

            list.add(TextHelpers.withStyleComponent(prefix, borderColour));
            var coreAvailableColour = entity.checkAndChargeCores(coreItemType, false) ? ColourHelpers.getSubHeaderColour() : ColourHelpers.getNegativeRed();

            list.add(TextHelpers.withStyleComponent(text + ": ", coreAvailableColour).copy().append(quantityCost));
            CoinSack.coinToolTip(list, coinCost);

            if(!isShiftDown()){
                list.add(TextHelpers.withStyleComponent("Hold", ColourHelpers.getHeaderColour()).copy().append(TextHelpers.withStyleComponent(" [Shift] ", ColourHelpers.getOffWhite())));
            }

            var toolTipSpacer = isShiftDown() ? ((list.size() * 10) + 10) : 0;
            guiGraphics.renderTooltip(minecraft.font, list, Optional.empty(), mouseX, mouseY - toolTipSpacer);
        }

        var converter = coinCost > 0 ? PlayerWallet.CurrencyConverter.convertToCoins(coinCost) : null;
        var adjustX = (double) guiGraphics.guiWidth() / 2 - 128;
        var adjustY = (double) guiGraphics.guiHeight() / 2 - 11;
        WalletOverlay.renderWallet(guiGraphics, minecraft, 1, adjustX, adjustY + 14, false, false, false, converter);
    }


    public static void hoverCarried(GuiGraphics guiGraphics, int x, int y, Slot hoveredSlot, DivineForgeMenu runeTableMenu, Minecraft minecraft){
        var carried = hoveredSlot == null || hoveredSlot.getItem().isEmpty() ? runeTableMenu.getCarried() : hoveredSlot.getItem();
        var isRuneSlot = hoveredSlot instanceof RuneSlot;
        var isNonRuneSlot = !carried.isEmpty() && !isRuneSlot;
        var isRuneWithShift = isRuneSlot && isShiftDown();

        if (isNonRuneSlot || isRuneWithShift) {
            guiGraphics.renderTooltip(minecraft.font, carried, x, y);
        }
    }
}
