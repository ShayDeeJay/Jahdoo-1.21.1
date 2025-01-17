package org.jahdoo.ability.effects.type_effects.lightning;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ability.effects.EffectHelpers;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.utils.DamageUtil;

import static org.jahdoo.ability.effects.EffectHelpers.getGetRandomChance;
import static org.jahdoo.particle.ParticleHandlers.spawnElectrifiedParticles;

public class LightningEffect extends MobEffect {

    public LightningEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(110, 176, 186));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = getGetRandomChance(pAmplifier);
            if(targetEntity.hasEffect(EffectsRegister.STUN_EFFECT.getDelegate())){
                spawnElectrifiedParticles(serverLevel, targetEntity.position(), ElementRegistry.LIGHTNING.get().getParticleGroup().magicSlow(), 1, targetEntity, 0.1, 1);
            } else {
                if(getRandomChance == 0) {
                    DamageUtil.damageWithJahdoo(targetEntity, Math.max((double) pAmplifier / 5, 0.5));
                    targetEntity.addEffect(new JahdooMobEffect(EffectsRegister.STUN_EFFECT.getDelegate(), pAmplifier > 3 ? 30 : pAmplifier * 10, 10));
                }
            }

            EffectHelpers.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.LIGHTNING.get(), SoundRegister.BOLT.get());
        }

        return true;
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
