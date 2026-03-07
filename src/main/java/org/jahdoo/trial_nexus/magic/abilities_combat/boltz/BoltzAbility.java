package org.jahdoo.trial_nexus.magic.abilities_combat.boltz;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;
import org.jahdoo.trial_nexus.magic.Ability;
import org.jahdoo.trial_nexus.magic.AbilityBuilder;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static net.minecraft.sounds.SoundSource.NEUTRAL;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;

public class BoltzAbility extends Ability {

    private static final String TOTAL_BOLTS = "Total Boltz";
    public static final ResourceLocation abilityId = JahdooHelpers.res("boltz");
    public static final String DISCHARGE_RADIUS = "Discharge Radius";

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
        return ElementReg.frost();
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(50)
            .setStaticCooldown(200)
            .setDamage(30,10,5, 1)
            .setEffectDuration(300,100,50, 1)
            .setEffectStrength(10, 0, 1, 1)
            .setEffectChance(20,5,5, 1)
            .setAbilityTagModifiersRandom(DISCHARGE_RADIUS, 3, 1, true, 1, 1)
            .setAbilityTagModifiersRandom(TOTAL_BOLTS, 6, 2, true, 1, 1)
            .buildAndReturn();
    }

    @Override
    public void invokeAbility(Player player) {
        var amplifier = 1;
        var totalShots = (int) CasterData.getSpecificValue(player, TOTAL_BOLTS) * amplifier;
        var direction = player.getLookAngle();
        var particleOptions = ParticleHandlers.genericParticle(ParticleStore.ELECTRIC_PARTICLE, this.getElemenType(), 5, 1.2f, 0.5);

        for (int i = 0; i < totalShots; i++) {
            var elementProjectile = new ElementProjectile(
                EntityReg.FROST_ELEMENT_PROJECTILE.get(),
                player,
                EntityDataReg.BOLTZ.get().setAbilityId(),
                0,
                abilityId.getPath().intern()
            );
            var spread =  0.8 * amplifier; // Adjust the spread value as needed
            var spreadX = direction.x + (Math.random() - 0.5) * spread;
            var spreadY = direction.y + (Math.random() - 0.5) * spread;
            var spreadZ = direction.z + (Math.random() - 0.5) * spread;

            fireProjectileDirection(elementProjectile, player, 0.5f, new Vec3(spreadX, spreadY, spreadZ));
        }

        for(int i = 0; i < totalShots * 5; i++){
            var spread = 0.8; // Adjust the spread value as needed
            var spreadX = direction.x + (Math.random() - 0.5) * spread;
            var spreadY = direction.y + (Math.random() - 0.5) * spread;
            var spreadZ = direction.z + (Math.random() - 0.5) * spread;
            sendParticles(player.level(), particleOptions, player.position().add(0,1.5,0), 0, spreadX, spreadY, spreadZ, 1);
        }

        SoundHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundReg.ORB_CREATE.get(), NEUTRAL, 0.5f,1.5f);
    }

    @Override
    public int levelRequirement() {
        return 100;
    }

    @Override
    public int getAbilityCost() {
        return 0;
    }

}
