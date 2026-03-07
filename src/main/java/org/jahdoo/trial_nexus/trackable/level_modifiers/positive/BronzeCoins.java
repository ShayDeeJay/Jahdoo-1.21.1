package org.jahdoo.trial_nexus.trackable.level_modifiers.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.attachments.PlayerWallet;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class BronzeCoins extends Coins {
    @Override
    public String id() {
        return TextHelpers.withStyleComponentTrans(PlayerWallet.CoinProperties.BRONZE.getName(), -1).getString();
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.BRONZE_COIN;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setBronzeCoin((int) value);
    }

    @Override
    public int getStampIndex() {
        return 0;
    }

    @Override
    public int getHeaderColour() {
        return ColourHelpers.getBronzeCoin();
    }

}
