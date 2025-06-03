package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_SAFE_LOOT_MULTIPLIER;

public class SafeLootMultiplier extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_SAFE_LOOT_MULTIPLIER;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.incrementSafeLootMultiplier((int) value);
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public boolean isPercentageOf() {
        return false;
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.SAFE;
    }

    @Override
    public double value(JahdooRarity rarity) {
        return 1;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public int getStampIndex() {
        return 8;
    }
}
