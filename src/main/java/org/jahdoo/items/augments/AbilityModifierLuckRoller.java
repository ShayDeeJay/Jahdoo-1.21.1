package org.jahdoo.items.augments;

import org.jahdoo.utils.ModHelpers;

public class AbilityModifierLuckRoller {


    public static double getWeightedRandomDouble(double high, double low, boolean isHigherBetter, double step, double boundLower){
        double boundUpper = 20.0;
        if(boundLower < 20.0){
            double ub = probability(boundLower);
            double lb = probability(boundUpper);

            double domain = ModHelpers.Random.nextDouble(boundLower, boundUpper);
            double probability = probability(domain);

            double normalized = (probability - lb) / (ub - lb);

            // Change boolean to juice values and make it so betters can roll. Might be good to figure out a way to modify these as like a perk or item?
            if (!isHigherBetter) normalized = -normalized + 1;

            double continuous = low + normalized * (high - low);
            return Math.round(continuous / step) * step;
        } else {
            if(isHigherBetter) return high; else return low;
        }
    }

    static double probability(double x){
        return x < 5 ? unLikely(x) : likely(x);
    }

    static double unLikely(double x) {
        return Math.pow(2, -x + 5) + 5;
    }

    static double likely(double x){
        return 0.8 * Math.pow(1.2, -0.6 * x + 13) + 1;
    }

}
