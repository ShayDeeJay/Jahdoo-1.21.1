package org.jahdoo.common.block.divine_forge.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.slots.CoreItemSlot;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.jahdoo.common.block.divine_forge.helpers.DivineForgeScreen.groupFade;
import static org.jahdoo.common.client.SharedUI.*;

public class RuneScreenShared {

    public static void renderItem(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        int startX,
        int startY,
        int widthA,
        int heightA,
        int scaleItem,
        Minecraft minecraft,
        ItemStack getItem,
        int leftPos,
        int topPos,
        int borderColour
    ) {
        // Define constants for positioning and sizing
        final var ITEM_OFFSET_X = 40;
        final var ITEM_OFFSET_Y = -17;
        final var SHIFT_X = 75;
        final var WAND_ITEM_OFFSET = CastHelper.validCasterType(getItem.getItem()) ? 75 : 80;
        final var SCALED_ITEM = scaleItem - WAND_ITEM_OFFSET;
        final var OFFSET_Y = 91;

        var minX = startX + ITEM_OFFSET_X + 70 - SHIFT_X;
        var minY = startY + ITEM_OFFSET_Y - 94;
        var maxX = startX + ITEM_OFFSET_X + 58;
        var width = widthA - SHIFT_X * 2;
        var height = heightA - (140 - SCALED_ITEM);
        var posX = startX + 23;
        var posY = startY + ITEM_OFFSET_Y - 106;

        bezelMaker(guiGraphics, posX + 1, posY+1, 200, OFFSET_Y-1, 32, null);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -10);
        guiGraphics.enableScissor(minX, minY, maxX, minY + 172);

        var stand = EntityType.ARMOR_STAND.create(minecraft.level);

        if (getItem.getItem() instanceof ArmorItem armorItem && stand != null) {
            stand.setItemSlot(armorItem.getEquipmentSlot(), getItem);
            stand.setInvisible(true);

            var yOffset = switch (armorItem.getEquipmentSlot()) {
                case HEAD -> 70;
                case CHEST -> 8;
                case LEGS -> -30;
                case FEET -> -60;
                default -> 20;
            };

            renderEntityInInventoryFollowsMouse(guiGraphics, leftPos - 48, topPos, leftPos + 78, height / 2 + yOffset + 50, 44, 0.0625F, mouseX, mouseY, stand,  320.0F);
        } else {
            SharedUI.renderItem(guiGraphics, width+4, height, getItem, SCALED_ITEM, mouseX, mouseY, 16);
        }

        var heightOffset = OFFSET_Y - 41;
        var colorA = groupFade();
        SharedUI.boxMaker(guiGraphics, minX, minY, 32, heightOffset, borderColour, colorA, borderColour);

        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
    }


    public static void tooltipSlots(List<Component> hoverTooltip, Slot hoveredSlot, ItemStack carried) {
        if(hoveredSlot != null && hoveredSlot.getItem().isEmpty() && carried.isEmpty() && hoveredSlot instanceof CoreItemSlot generalItemSlot){
            var defaultInstance = generalItemSlot.getSlotType().getDefaultInstance();
            CoreData.setFilled(defaultInstance);
            hoverTooltip.add(defaultInstance.getHoverName());
        }
    }

    public static void overlayInventory(@NotNull GuiGraphics guiGraphics, int startX, int startY, Screen screen, int borderColour) {
        guiGraphics.pose().popPose();
        var i = 40;

        guiGraphics.pose().translate(0,0,20);
        var startX1 = startX + i + 3;

        boxMaker(guiGraphics, startX1 + 11, startY - 5, 86, 43, borderColour, groupFade());
        renderInventoryBackground(guiGraphics, screen, 256, 24, true);
        guiGraphics.pose().pushPose();
    }


}
