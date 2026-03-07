package org.jahdoo.trial_nexus.magic.effects.type_effects.frost;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.magic.abilities_combat.storm_rush.StormRushAbility;
import org.jahdoo.trial_nexus.magic.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.effect.MobEffectCategory.BENEFICIAL;
import static org.jahdoo.trial_nexus.magic.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleHandlers.particleBurst;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;

public class FrostStormEffect extends MobEffect {

    public FrostStormEffect() {
        super(BENEFICIAL, color(45, 169, 255));
    }

    @Override
    public MobEffectCategory getCategory() {
        return BENEFICIAL;
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
        var level = targetEntity.level();
        var holder = CasterData.entityHolder(targetEntity, StormRushAbility.abilityId.getPath().intern()).data().abilityProperties();
        var duration = holder.get(EFFECT_DURATION).actualValue();
        var strength = holder.get(EFFECT_STRENGTH).actualValue();
        var applyChance = holder.get(EFFECT_CHANCE).actualValue();

        if(applyChance == 1 || Random.nextDouble(applyChance) == 0){
            if (level instanceof ServerLevel) {
                novaDamageBehaviour(targetEntity, (int) duration, (int) strength);
            }
        }

        if (level.isClientSide) {
            for (int i = 0; i < 30; i++) {
                var particle = ParticleHandlers.getAllParticleTypes(ElementReg.frost(), 6, Random.nextFloat(1F, 1.5F));
                var x = targetEntity.getRandomX(0.5);
                var y = targetEntity.getRandomY();
                var z = targetEntity.getRandomZ(0.5);
                double xSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
                double ySpeed = Random.nextDouble(0.1, 0.3);
                double zSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
                ParticleHandlers.sendParticles(level, particle, new Vec3(x, y, z), 1, xSpeed, ySpeed, zSpeed, 60);
            }
        }

        return true;
    }

    private void novaDamageBehaviour(LivingEntity targetEntity, int duration, int amp){
        targetEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            targetEntity,
            targetEntity.getBoundingBox().inflate(1)
        ).forEach(livingEntity -> {
            if(!livingEntity.hasEffect(EffectReg.FROST_EFFECT)){
                livingEntity.addEffect(new JahdooMobEffect(EffectReg.FROST_EFFECT, duration, amp));
                particleBurst(
                    livingEntity.level(), livingEntity.position().add(0, 0.2, 0), 10,
                    genericParticle(SOFT_PARTICLE, ElementReg.frost(), 5, 1.4f),
                    0, 1.5, 0, 0.1f
                );
                JahdooHelpers.getSoundWithPositionV(livingEntity.level(), livingEntity.position(), SoundReg.FROST_ABILITY.get(), 1, 1);
            }
        });
    }
}
