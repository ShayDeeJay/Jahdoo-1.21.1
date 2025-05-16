package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.List;

public class CoinSack extends Item implements JahdooItem {
    public CoinSack() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);
        var getCoins = item.get(ComponentReg.STORE_INTEGER);
        if(getCoins != null) {
            player.playSound(SoundReg.COIN.get());
            player.getData(AttachmentReg.PLAYER_WALLET_DATA.get()).addBronze(getCoins);
            item.shrink(1);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public Component getName(ItemStack stack) {
        var getCoins = stack.get(ComponentReg.STORE_INTEGER);
        return Helpers.withStyleComponent((getCoins == null ? "Empty " : "") + "Coin Sack", ColourStore.CHAMPION_GOLD);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var coins = stack.get(ComponentReg.STORE_INTEGER);
        if(coins != null) coinToolTip(tooltipComponents, coins);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static void coinToolTip(List<Component> tooltipComponents, int coins) {
        var asWallet = PlayerWallet.CurrencyConverter.convertToCoins(coins);
        if (asWallet.platinum() > 0)
            tooltipComponents.add(Helpers.withStyleComponent("Platinum: " + asWallet.platinum(), ColourStore.PLATINUM_COIN));
        if (asWallet.gold() > 0)
            tooltipComponents.add(Helpers.withStyleComponent("Gold: " + asWallet.gold(), ColourStore.GOLD_COIN));
        if (asWallet.silver() > 0)
            tooltipComponents.add(Helpers.withStyleComponent("Silver: " + asWallet.silver(), ColourStore.SILVER_COIN));
        if (asWallet.bronze() > 0)
            tooltipComponents.add(Helpers.withStyleComponent("Bronze: " + asWallet.bronze(), ColourStore.BRONZE_COIN));
    }
}
