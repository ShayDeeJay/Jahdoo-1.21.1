package org.jahdoo.common.block.divine_forge.helpers;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.divine_forge.DivineForgeMenu;
import org.jahdoo.common.items.CoinSack;
import org.jahdoo.common.items.caster_item.CasterItemHelper;
import org.jahdoo.common.networking.client2server.JahdooGearDataC2SP;
import org.jahdoo.common.networking.client2server.WalletSyncC2SP;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.List;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.items.runes.rune_data.JahdooGearData.*;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;
import static org.shaydee.shaydeeapi.helpers.ClientHelpers.getMinecraft;

public class RepairTab {

    public static int repairPotentialCost(ItemStack itemStack) {
        return 5 * (totalRepairs(itemStack));
    }

    public static boolean hasRepairSlots(ItemStack itemStack){
        var holder = getGearData(itemStack);
        return holder.repairSlots().contains(1);
    }

    public static int repairCost(ItemStack stack) {
        var total = totalRepairSlots(stack);
        var repaired = totalRepairs(stack);
        return (int) ((Math.pow(20, repaired)) * (5 + total));
    }

    public static boolean repairable(ItemStack item) {
        var hasRepairSlots = hasRepairSlots(item);
        var insufficientPotential = !(getItemPotential(item) >= repairPotentialCost(item));
        var canPurchase = PlayerWallet.CurrencyConverter.canPurchase(repairCost(item), getMinecraft().player);
        return !hasRepairSlots || insufficientPotential || !canPurchase;
    }

    public static int onHoverRepair(List<Component> hoverTooltip, int borderColour, ItemStack itemStack){
        if(hoverTooltip.isEmpty() && hasRepairSlots(itemStack)){
            hoverTooltip.add(TextHelpers.withStyleComponentTrans("info.jahdoo.cost", borderColour, ": "));
            hoverTooltip.add(CasterItemHelper.getPotentialComponent(repairPotentialCost(itemStack)));
            var i = repairCost(itemStack);
            CoinSack.coinToolTip(hoverTooltip, i);

            return i;
        }

        return 0;
    }

    public static void onRepair(DivineForgeMenu menu){
        var entity = menu.tableEntity();
        var item = entity.getInputItemHandler().getStackInSlot(0);

        SoundHelpers.uiSound(SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 0.6F);
        SoundHelpers.uiSound(SoundReg.REJECT.get(), 1F, 0.5F);

        updateRefinementPotential(item, getItemPotential(item) - repairPotentialCost(item));
        checkAndRepair(item);

        sendToServer(new JahdooGearDataC2SP(item.get(JAHDOO_GEAR_DATA), entity.getBlockPos(), 0));
    }

    public static void checkAndRepair(ItemStack itemStack){
        var holder = getGearData(itemStack);
        var originalSlots = new ArrayList<>(holder.repairSlots());
        var hasRepairSlots = originalSlots.contains(1);

        if(hasRepairSlots) {
            var totalCost = repairCost(itemStack);
            var player = getMinecraft().player;
            if(player == null) return;

            WalletSyncC2SP.sendWallet(PlayerWallet.getWalletValue(player) -  totalCost);

            var index = originalSlots.indexOf(1);
            originalSlots.set(index, 0);
            updateRepairSlots(itemStack, originalSlots);
        }
    }


}
