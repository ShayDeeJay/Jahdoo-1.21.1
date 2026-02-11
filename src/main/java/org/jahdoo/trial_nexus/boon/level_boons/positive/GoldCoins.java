package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_GOLD_COIN;

public class GoldCoins extends Coins{
    @Override
    public String id() {
        return KEY_GOLD_COIN;
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.GOLD_COIN;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setGoldCoin((int) value);
    }

    @Override
    public int getStampIndex() {
        return 2;
    }

    @Override
    public int getHeaderColour() {
        return ColourHelpers.getGoldCoin();
    }
}
