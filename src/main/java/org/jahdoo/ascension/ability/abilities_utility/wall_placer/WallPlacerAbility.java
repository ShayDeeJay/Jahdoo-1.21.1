package org.jahdoo.ascension.ability.abilities_utility.wall_placer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbstractBlockAbility;
import org.jahdoo.ascension.ability.abilities_utility.farmers_touch.FarmersTouchAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;

import static org.jahdoo.ascension.ability.AbilityBuilder.OFFSET;

public class WallPlacerAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = Helpers.res("wall_placer");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public boolean isInputUser() {
        return true;
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
    public String projectileKey() {
        return EntityDataReg.WALL_PLACER.get().setAbilityId();
    }

    @Override
    public void invokeAbility(Player player) {
        var genericProjectile = new GenericProjectile(player, 0.06, projectileKey(), abilityId.getPath().intern());
        fireUtilityProjectile(genericProjectile, player);
    }

    @Override
    public int getAbilityCost() {
        return 2;
    }

    @Override
    public String requiredUnlock() {
        return FarmersTouchAbility.abilityId.getPath();
    }

    @Override
    public AbilityHolder setModifiers() {
        int high = 13;
        return new AbilityBuilder(abilityId.getPath().intern())
            .setMana(30, 15, 5, 1, 1.5)
            .setBlockSize(high, 1, 2, 1, 1.5)
            .setModifierWithStepSet(OFFSET, high, 0, true, high, 1, 1, 1, 1.5)
            .buildAndReturn();
    }

}
