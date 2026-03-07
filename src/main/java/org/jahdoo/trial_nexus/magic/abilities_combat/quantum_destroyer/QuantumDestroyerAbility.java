package org.jahdoo.trial_nexus.magic.abilities_combat.quantum_destroyer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.magic.Ability;
import org.jahdoo.trial_nexus.magic.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.registers.EntityReg.MYSTIC_ELEMENT_PROJECTILE;
import static org.jahdoo.common.registers.mod.EntityDataReg.QUANTUM_DESTROYER;

public class QuantumDestroyerAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("quantum_destroyer");
    public static final String ENERGY_RADIUS = "Energy Radius";

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.MYTHIC;
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
        return DISTANCE_CAST;
    }

    @Override
    public int levelRequirement() {
        return 90;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.mystic();
    }

    @Override
    public void invokeAbility(Player player) {
        var position = player.pick(20, 0, false).getLocation();
        var name = QUANTUM_DESTROYER.get().setAbilityId();
        var elementProjectile = new ElementProjectile(MYSTIC_ELEMENT_PROJECTILE.get(), player, name, 0, abilityId.getPath().intern());

        elementProjectile.moveTo(position.x, position.y, position.z);
        fireProjectileNoSound(elementProjectile, player, 0.0f);
        player.playSound(SoundReg.ORB_FIRE.get(), 0.8f, 1.2f);
        player.playSound(SoundReg.MAGIC_EXPLOSION.get(), 0.2f, 1.4f);
    }

    @Override
    public int getAbilityCost() {
        return 20;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(160)
            .setStaticCooldown(3000)
            .setImplosions(10, 4, 2, 2)
            .setDamage(80, 40, 10, 3)
            .setCastingDistance(30, 10, 10, 3)
            .setGravitationalPull(2, 1, 0.2, 2)
            .setAbilityTagModifiersRandom(ENERGY_RADIUS, 6, 3, true, 1, 3)
            .buildAndReturn();
    }

}
