package org.jahdoo.ascension.boon.level_boons.positive;

import org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon;

public abstract class Coins extends AbstractLevelBoon {

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public boolean isPercentageOf() {
        return false;
    }

    @Override
    public double value() {
        return 1;
    }

}
