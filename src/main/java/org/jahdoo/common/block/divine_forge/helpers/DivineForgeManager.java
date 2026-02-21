package org.jahdoo.common.block.divine_forge.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.divine_forge.DivineForgeEntity;
import org.jahdoo.common.block.divine_forge.DivineForgeMenu;
import org.jahdoo.common.networking.client2server.ItemInBlockC2SP;
import org.jahdoo.common.networking.client2server.JahdooGearDataC2SP;
import org.jahdoo.common.registers.SoundReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.client.SharedUI.getCore;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.*;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;

public class DivineForgeManager {

    public static void sharedClick(Minecraft minecraft, DivineForgeMenu menu, Consumer<ItemStack> runnable){
        var entity = menu.tableEntity();
        var item = entity.getInputItemHandler().getStackInSlot(0);

        minecraft.player.playSound(SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 0.6F);
        minecraft.player.playSound(SoundReg.REJECT.get(), 1, 1.8F);

        runnable.accept(item);

        var index = getRepairCoreCost(item);
        System.out.println(index);
        var handler = entity.getInputItemHandler().getStackInSlot(index);

        sendToServer(new ItemInBlockC2SP(coreCost(item), entity.getBlockPos(), index));
        sendToServer(new JahdooGearDataC2SP(item.get(JAHDOO_GEAR_DATA), entity.getBlockPos(), 0));
    }

    public static void onRepair(Minecraft minecraft, DivineForgeMenu menu){
        sharedClick(
            minecraft, menu, item -> {
                updateRefinementPotential(item, getItemPotential(item) - repairPotentialCost(item));
                checkAndRepair(item);
            }
        );
    }

    public static void onIncreaseRefinement(Minecraft minecraft, DivineForgeMenu menu){
        sharedClick(
            minecraft, menu, item -> {
                updateRefinementPotential(item, getItemPotential(item) + 50);
            }
        );
    }

    public static void onRepairSlot(Minecraft minecraft, DivineForgeMenu menu){
        sharedClick(
            minecraft, menu, item -> {
                var data = getGearData(item);
                var slots = data.repairSlots();

                var copy = new ArrayList<>(slots);
                var x = copy.lastIndexOf(0);
                if(x >= 0){
                    copy.set(x, 1);
                    updateRepairSlots(item, copy);
                }
            }
        );
    }

    public static void onIncreaseSlots(Minecraft minecraft, DivineForgeMenu menu){
        sharedClick(
            minecraft, menu, item -> {
                var data = getGearData(item);
                var copy = new ArrayList<>(data.repairSlots());

                copy.add(1);
                updateRepairSlots(item, copy);
            }
        );
    }

    public static void onHoverRepair(List<Component> hoverTooltip, int borderColour, ItemStack itemStack){
        if(hoverTooltip.isEmpty()){
            var stack = coreCost(itemStack);
            if(!stack.isEmpty()){
                var subHeaderColour = ColourHelpers.getSubHeaderColour();
                var prefix = TextHelpers.withStyleComponent("Potential: ", subHeaderColour);
                var value = TextHelpers.withStyleComponent("" + repairPotentialCost(itemStack), borderColour);

                hoverTooltip.add(TextHelpers.withStyleComponentTrans("info.jahdoo.cost", borderColour, ""));
                hoverTooltip.add(prefix.copy().append(value));

                var hoverName = TextHelpers.withStyleComponent(stack.getHoverName().getString() + ":", subHeaderColour);
                hoverTooltip.add(hoverName.copy().append(TextHelpers.withStyleComponent(" 1", borderColour)));
            }
        }
    }

    public static boolean repairable(ItemStack item, DivineForgeEntity entity) {
        var canUpgrade = canRepair(item);
        var insufficientPotential = !(getItemPotential(item) >= repairPotentialCost(item));
        var coresCharge = !(entity.checkAndChargeCores(coreCost(item).getItem(), false));
        return !canUpgrade || insufficientPotential || coresCharge;
    }

    public static ItemStack coreCost(ItemStack itemStack) {
        var coreType = getRepairCoreCost(itemStack);
        var item = getCore().get(coreType);
        return new ItemStack(item);
    }

    public static int getRepairCoreCost(ItemStack item) {
//        System.out.println(totalRepairs(item));
        return Math.min(totalRepairs(item), 2);
    }

    public static int repairPotentialCost(ItemStack itemStack) {
        return 5 * (totalRepairs(itemStack));
    }

}
