package org.jahdoo.ascension.ability.abilities_combat.fireball;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.abilities_combat.BurningSkullsAbility;
import org.jahdoo.ascension.ability.abilities_combat.hellfire.HellfireAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;

import static org.jahdoo.ascension.ability.abilities_combat.armageddon.ArmageddonModule.IS_BUDDY;

public class FireballAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("fireball");
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
    public String requiredUnlock() {
        return HellfireAbility.abilityId.getPath();
    }

    @Override
    public void invokeAbility(Player player) {
        var projCount = 1;
        fireMultiShotProjectile(projCount, 0.5f, player, 0.4,
            () -> new ElementProjectile(
                EntityReg.INFERNO_ELEMENT_PROJECTILE.get(), player,
                EntityDataReg.FIRE_BALL.get().setAbilityId(), projCount == 1 ? offsetShoot(player) : 0,
                abilityId.getPath().intern()
            )
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
            .setDamage(45, 20, 5, 2)
            .setEffectDuration(300, 100, 20, 1)
            .setEffectStrength(10, 0, 1, 1)
            .setEffectChance(50, 10, 10, 2)
            .setModifierWithoutBounds(IS_BUDDY, 0)
            .setAbilityTagModifiersRandom(NOVA_RANGE, 6, 3, true, 1, 2)
            .buildAndReturn();
    }

}
