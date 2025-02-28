package org.jahdoo.ascension.ability.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import static net.minecraft.world.effect.MobEffectCategory.BENEFICIAL;

public class Rebound extends MobEffect {

    public Rebound() {
        super(BENEFICIAL, 3436524);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public MobEffectCategory getCategory() {
        return BENEFICIAL;
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }

}
