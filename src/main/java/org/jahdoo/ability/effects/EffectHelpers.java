package org.jahdoo.ability.effects;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.particle.ParticleHandlers.spawnElectrifiedParticles;
import static org.jahdoo.utils.ModHelpers.sendEffectPacketsToPlayerDistance;

public class EffectHelpers {

    public static void setEffectParticle(
        int getRandomChance,
        LivingEntity targetEntity,
        ServerLevel serverLevel,
        AbstractElement particleGroup,
        SoundEvent soundEvents
    ){
        boolean isChance = getRandomChance == 0;
        if(targetEntity.isAlive()){
            if (isChance) {
                ModHelpers.getSoundWithPosition(targetEntity.level(), targetEntity.blockPosition(), soundEvents, 0.3f);
                spawnElectrifiedParticles(serverLevel, targetEntity.position(), particleGroup.getParticleGroup().magicSlow(), 3, targetEntity, -0.3, -1);
            }
            spawnElectrifiedParticles(serverLevel, targetEntity.position(), particleGroup.getParticleGroup().bakedSlow(), isChance ? 10 : 1, targetEntity, isChance ? 0.1 : 0.08);
            spawnElectrifiedParticles(serverLevel, targetEntity.position(), particleGroup.getParticleGroup().magic(), 1, targetEntity, 0.08);
        }
    }

    public static void GreaterGlowSync(LivingEntity targetEntity, int pAmplifier, ServerLevel serverLevel, Holder<MobEffect> effectsHolder) {
        targetEntity.addEffect(new JahdooMobEffect(MobEffects.GLOWING.getDelegate(), 2, 1));
        targetEntity.addEffect(new JahdooMobEffect(effectsHolder, 10, pAmplifier));
        var effectInstance = new JahdooMobEffect(effectsHolder, 10, pAmplifier);
        sendEffectPacketsToPlayerDistance(targetEntity.position(), 50, serverLevel, targetEntity.getId(), effectInstance);
    }

    public static int getGetRandomChance(int amplifier) {
        return ModHelpers.Random.nextInt(0, Math.max((20 - amplifier), 1));
    }
}
