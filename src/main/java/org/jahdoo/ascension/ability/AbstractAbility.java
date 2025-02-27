package org.jahdoo.ascension.ability;

import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.ascension.utils.Helpers;

public abstract class AbstractAbility {

    public abstract WandAbilityHolder getWandAbilityHolder();

    public abstract String abilityId();

    public double getTag(String name) {
        if(Helpers.getModifierValue(getWandAbilityHolder(), abilityId()).get(name) != null){
            return Helpers.getModifierValue(getWandAbilityHolder(), abilityId()).get(name).setValue();
        }
        return 0;
    }

    public double getTagUtility(String name) {
        if(Helpers.getModifierValue(getWandAbilityHolder(), abilityId()).get(name) != null){
            return Helpers.getModifierValue(getWandAbilityHolder(), abilityId()).get(name).setValue();
        }
        return 0;
    }

}
