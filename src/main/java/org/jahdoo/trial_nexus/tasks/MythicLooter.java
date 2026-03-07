package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import java.util.List;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter;

public class MythicLooter extends AbstractTask {

    @Override
    public ResourceLocation taskIcon() {
        return Icons.CHEST_MYTHIC;
    }

    @Override
    public String taskName() {
        return JahdooRarity.MYTHIC.getSerializedName() +  " Looter";
    }

    @Override
    public String taskDescription() {
        return "Open 500 mythic chests";
    }

    @Override
    public boolean completionPredicate(Player player) {
        return trackedValue(player) >= countRequired();
    }

    @Override
    public int trackedValue(Player player) {
        var x = PlayerTrialData.getData(player).getPastRuns().stream().mapToInt(RunData::getMythicChests).sum();
        var current = RunData.getRunData(player).getMythicChests();
        return x + current;
    }

    @Override
    public int countRequired() {
        return 500;
    }

    @Override
    public List<ItemStack> rewards(int tick) {
        var x = new ItemStack(ItemReg.AUGMENT_HYPER_CORE).copyWithCount(5);
        var y = new ItemStack(ItemReg.SKILL_POINT).copyWithCount(5);
        var z = new ItemStack(ItemReg.COIN_SACK);

        z.set(ComponentReg.STORE_INTEGER, CurrencyConverter.convertToWallet(new CurrencyConverter(5, 0, 0, 0)));
        CoreData.setFilled(x);

        return List.of(x, y, z);
    }

    @Override
    public TriggerType type() {
        return TriggerType.USE;
    }

}
