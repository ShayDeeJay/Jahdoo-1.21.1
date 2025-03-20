package org.jahdoo.ascension.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class FillerBronzeCoins extends Coins{
    @Override
    public String id() {
        return "filler_bronze_coin";
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.BRONZE_COIN;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setBronzeCoin((int) value);
    }
}
