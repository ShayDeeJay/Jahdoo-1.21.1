package org.jahdoo.trial_nexus.magic.abilities_combat.ice_bomb;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.magic.Ability;
import org.jahdoo.trial_nexus.magic.AbilityBuilder;
import org.jahdoo.trial_nexus.magic.abilities_combat.BurningSkullsAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

public class IceBombAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("ice_bomb");

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
        return ElementReg.frost();
    }

    @Override
    public int levelRequirement() {
        return 30;
    }

    @Override
    public void invokeAbility(Player player) {
        BurningSkullsAbility.frostSoundEffect(player);
        fireProjectileNoSound(
            new ElementProjectile(
                EntityReg.FROST_ELEMENT_PROJECTILE.get(), player,
                EntityDataReg.ICE_BOMB.get().setAbilityId(),
                offsetShoot(player),
                abilityId.getPath().intern()
            ),
            player, 0.5F
        );
    }

    @Override
    public int getAbilityCost() {
        return 5;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(45)
            .setStaticCooldown(400)
            .setDamage(20, 10, 2, 1)
            .ricochets(6, 2, 1, 1)
            .setEffectStrength(10, 5, 1, 1)
            .setEffectDuration(600, 300, 100, 2)
            .buildAndReturn();
    }

}
