package org.jahdoo.ability;

import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.utils.ModHelpers;

public abstract class AbstractAbility {

    public abstract WandAbilityHolder getWandAbilityHolder();
    public abstract String abilityId();
    public double getTag(String name) {
        if(ModHelpers.getModifierValue(getWandAbilityHolder(), abilityId()).get(name) != null){
            return ModHelpers.getModifierValue(getWandAbilityHolder(), abilityId()).get(name).actualValue();
        }
        return 0;
    }

    public double getTagUtility(String name) {
        if(ModHelpers.getModifierValue(getWandAbilityHolder(), abilityId()).get(name) != null){
            return ModHelpers.getModifierValue(getWandAbilityHolder(), abilityId()).get(name).setValue();
        }
        return 0;
    }

}
