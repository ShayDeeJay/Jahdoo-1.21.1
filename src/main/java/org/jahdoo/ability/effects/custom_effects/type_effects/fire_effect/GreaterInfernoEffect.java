package org.jahdoo.ability.effects.custom_effects.type_effects.fire_effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.ability.effects.EffectParticles;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.utils.ModHelpers.sendEffectPacketsToPlayerDistance;
import static org.jahdoo.utils.PositionGetters.getOuterRingOfRadiusRandom;

public class GreaterInfernoEffect extends MobEffect {


    public GreaterInfernoEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(255, 68, 0));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.isAlive()){
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                int getRandomChance = ModHelpers.Random.nextInt(0, Math.max((20 - pAmplifier), 1));
                if (getRandomChance == 0) {
                    DamageUtil.damageWithJahdoo(targetEntity, pAmplifier);
                }
                EffectParticles.setEffectParticle(getRandomChance, targetEntity, serverLevel, getElement(), SoundEvents.PLAYER_HURT_ON_FIRE);
                targetEntity.addEffect(new CustomMobEffect(MobEffects.GLOWING.getDelegate(), 2, 1));
                var effectInstance = new CustomMobEffect(EffectsRegister.INFERNO_EFFECT, 10,pAmplifier);
                sendEffectPacketsToPlayerDistance(targetEntity.position(), 50, serverLevel, targetEntity.getId(), effectInstance);
                getOuterRingOfRadiusRandom(targetEntity.position(), 1.5, 10, pos -> setParticleNova(pos.add(0,0,0), targetEntity));
                novaDamageBehaviour(targetEntity);
            }
        }

        return true;
    }

    private static @NotNull AbstractElement getElement() {
        return ElementRegistry.INFERNO.get();
    }


    private void novaDamageBehaviour(LivingEntity targetEntity){
        targetEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            targetEntity,
            targetEntity.getBoundingBox()
                .inflate(2,0, 2)
                .deflate(0,1,0 )
        ).forEach(livingEntity -> livingEntity.addEffect(new CustomMobEffect(EffectsRegister.INFERNO_EFFECT, 2, 1)));
    }
    
    public static void setParticleNova(Vec3 worldPosition, LivingEntity livingEntity){
        var element = getElement();
        var positionScrambler = worldPosition.add(0,1,0);
        var directions = positionScrambler.subtract(livingEntity.position()).normalize();
        var lifetime = 4;
        var size = ModHelpers.Random.nextDouble(0.2, 0.4);
        var col1 = element.particleColourPrimary();
        var col2 = element.particleColourSecondary();
        var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, lifetime, (float) (size - 0.2), col1, col2, true);
        var randomSpeed = ModHelpers.Random.nextDouble(0.1, 0.3);

        ParticleHandlers.sendParticles(
            livingEntity.level(), genericParticle, worldPosition, 0, directions.x, directions.y + 0.05, directions.z, randomSpeed
        );
    }

    @Override
    public MobEffectCategory getCategory() {
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
