package org.jahdoo.ascension.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class Experience extends AbstractLevelBoon {

    @Override
    public String id() {
        return "xp";
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setExperience((int) value);
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
        return Icons.TRIAL_EXPERIENCE;
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return (int) getRarity.getRandomManaPool();
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

}
