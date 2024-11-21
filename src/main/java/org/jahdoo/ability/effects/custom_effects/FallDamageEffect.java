package org.jahdoo.ability.effects.custom_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FallDamageEffect extends MobEffect {


    public FallDamageEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        targetEntity.resetFallDistance();
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
