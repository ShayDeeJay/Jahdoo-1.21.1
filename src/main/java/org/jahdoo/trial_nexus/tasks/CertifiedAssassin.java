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
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import java.util.List;

import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter;
import static org.jahdoo.trial_nexus.trading_post.ShoppingItems.getRandomWand;

public class CertifiedAssassin extends AbstractTask {
    public static ItemStack wandExample;

    public ItemStack getThisItem(int x){
        if(wandExample == null || x % 80 == 0) wandExample = getRandomWand(JahdooRarity.ETERNAL, null);
        return wandExample;
    }

    @Override
    public ResourceLocation taskIcon() {
        return Icons.HORDE;
    }

    @Override
    public String taskName() {
        return "Certified Assassin";
    }

    @Override
    public String taskDescription() {
        return "Kill 100000 mobs in the trial dimension";
    }

    @Override
    public boolean completionPredicate(Player player) {
        return trackedValue(player) >= countRequired();
    }

    @Override
    public int trackedValue(Player player) {
        var x = PlayerTrialData.getData(player).getPastRuns().stream().mapToInt(RunData::getMobsKilled).sum();
        var current = RunData.getStat(player, RunData.MOBS_KILLED);

        return x + current;
    }

    @Override
    public int countRequired() {
        return 100000;
    }

    @Override
    public List<ItemStack> rewards(int tick) {
        var x = new ItemStack(ItemReg.AUGMENT_HYPER_CORE);
        var y = new ItemStack(ItemReg.SKILL_POINT).copyWithCount(5);
        var z = new ItemStack(ItemReg.COIN_SACK);

        z.set(ComponentReg.STORE_INTEGER, CurrencyConverter.convertToWallet(new CurrencyConverter(10, 0, 0, 0)));
        CoreData.setFilled(x);

        return List.of(getThisItem(tick), x, y, z);
    }

    @Override
    public TriggerType type() {
        return TriggerType.KIll;
    }

}
