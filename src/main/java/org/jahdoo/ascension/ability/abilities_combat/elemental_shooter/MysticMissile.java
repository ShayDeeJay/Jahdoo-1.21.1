package org.jahdoo.ascension.ability.abilities_combat.elemental_shooter;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.ascension.ability.AbilityBuilder.NUMBER_OF_RICOCHET;
import static org.jahdoo.ascension.ability.AbilityBuilder.SHOT_MULTIPLIER;

public class MysticMissile extends ElementalMissile {

    public static final ResourceLocation abilityId = Helpers.res("mystic_missile");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.MYSTIC.get();
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(15)
            .setStaticCooldown(0)
            .setDamage(20, 10, 2)
            .setEffectChance(50, 10, 10)
            .setEffectStrength(10, 1, 1)
            .setEffectDuration(300, 100, 50)
            .setAbilityTagModifiersRandom(SHOT_MULTIPLIER, 3, 1, true, 1)
            .setAbilityTagModifiersRandom(NUMBER_OF_RICOCHET, 6, 1, true, 1)
            .setElement(getElemenType().id())
            .buildAndReturn();
    }

}
