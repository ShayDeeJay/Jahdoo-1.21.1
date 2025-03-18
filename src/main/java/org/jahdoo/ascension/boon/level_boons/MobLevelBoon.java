package org.jahdoo.ascension.boon.level_boons;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.ascension.rarity.JahdooRarity.getRarity;
import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public abstract class MobLevelBoon extends AbstractLevelBoon{

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public boolean isPercentageOf() {
        return false;
    }


    @Override
    public double value() {
        var getRarity = getRarity().getAttributes();
        return doubleFormattedDouble(getRarity.getRandomMaxAbsorption());
    }

}
