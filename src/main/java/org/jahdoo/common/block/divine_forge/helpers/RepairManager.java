package org.jahdoo.common.block.divine_forge.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.divine_forge.DivineForgeEntity;
import org.jahdoo.common.block.divine_forge.RuneTableMenu;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.networking.client2server.ItemInBlockC2SP;
import org.jahdoo.common.networking.client2server.JahdooGearDataC2SP;
import org.jahdoo.common.registers.SoundReg;

import java.util.List;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.trial_nexus.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.trial_nexus.utils.Helpers.repairDurability;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.client.SharedUI.getCore;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.*;

public class RepairManager {

    public static void onRepair(Minecraft minecraft, RuneTableMenu menu){
        var entity = menu.tableEntity();
        var item = entity.inputItemHandler.getStackInSlot(0);
        minecraft.player.playSound(SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 0.6F);
        minecraft.player.playSound(SoundReg.REJECT.get(), 1, 1.8F);
        var handler = entity.inputItemHandler.getStackInSlot(getRepairCoreCost(item)+1);

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
                var subHeaderColour = SUB_HEADER_COLOUR;
                var prefix = withStyleComponent("Potential: ", subHeaderColour);
                var value = withStyleComponent("" + repairPotentialCost(itemStack), borderColour);

                hoverTooltip.add(withStyleComponent("Cost:", borderColour));
                hoverTooltip.add(prefix.copy().append(value));

                var hoverName = withStyleComponent(stack.getHoverName().getString() + ":", subHeaderColour);
                hoverTooltip.add(hoverName.copy().append(withStyleComponent(" 1", borderColour)));
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
            var set = new ItemStack(getCore().get(coreType));
            CoreData.setFilled(set);
            return set;
        }
    }

    public static int getRepairCoreCost(ItemStack item) {
        return Math.min(totalRepairs(item), 2);
    }

    public static int repairPotentialCost(ItemStack itemStack) {
        return 5 * (totalRepairs(itemStack) + 1);
    }

}
