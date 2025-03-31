package org.jahdoo.ascension.ability.effects.type_effects.frost;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ascension.ability.effects.EffectHelpers;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;

import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.effect.MobEffectCategory.HARMFUL;

public class FrostEffect extends MobEffect {

    public FrostEffect() {
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
            int getRandomChance = Helpers.Random.nextInt(0,20);
            EffectHelpers.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementReg.frost(), SoundReg.ICE_ATTACH.get());
        }
        return true;
    }

}
