package org.jahdoo.ascension.ability.abilities_combat.elemental_missile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.mod.ElementReg;

public class VitalityMissileAbility extends ElementalMissileAbility {

    public static final ResourceLocation abilityId = Helpers.res("vitality_missile");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.VITALITY.get();
    }

    @Override
    public void invokeAbility(Player player) {
        var name = abilityId.getPath().intern();
        this.doOnCast(player, name);
    }

    @Override
    public AbilityHolder setModifiers() {
        var name = abilityId.getPath().intern();
        return this.getWithElement(getElemenType().id(), name);
    }
}
