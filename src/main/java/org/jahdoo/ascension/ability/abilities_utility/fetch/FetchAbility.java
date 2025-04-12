package org.jahdoo.ascension.ability.abilities_utility.fetch;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbstractBlockAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.registers.mod.EntityDataReg.FETCH;

public class FetchAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = Helpers.res("fetch");

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
        return 0;
    }

    @Override
    public int getAbilityCost() {
        return 2;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setMana(20, 10, 2, 1)
            .setRange(10, 3, 1, 1)
            .buildAndReturn();
    }

}
