package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.element.Mystic;
import org.jahdoo.trial_nexus.magic.effects.EffectHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.List;

import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class MysticEffect extends AbstractElementEffect {

    @Override
    public AbstractElement getElement() {
        return ElementReg.mystic();
    }

    @Override
    public String getName() {
        return Mystic.abilityId;
    }

    @Override
    public void setEffect(LivingEntity applier, LivingEntity target, int time) {
        applyMysticEffect(applier, target, time);
    }

    @Override
    public void setGreaterEffect(LivingEntity applier, LivingEntity target, int time) {
        applyGreaterMysticEffect(applier, target, time);
    }

    @Override
    public void onStarted(LivingEntity livingEntity) {
        livingEntity.playSound(SoundReg.SUSPEND.get(), 1.0F, 0.6F);
    }

    @Override
    public void greaterEffect(LivingEntity entity) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            if (MathHelpers.percentageChance(getSecondaryValue())) {
                PositionFinders.getOuterRingOfRadiusRandom(entity.position(), entity.getBbWidth() / 4, 40,
                    worldPosition -> this.setParticleNova(entity, worldPosition, getElement())
                );
                explosionHandler(entity, serverLevel);
            }
        }
    }

    @Override
    public void onActive(LivingEntity livingEntity) {
        if(livingEntity.level() instanceof ServerLevel serverLevel) {
            var getRandomChance = Random.nextInt(0, 10);
            var sound = SoundReg.QUANTUM.get();
            EffectHelpers.setEffectParticle(getRandomChance, livingEntity, serverLevel, ElementReg.mystic(), sound, 0.05F, Random.nextFloat(1.4F, 2F));
        }

        var getMaxHeight = ((double) this.getMaxTime() - this.getTimer()) / 8;
        livingEntity.setDeltaMovement(0, this.getTimer() > (getMaxTime() - 10) ? getMaxHeight : 0 , 0);
    }

    private void explosionHandler(LivingEntity targetEntity, ServerLevel serverLevel) {
        doDamage(targetEntity, serverLevel);
        targetEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            targetEntity,
            targetEntity.getBoundingBox().inflate(3)
        ).forEach(
            livingEntity -> {
                if(avoidOwner(livingEntity)) doDamage(livingEntity, serverLevel, getDamage()/2);
            }
        );
        SoundHelpers.getSoundWithPosition(serverLevel, targetEntity.blockPosition(), getElement().sound(), SoundSource.NEUTRAL, 1.2F, 1F);
    }

    private void setParticleNova(LivingEntity livingEntity, Vec3 worldPosition, AbstractElement element){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), (float) 4);
        var directions = positionScrambler.subtract(livingEntity.position()).normalize();
        var colourPrimary = element.partColourA();
        var colourSecondary = element.partColourB();
        var bakedParticle = bakedParticle(element.id(), 6, 8, false);
        var genericParticle = ParticleHandlers.genericParticle(MAGIC_PARTICLE, 6, 4, colourPrimary, colourSecondary, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle);

        ParticleHandlers.sendParticles(
            livingEntity.level(),
            getRandomParticle.get(Random.nextInt(2)),
            worldPosition.add(0, livingEntity.getBbHeight()/2, 0),
            0, directions.x, directions.y, directions.z,
            Random.nextDouble(0.8,1.0)
        );
    }


}
