package org.jahdoo.all_magic.effects.custom_effects.type_effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.all_magic.effects.EffectParticles;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.CustomMobEffect;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleHandlers.spawnElectrifiedParticles;

public class LightningEffect extends MobEffect {

    public LightningEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = GeneralHelpers.Random.nextInt(0,Math.max((20-pAmplifier), 1));
            if(targetEntity.hasEffect(EffectsRegister.STUN_EFFECT.getDelegate())){
                spawnElectrifiedParticles(serverLevel, targetEntity.position(), ElementRegistry.LIGHTNING.get().getParticleGroup().magicSlow(), 1, targetEntity, 0.1, 1);
            } else {
                if(getRandomChance == 0) {
                    targetEntity.hurt(targetEntity.level().damageSources().magic(), (float) Math.max((double) pAmplifier / 5, 0.5));
                    targetEntity.addEffect(new CustomMobEffect(EffectsRegister.STUN_EFFECT.getDelegate(), pAmplifier > 3 ? 30 : pAmplifier * 10, 10));
                }
            }
            EffectParticles.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.LIGHTNING.get(), SoundRegister.BOLT.get());
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
