package org.jahdoo.common.block.divine_forge.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.divine_forge.DivineForgeEntity;
import org.jahdoo.common.block.divine_forge.RuneTableMenu;
import org.jahdoo.common.networking.client2server.ItemInBlockC2SP;
import org.jahdoo.common.networking.client2server.JahdooGearDataC2SP;
import org.jahdoo.common.registers.SoundReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.client.SharedUI.getCore;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.repairDurability;

public class RepairManager {

    public static void onRepair(Minecraft minecraft, RuneTableMenu menu){
        var entity = menu.tableEntity();
        var item = entity.getInputItemHandler().getStackInSlot(0);
        minecraft.player.playSound(SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 0.6F);
        minecraft.player.playSound(SoundReg.REJECT.get(), 1, 1.8F);
        var handler = entity.getInputItemHandler().getStackInSlot(getRepairCoreCost(item)+1);

        sendToServer(new ItemInBlockC2SP(handler.copyWithCount(handler.getCount()-1), entity.getBlockPos(), getRepairCoreCost(item)+1));
        repairDurability(item);
        updateRefinementPotential(item, getItemPotential(item) - repairPotentialCost(item));
        checkAndRepair(item);
        sendToServer(new JahdooGearDataC2SP(getGearData(item), entity.getBlockPos(), 0));
    }

    public static void onHoverRepair(List<Component> hoverTooltip, int borderColour, ItemStack itemStack){
        if(hoverTooltip.isEmpty()){
            var stack = coreCost(itemStack);
            if(!stack.isEmpty()){
                var subHeaderColour = ColourHelpers.getSubHeaderColour();
                var prefix = TextHelpers.withStyleComponent("Potential: ", subHeaderColour);
                var value = TextHelpers.withStyleComponent("" + repairPotentialCost(itemStack), borderColour);

                hoverTooltip.add(TextHelpers.withStyleComponent("Cost:", borderColour));
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
        if (coreType < 0) {
            return ItemStack.EMPTY;
        } else {
            return new ItemStack(getCore().get(coreType));
        }
    }

    public static int getRepairCoreCost(ItemStack item) {
        return Math.min(totalRepairs(item), 2);
    }

    public static int repairPotentialCost(ItemStack itemStack) {
        return 5 * (totalRepairs(itemStack) + 1);
    }

}
