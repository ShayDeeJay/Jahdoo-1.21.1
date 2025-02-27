package org.jahdoo.ascension.ability.effects.type_effects.vitality;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ascension.ability.effects.EffectHelpers;
import org.jahdoo.common.registers.EffectsRegister;

public class GreaterVitalityEffect extends MobEffect {

    public GreaterVitalityEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(226, 51, 119));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int amplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            EffectHelpers.GreaterGlowSync(targetEntity, amplifier, serverLevel, EffectsRegister.VITALITY_EFFECT);
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
