package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.JahdooItem;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.common.client.overlay.CustomHudOverlay.getDurabilityWithColor;
import static org.jahdoo.trial_nexus.utils.Configuration.DISPLAY_DURABILITY_OVERLAY;

public class DurabilityOverlay extends AbstractTimedOverlay {

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if(player == null) return;

        if(DISPLAY_DURABILITY_OVERLAY.get()) overlayDurability(guiGraphics, player, minecraft);
    }

    private static void overlayDurability(GuiGraphics graphics, LocalPlayer player, Minecraft minecraft) {
        var items = new ArrayList<ItemStack>();

        var itemStack5 = player.getItemBySlot(EquipmentSlot.HEAD);
        if(!itemStack5.isEmpty()) items.add(itemStack5);

        var itemStack4 = player.getItemBySlot(EquipmentSlot.CHEST);
        if(!itemStack4.isEmpty()) items.add(itemStack4);

        var itemStack3 = player.getItemBySlot(EquipmentSlot.LEGS);
        if(!itemStack3.isEmpty()) items.add(itemStack3);

        var itemStack2 = player.getItemBySlot(EquipmentSlot.FEET);
        if(!itemStack2.isEmpty()) items.add(itemStack2);

        var itemStack = player.getItemBySlot(EquipmentSlot.OFFHAND);
        if(itemStack.has(DataComponents.MAX_DAMAGE) && !itemStack.isEmpty()) items.add(itemStack);

        var itemStack1 = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if(itemStack1.has(DataComponents.MAX_DAMAGE) && !itemStack1.isEmpty()) items.add(itemStack1);

        var curioSlotsItems = CuriosApi.getCuriosInventory(player);
        if(curioSlotsItems.isPresent()){
            var withSlots = curioSlotsItems.get().getEquippedCurios();

            for (int i = 0; i < withSlots.getSlots(); i++) {
                var stackInSlot = withSlots.getStackInSlot(i);
                if (stackInSlot.getItem() instanceof JahdooItem && stackInSlot.has(DataComponents.DAMAGE)) {
                    items.add(stackInSlot);
                }
            }
        }

        displayItemDurability(graphics, items, graphics.guiHeight() - 10, minecraft);
    }

    private static void displayItemDurability(GuiGraphics graphics, List<@NotNull ItemStack> listOfSlots, int y, Minecraft minecraft) {
        int baseY = y - 20;
        int spacer = 0;

        for (ItemStack stack : listOfSlots) {
            int drawX = 10 + spacer;
            var cooldownWithDurability = getDurabilityWithColor(stack);
            graphics.renderItem(stack, drawX, baseY);
            graphics.drawCenteredString(minecraft.font, TextHelpers.withStyleComponent(cooldownWithDurability.getFirst() + "%", cooldownWithDurability.getSecond()), drawX + 8, baseY + 18, -1);
            spacer += 32;
        }

    }

}
