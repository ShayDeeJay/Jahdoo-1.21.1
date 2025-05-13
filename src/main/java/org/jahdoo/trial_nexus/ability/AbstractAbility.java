package org.jahdoo.trial_nexus.ability;

import org.jahdoo.common.components.AbilityHolder;

public abstract class AbstractAbility {

    public abstract AbilityHolder getAbilityHolder();

    public abstract String abilityId();

    public double getTag(String name) {
        return getAbilityHolder().data().abilityProperties().get(name).setValue();
    }

}
