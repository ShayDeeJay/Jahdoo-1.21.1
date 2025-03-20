package org.jahdoo.ascension.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class SilverCoins extends Coins {
    @Override
    public String id() {
        return "silver_coin";
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
}
