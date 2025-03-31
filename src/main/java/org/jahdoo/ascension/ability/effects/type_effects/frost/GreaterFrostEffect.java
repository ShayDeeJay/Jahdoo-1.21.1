package org.jahdoo.ascension.ability.effects.type_effects.frost;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ascension.ability.effects.EffectHelpers;
import org.jahdoo.common.registers.EffectReg;

import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.effect.MobEffectCategory.HARMFUL;

public class GreaterFrostEffect extends MobEffect {

    public GreaterFrostEffect() {
        super(HARMFUL, color(45, 169, 255));
    }

    @Override
    public MobEffectCategory getCategory() {
        return HARMFUL;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int amplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            EffectHelpers.GreaterGlowSync(targetEntity, amplifier, serverLevel, EffectReg.FROST_EFFECT);
        }
        return true;
    }

}
