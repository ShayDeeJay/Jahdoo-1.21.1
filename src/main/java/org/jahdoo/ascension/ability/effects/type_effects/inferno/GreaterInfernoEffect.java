package org.jahdoo.ascension.ability.effects.type_effects.inferno;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.effects.EffectHelpers;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EffectReg;

import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.effect.MobEffectCategory.HARMFUL;
import static org.jahdoo.ascension.utils.PositionFinders.getOuterRingOfRadiusRandom;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.registers.mod.ElementReg.inferno;

public class GreaterInfernoEffect extends MobEffect {

    public GreaterInfernoEffect() {
        super(HARMFUL, color(255, 68, 0));
    }

    private static AbstractElement getElement() {
        return inferno();
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
        if(targetEntity.isAlive()){
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                EffectHelpers.GreaterGlowSync(targetEntity, amplifier, serverLevel, EffectReg.INFERNO_EFFECT);
                getOuterRingOfRadiusRandom(targetEntity.position(), 1.5, 10, pos -> setParticleNova(pos.add(0,0,0), targetEntity));
                novaDamageBehaviour(targetEntity);
            }
        }
        return true;
    }

    private void novaDamageBehaviour(LivingEntity targetEntity){
        targetEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            targetEntity,
            targetEntity.getBoundingBox()
                .inflate(2,0, 2)
                .deflate(0,1,0 )
        ).forEach(livingEntity -> livingEntity.addEffect(new JahdooMobEffect(EffectReg.INFERNO_EFFECT, 2, 1)));
    }

    public static void setParticleNova(Vec3 worldPosition, LivingEntity livingEntity){
        var element = getElement();
        var positionScrambler = worldPosition.add(0,1,0);
        var directions = positionScrambler.subtract(livingEntity.position()).normalize();
        var lifetime = 4;
        var size = Helpers.Random.nextDouble(0.2, 0.4);
        var col1 = element.textColourA();
        var col2 = element.textColourB();
        var genericParticle = ParticleHandlers.genericParticle(GENERIC_PARTICLE, lifetime, (float) (size - 0.2), col1, col2, true);
        var randomSpeed = Helpers.Random.nextDouble(0.1, 0.3);

        ParticleHandlers.sendParticles(
            livingEntity.level(), genericParticle, worldPosition, 0, directions.x, directions.y + 0.05, directions.z, randomSpeed
        );
    }

}
