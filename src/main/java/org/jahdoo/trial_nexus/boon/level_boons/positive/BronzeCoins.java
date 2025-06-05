package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.utils.ColourStore;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_BRONZE_COIN;

public class BronzeCoins extends Coins{
    @Override
    public String id() {
        return KEY_BRONZE_COIN;
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
        return ColourStore.BRONZE_COIN;
    }

}
