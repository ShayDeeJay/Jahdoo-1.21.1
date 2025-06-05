package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_LEGENDARY_LOOT_MULTIPLIER;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.LEGENDARY;

public class LegendaryLootMultiplier extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_LEGENDARY_LOOT_MULTIPLIER;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setLegendaryLootMultiplier((int) value);
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
        return Icons.CHEST_LEGENDARY;
    }

    @Override
    public double value(JahdooRarity rarity) {
        return 1;
    }

    @Override
    public JahdooRarity rarity() {
        return LEGENDARY;
    }

    @Override
    public int getStampIndex() {
        return 5;
    }

    @Override
    public int getHeaderColour() {
        return LEGENDARY.getColour();
    }
}
