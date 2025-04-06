package org.jahdoo.ascension.ability.abilities_combat.elemental_shooter;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.ElementReg;

public class InfernoMissile extends ElementalMissile {

    public static final ResourceLocation abilityId = Helpers.res("inferno_missile");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.INFERNO.get();
    }

    @Override
    public AbilityHolder setModifiers() {
        return this.getWithElement(getElemenType().id());
    }

}
