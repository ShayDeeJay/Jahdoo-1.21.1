package org.jahdoo.all_magic.all_abilities.ability_components;

import org.jahdoo.all_magic.AbstractAbility;

public abstract class AbstractContainerAccessor extends AbstractAbility {
    public boolean isInputUser() {
        return false;
    }

    public boolean isOutputUser() {
        return false;
    }
}
