package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;

import java.util.List;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter;

public class RareLooter extends AbstractTask {

    @Override
    public ResourceLocation taskIcon() {
        return Icons.CHEST_RARE;
    }

    @Override
    public String taskName() {
        return "Rare Looter";
    }

    @Override
    public String taskDescription() {
        return "Open 500 rare chests";
    }

    @Override
    public boolean completionPredicate(Player player) {
        return trackedValue(player) >= countRequired();
    }

    @Override
    public int trackedValue(Player player) {
        var x = PlayerTrialData.getData(player).getPastRuns().stream().mapToInt(RunData::getRareChests).sum();
        var current = RunData.getRunData(player).getRareChests();
        return x + current;
    }

    @Override
    public int countRequired() {
        return 500;
    }

    @Override
    public List<ItemStack> rewards(int tick) {
        var x = new ItemStack(ItemReg.AUGMENT_CORE).copyWithCount(3);
        var y = new ItemStack(ItemReg.SKILL_POINT).copyWithCount(2);
        var z = new ItemStack(ItemReg.COIN_SACK);

        z.set(ComponentReg.STORE_INTEGER, CurrencyConverter.convertToWallet(new CurrencyConverter(0, 2, 0, 0)));
        CoreData.setFilled(x);

        return List.of(x, y, z);
    }

    @Override
    public TriggerType type() {
        return TriggerType.USE;
    }

}
