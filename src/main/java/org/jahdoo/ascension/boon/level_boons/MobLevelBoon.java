package org.jahdoo.ascension.boon.level_boons;

import org.jahdoo.ascension.rarity.JahdooRarity;

import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;

public abstract class MobLevelBoon extends AbstractLevelBoon {

    public abstract boolean isTrashMob();

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public boolean isPercentageOf() {
        return false;
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return isTrashMob()? doubleFormattedDouble(getRarity.getRandomMaxAbsorption()) : 1;
    }

}
