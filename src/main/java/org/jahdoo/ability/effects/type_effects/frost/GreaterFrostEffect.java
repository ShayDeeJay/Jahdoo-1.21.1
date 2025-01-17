package org.jahdoo.ability.effects.type_effects.frost;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ability.effects.EffectHelpers;
import org.jahdoo.registers.EffectsRegister;

public class GreaterFrostEffect extends MobEffect {
    public GreaterFrostEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(45, 169, 255));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            EffectHelpers.GreaterGlowSync(targetEntity, pAmplifier, serverLevel, EffectsRegister.FROST_EFFECT);
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
