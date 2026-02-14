package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_MAX_TIME;

public class Time extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_MAX_TIME;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setMaxTime((int) value);
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
        return Icons.CLOCK;
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return MathHelpers.doubleFormattedDouble(getRarity.getRandomTime());
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public int getStampIndex() {
        return 9;
    }

    @Override
    public int getHeaderColour() {
        return ColourHelpers.getUniqueB();
    }
}
