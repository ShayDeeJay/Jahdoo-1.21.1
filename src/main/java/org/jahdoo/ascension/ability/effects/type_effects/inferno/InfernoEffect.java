package org.jahdoo.ascension.ability.effects.type_effects.inferno;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.ability.effects.EffectHelpers;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.ability.effects.EffectHelpers.*;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;

public class InfernoEffect extends MobEffect {


    public InfernoEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(255, 68, 0));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.isAlive()){
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                var getRandomChance = EffectHelpers.getGetRandomChance(pAmplifier);
                if (getRandomChance == 0) DamageUtils.damageWithJahdoo(targetEntity, pAmplifier);
                setEffectParticle(getRandomChance, targetEntity, serverLevel, getElement(), SoundEvents.PLAYER_HURT_ON_FIRE);
            }
        }
        return true;
    }

    private static @NotNull AbstractElement getElement() {
        return ElementRegistry.inferno();
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
