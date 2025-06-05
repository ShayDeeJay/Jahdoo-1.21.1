package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_MYTHIC_LOOT_MULTIPLIER;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.MYTHIC;

public class MythicLootMultiplier extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_MYTHIC_LOOT_MULTIPLIER;
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
        return Icons.CHEST_MYTHIC;
    }

    @Override
    public double value(JahdooRarity rarity) {
        return 1;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public int getStampIndex() {
        return 6;
    }

    @Override
    public int getHeaderColour() {
        return MYTHIC.getColour();
    }
}
