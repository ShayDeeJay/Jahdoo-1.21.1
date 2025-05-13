package org.jahdoo.trial_nexus.quests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;

public abstract class CollectCoinQuest extends AbstractQuest{

    public abstract PlayerWallet.CoinProperties coinType();

    @Override
    public ResourceLocation questIcon() {
        return coinType().getLocation();
    }

    @Override
    public InstanceDifficulty difficulty() {
        return InstanceDifficulty.NOVICE;
    }

    @Override
    public int questQuantity(Player player) {
        return questValueMultiplier(player, 50, 100, 5);
    }

    @Override
    public String getDisplayName() {
        return "Coin Catcher";
    }

    @Override
    public String questDescription(Player player) {
        return "Collect " + questQuantity(player) + " " + coinType().getSerializedName() + " Coins";
    }

    @Override
    public int questColour() {
        return coinType().getTextColour();
    }

}
