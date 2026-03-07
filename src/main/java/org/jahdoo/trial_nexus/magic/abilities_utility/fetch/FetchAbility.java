package org.jahdoo.trial_nexus.magic.abilities_utility.fetch;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.magic.AbilityBuilder;
import org.jahdoo.trial_nexus.magic.AbstractBlockAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.registers.mod.EntityDataReg.FETCH;

public class FetchAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = JahdooHelpers.res("fetch");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return "description.ability.jahdoo.test";
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
    public boolean isOutputUser() {
        return true;
    }

    @Override
    public String projectileKey() {
        return FETCH.get().setAbilityId();
    }

    @Override
    public void invokeAbility(Player player) {
        var genericProjectile = new GenericProjectile(player, 0, projectileKey(), abilityId.getPath().intern());
        fireUtilityProjectile(genericProjectile, player);
    }

    @Override
    public int levelRequirement() {
        return 10;
    }

    @Override
    public int getAbilityCost() {
        return 2;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(5)
            .setRange(10, 2, 2, 1)
            .buildAndReturn();
    }

}
