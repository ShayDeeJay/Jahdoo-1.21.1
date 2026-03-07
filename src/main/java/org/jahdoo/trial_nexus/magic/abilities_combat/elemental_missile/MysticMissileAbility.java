package org.jahdoo.trial_nexus.magic.abilities_combat.elemental_missile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.mod.ElementReg;

public class MysticMissileAbility extends ElementalMissileAbility {

    public static final ResourceLocation abilityId = JahdooHelpers.res("mystic_missile");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.MYSTIC.get();
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
