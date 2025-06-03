package org.jahdoo.trial_nexus.boon.level_boons;

import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import static org.jahdoo.trial_nexus.utils.Maths.doubleFormattedDouble;

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
