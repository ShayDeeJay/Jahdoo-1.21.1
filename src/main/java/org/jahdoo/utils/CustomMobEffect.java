package org.jahdoo.utils;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.entities.EternalWizard;
import org.jetbrains.annotations.Nullable;

public class CustomMobEffect extends MobEffectInstance {

    public CustomMobEffect(Holder<MobEffect> pEffect, int pDuration, int pAmplifier) {
        super(pEffect, pDuration, pAmplifier, false, false, false);
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity) {
        super.onEffectAdded(pLivingEntity);
    }

}
