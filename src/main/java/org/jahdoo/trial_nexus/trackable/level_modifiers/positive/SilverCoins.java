package org.jahdoo.trial_nexus.trackable.level_modifiers.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class SilverCoins extends Coins {
    @Override
    public String id() {
        return TextHelpers.withStyleComponentTrans(PlayerWallet.CoinProperties.SILVER.getName(), -1).getString();
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.SILVER_COIN;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setSilverCoin((int) value);
    }

    @Override
    public int getStampIndex() {
        return 1;
    }

    @Override
    public int getHeaderColour() {
        return ColourHelpers.getSilverCoin();
    }
}
