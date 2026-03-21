package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.element.Inferno;
import org.jahdoo.trial_nexus.magic.effects.EffectHelpers;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.trial_nexus.magic.effects.EffectHelpers.setEffectParticle;
import static org.jahdoo.trial_nexus.utils.PositionFinders.getOuterRingOfRadiusRandom;

public class InfernoEffect extends AbstractElementEffect {

    @Override
    public AbstractElement getElement() {
        return ElementReg.inferno();
    }

    @Override
    public String getName() {
        return Inferno.abilityId;
    }

    @Override
    public void setEffect(LivingEntity applier, LivingEntity target, int time) {
        applyInfernoEffect(applier, target, time);
    }

    @Override
    public void setGreaterEffect(LivingEntity applier, LivingEntity target, int time) {
        applyGreaterInfernoEffect(applier, target, time);
    }

    @Override
    public void greaterEffect(LivingEntity targetEntity) {
        getOuterRingOfRadiusRandom(targetEntity.position(), getSecondaryValue() - 0.5, 10, pos -> setParticleNova(pos.add(0,0,0), targetEntity));
        novaDamageBehaviour(targetEntity);
    }

    @Override
    public void onActive(LivingEntity livingEntity) {
        if(livingEntity.level() instanceof ServerLevel serverLevel) {
            var getRandomChance = EffectHelpers.getGetRandomChance(2);
            if (getRandomChance == 0) doDamage(livingEntity, serverLevel);
            setEffectParticle(getRandomChance, livingEntity, serverLevel, getElement(), SoundReg.FIRE_ABILITY.get());
        }
    }

    private void novaDamageBehaviour(LivingEntity targetEntity){
        targetEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            targetEntity,
            targetEntity.getBoundingBox()
                .inflate(getSecondaryValue(), 0, getSecondaryValue())
                .deflate(0, targetEntity.getBbHeight()/2 ,0)
        ).forEach(
            livingEntity -> {
                if (avoidOwner(livingEntity)) {
                    if(targetEntity.level() instanceof ServerLevel serverLevel){
                        applyInfernoEffect(getOwner(serverLevel), livingEntity, 50);
                    }
                }
            }
        );
    }

    public static void setParticleNova(Vec3 worldPosition, LivingEntity livingEntity){
        var element = ElementReg.inferno();
        var positionScrambler = worldPosition.add(0,1,0);
        var directions = positionScrambler.subtract(livingEntity.position()).normalize();
        var lifetime = 4;
        var size = JahdooHelpers.Random.nextDouble(0.2, 0.4);
        var col1 = element.partColourA();
        var col2 = element.partColourB();
        var genericParticle = ParticleHandlers.genericParticle(GENERIC_PARTICLE, lifetime, (float) (size - 0.2), col1, col2, true);
        var randomSpeed = JahdooHelpers.Random.nextDouble(0.1, 0.3);

        ParticleHandlers.sendParticles(
            livingEntity.level(), genericParticle, worldPosition, 0, directions.x, directions.y + 0.05, directions.z, randomSpeed
        );
    }

}
