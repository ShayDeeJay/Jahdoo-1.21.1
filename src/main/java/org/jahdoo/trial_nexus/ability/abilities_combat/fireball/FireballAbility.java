package org.jahdoo.trial_nexus.ability.abilities_combat.fireball;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.abilities_combat.BurningSkullsAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

import static org.jahdoo.trial_nexus.ability.abilities_combat.armageddon.ArmageddonModule.IS_BUDDY;

public class FireballAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("fireball");
    public static final String NOVA_RANGE = "Explosion Radius";

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
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
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
        return ElementReg.inferno();
    }

    @Override
    public int levelRequirement() {
        return 50;
    }

    @Override
    public void invokeAbility(Player player) {
        var projCount = 1;
        fireMultiShotProjectile(projCount, 0.5f, player, 0,
            () -> new ElementProjectile(
                EntityReg.INFERNO_ELEMENT_PROJECTILE.get(), player,
                EntityDataReg.FIRE_BALL.get().setAbilityId(), projCount == 1 ? offsetShoot(player) : 0,
                abilityId.getPath().intern()
            ),
            0
        );
        BurningSkullsAbility.infernoSoundEffect(player);
    }

    @Override
    public int getAbilityCost() {
        return 12;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(600)
            .setDamage(50, 30, 10, 4)
            .setEffectDuration(300, 100, 100, 2)
            .setEffectStrength(6, 0, 2, 1)
            .setEffectChance(40, 10, 10, 2)
            .setModifierWithoutBounds(IS_BUDDY, 0)
            .setAbilityTagModifiersRandom(NOVA_RANGE, 6, 3, true, 1, 2)
            .buildAndReturn();
    }

}
