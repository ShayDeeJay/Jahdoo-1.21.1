package org.jahdoo.trial_nexus.ability.abilities_utility.deprecated.excavator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.AbstractBlockAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

public class BlockBreakerAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = JahdooHelpers.res("excavator");

    @Override
    public String projectileKey() {
        return EntityDataReg.BLOCK_BREAKER.get().setAbilityId();
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
    }

    @Override
    public int getCastType() {
        return PROJECTILE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.utility();
    }

    @Override
    public AbilityHolder setModifiers() {
       return new AbilityBuilder(abilityId.getPath().intern())
           .setStaticMana(1)
           .buildAndReturn();
    }

    @Override
    public void invokeAbility(Player player) {
        GenericProjectile genericProjectile = new GenericProjectile(
            player, 0, projectileKey(), abilityId.getPath().intern()
        );
        fireUtilityProjectile(genericProjectile, player);
    }

    @Override
    public int levelRequirement() {
        return 0;
    }

    @Override
    public int getAbilityCost() {
        return 1;
    }

}
