package org.jahdoo.ascension.ability.abilities_utility.farmers_touch;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbstractBlockAbility;
import org.jahdoo.ascension.ability.abilities_utility.block_breaker.BlockBreakerAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;


public class FarmersTouchAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = Helpers.res("farmers_touch");
    public static final String GROWTH_CHANCE = "Growth Chance";
    public static final String HARVEST_CHANCE = "Harvest Chance";

    @Override
    public String projectileKey() {
        return EntityDataReg.BONE_MEAL.get().setAbilityId();
    }

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
    public void invokeAbility(Player player) {
        GenericProjectile genericProjectile = new GenericProjectile(
            player, 0,
            projectileKey(),
            abilityId.getPath().intern()
        );
        fireUtilityProjectile(genericProjectile, player);
    }

    @Override
    public String requiredUnlock() {
        return BlockBreakerAbility.abilityId.getPath();
    }

    @Override
    public int getAbilityCost() {
        return 4;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setMana(30, 15, 5, 1, 1.5)
            .setRange(10, 1, 1, 1, 1.5)
            .setAbilityTagModifiersRandom(GROWTH_CHANCE, 30, 5, false, 5, 1, 1.5)
            .setAbilityTagModifiersRandom(HARVEST_CHANCE, 30, 5, false, 5, 1, 1.5)
            .buildAndReturn();
    }

}
