package org.jahdoo.ability.effects.custom_effects.type_effects.fire_effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ability.effects.EffectParticles.*;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.utils.ModHelpers.sendEffectPacketsToPlayerDistance;

public class InfernoEffect extends MobEffect {


    public InfernoEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(255, 68, 0));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.isAlive()){
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                var getRandomChance = ModHelpers.Random.nextInt(0, Math.max((20 - pAmplifier), 1));
                if (getRandomChance == 0) DamageUtil.damageWithJahdoo(targetEntity, pAmplifier);
                setEffectParticle(getRandomChance, targetEntity, serverLevel, getElement(), SoundEvents.PLAYER_HURT_ON_FIRE);
                targetEntity.addEffect(new CustomMobEffect(MobEffects.GLOWING.getDelegate(), 2, 1));
                var effectInstance = new CustomMobEffect(EffectsRegister.INFERNO_EFFECT, 10, pAmplifier);
                sendEffectPacketsToPlayerDistance(targetEntity.position(), 50, serverLevel, targetEntity.getId(), effectInstance);
            }
        }

        return true;
    }

    private static @NotNull AbstractElement getElement() {
        return ElementRegistry.INFERNO.get();
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
