package org.jahdoo.trial_nexus.tasks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.StatEntryReg;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;

import java.util.List;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter;

public class RookieAssassin extends AbstractTask {

    @Override
    public ResourceLocation taskIcon() {
        return Icons.HORDE;
    }

    @Override
    public String taskName() {
        return "Rookie Assassin";
    }

    @Override
    public String taskDescription() {
        return "Kill 1000 mobs";
    }

    @Override
    public boolean completionPredicate(Player player) {
        return trackedValue(player) >= countRequired();
    }

    @Override
    public int trackedValue(Player player) {
        var x = PlayerTrialData.getData(player).getPastRuns().stream().mapToInt(RunData::getMobsKilled).sum();
        var current = RunData.getStat(player, StatEntryReg.MOBS_KILLED.get().id());

        return x + current;
    }

    @Override
    public int countRequired() {
        return 1000;
    }

    @Override
    public List<ItemStack> rewards(int tick) {
        var x = new ItemStack(ItemReg.AUGMENT_CORE);
        var y = new ItemStack(ItemReg.SKILL_POINT);
        var z = new ItemStack(ItemReg.COIN_SACK);

        z.set(ComponentReg.STORE_INTEGER, CurrencyConverter.convertToWallet(new CurrencyConverter(0, 1, 0, 0)));
        CoreData.setFilled(x);

        return List.of(x, y, z);
    }

    @Override
    public TriggerType type() {
        return TriggerType.KIll;
    }

}
