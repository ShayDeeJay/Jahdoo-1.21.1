package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_SILVER_COIN;

public class SilverCoins extends Coins {
    @Override
    public String id() {
        return KEY_SILVER_COIN;
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
        return ColourStore.SILVER_COIN;
    }
}
