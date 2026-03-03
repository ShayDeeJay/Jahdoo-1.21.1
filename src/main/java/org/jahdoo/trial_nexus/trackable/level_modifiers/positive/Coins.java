package org.jahdoo.trial_nexus.trackable.level_modifiers.positive;

import org.jahdoo.trial_nexus.trackable.level_modifiers.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

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
    public double value(JahdooRarity rarity) {
        return 1;
    }

}
