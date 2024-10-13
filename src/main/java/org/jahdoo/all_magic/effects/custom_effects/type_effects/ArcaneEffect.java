package org.jahdoo.all_magic.effects.custom_effects.type_effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.all_magic.effects.EffectParticles;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;

import java.util.concurrent.atomic.AtomicInteger;

public class ArcaneEffect extends MobEffect {

    public ArcaneEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        AtomicInteger randomInts = new AtomicInteger(2);

        if(
            !targetEntity.level().getBlockState(targetEntity.blockPosition().below()).canBeReplaced()||
            !targetEntity.level().getBlockState(targetEntity.blockPosition().below(1)).canBeReplaced()||
            !targetEntity.level().getBlockState(targetEntity.blockPosition().below(2)).canBeReplaced()
        ){

            targetEntity.setDeltaMovement(0, 0.12, 0);
            randomInts.set(randomInts.get() == 2 ? 6 : 2);
        }

//        targetEntity.addEffect(new CustomMobEffect(MobEffects.GLOWING.get(), 10,1));

        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = GeneralHelpers.Random.nextInt(0,10);
            EffectParticles.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.MYSTIC.get(), SoundEvents.SOUL_ESCAPE.value());
        }

        return false;
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
