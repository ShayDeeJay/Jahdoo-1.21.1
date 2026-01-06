package org.jahdoo.trial_nexus.ability.abilities_utility.hammer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.AbstractBlockAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

import static org.jahdoo.trial_nexus.ability.AbilityBuilder.OFFSET;

public class HammerAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = Helpers.res("hammer");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public boolean isOutputUser() {
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
    public int levelRequirement() {
        return 0;
    }

    @Override
    public int getAbilityCost() {
        return 1;
    }

    @Override
    public AbilityHolder setModifiers() {
        int high = 13;
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(10)
            .setBlockSize(high, 1, 2, 1)
            .setModifierWithStepSet(OFFSET, high, 0, true, high, 1, 1, 1)
            .toggleVoid(1)
            .toggleSilk(1)
            .toggleSmelt(1)
            .toggleCollect(1)
            .toggleReinforced(1)
            .setFortune(1)
            .buildAndReturn();
    }

}
