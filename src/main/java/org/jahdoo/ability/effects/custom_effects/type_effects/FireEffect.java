package org.jahdoo.ability.effects.custom_effects.type_effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ability.effects.EffectParticles;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;

public class FireEffect extends MobEffect {


    public FireEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = ModHelpers.Random.nextInt(0,Math.max((20-pAmplifier), 1));
            if(getRandomChance == 0) {
                DamageUtil.damageWithJahdoo(targetEntity, pAmplifier);
            }
            EffectParticles.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.INFERNO.get(), SoundEvents.PLAYER_HURT_ON_FIRE);
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
