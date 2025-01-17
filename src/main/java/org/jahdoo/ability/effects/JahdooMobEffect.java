package org.jahdoo.ability.effects;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class JahdooMobEffect extends MobEffectInstance {

    public JahdooMobEffect(Holder<MobEffect> pEffect, int pDuration, int pAmplifier) {
        super(pEffect, pDuration, pAmplifier, false, false, false);
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity) {
        super.onEffectAdded(pLivingEntity);
    }

}
