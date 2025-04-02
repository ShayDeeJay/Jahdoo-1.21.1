package org.jahdoo.ascension.ability;

import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;

public abstract class AbstractAbility {

    public abstract AbilityHolder getAbilityHolder();

    public abstract String abilityId();

    public double getTag(String name) {
        if(Helpers.getModifierValue(getAbilityHolder(), abilityId()).get(name) != null){
            return Helpers.getModifierValue(getAbilityHolder(), abilityId()).get(name).setValue();
        }
        return 0;
    }

    public double getTagUtility(String name) {
        if(Helpers.getModifierValue(getAbilityHolder(), abilityId()).get(name) != null){
            return Helpers.getModifierValue(getAbilityHolder(), abilityId()).get(name).setValue();
        }
        return 0;
    }

}
