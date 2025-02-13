package org.jahdoo.ability.effects.type_effects.lightning;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ability.abilities.ability_data.LifeSiphonAbility;
import org.jahdoo.ability.effects.EffectHelpers;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.ability.ability_components.LightningTrail.getLightningTrailModifiers;
import static org.jahdoo.particle.ParticleHandlers.spawnElectrifiedParticles;
import static org.jahdoo.utils.ModHelpers.Random;

public class GreaterLightningEffect extends MobEffect {

    public GreaterLightningEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(110, 176, 186));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            shootSpikesRandomly(0.8f, targetEntity, pAmplifier);
            EffectHelpers.GreaterGlowSync(targetEntity, pAmplifier, serverLevel, EffectsRegister.LIGHTNING_EFFECT);
        }
        return true;
    }

    public static void shootSpikesRandomly(float velocity, LivingEntity targetEntity, int amplifier){
        if(Random.nextInt(20) == 0){
            var speeds = Random.nextFloat(velocity - 0.2f, velocity);
            for (int entitiesShot = 0; entitiesShot < Random.nextInt(2, 4); entitiesShot++) {
                var hasBlockBelow = targetEntity.verticalCollisionBelow;
                var theta = Random.nextDouble() * Math.PI * (hasBlockBelow ? 1 : 4);
                var phi = Random.nextDouble() * Math.PI;
                var x = Math.sin(phi) * Math.cos(theta);
                var y = Math.sin(phi) * Math.sin(theta);
                var z = Math.cos(phi);
                var newPosition = targetEntity.position().add(targetEntity.getDeltaMovement().scale(4.5));
                var genericProjectile = new GenericProjectile(
                    targetEntity, newPosition.x, newPosition.y, newPosition.z,
                    EntityPropertyRegister.LIGHTNING_TRAIL.get().setAbilityId(),
                    getLightningTrailModifiers(5 * amplifier, 4, 8, 1),
                    ElementRegistry.LIGHTNING.get(),
                    LifeSiphonAbility.abilityId.getPath()
                );
                ModHelpers.getSoundWithPosition(targetEntity.level(), targetEntity.blockPosition(), SoundRegister.BOLT.get(), 0.3f, 0.8f);
                genericProjectile.shoot(x, y, z, speeds + 0.1f, 0);
                targetEntity.level().addFreshEntity(genericProjectile);
            }
        }
    }

    @Override
    public @NotNull MobEffectCategory getCategory() {
        return MobEffectCategory.HARMFUL;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }
}
