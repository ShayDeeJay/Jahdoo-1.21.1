package org.jahdoo.ascension.ability.abilities_utility.hammer;

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
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;

import static org.jahdoo.ascension.ability.AbilityBuilder.OFFSET;

public class HammerAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = Helpers.res("hammer");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.LEGENDARY;
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
    public String projectileKey() {
        return EntityDataReg.HAMMER.get().setAbilityId();
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
    public int getAbilityCost() {
        return 6;
    }

    @Override
    public AbilityHolder setModifiers() {
        int high = 13;
        return new AbilityBuilder(abilityId.getPath().intern())
            .setMana(30, 15, 5, 1, 1.5)
            .setBlockSize(high, 3, 2, 1, 1.5)
            .setModifierWithStepSet(OFFSET, high,0, true, high, 1, 1, 1, 1.5)
            .buildAndReturn();
    }

}
