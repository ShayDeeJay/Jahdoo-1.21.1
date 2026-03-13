package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.magic.effects.EffectHelpers;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.trial_nexus.magic.effects.EffectHelpers.setEffectParticle;
import static org.jahdoo.trial_nexus.utils.PositionFinders.getOuterRingOfRadiusRandom;

public class InfernoEffect extends AbstractEntityEffect {

    public static final String INFERNO_EFFECT = "inferno_effect";

    @Override
    public String id() {
        return INFERNO_EFFECT;
    }

    @Override
    public AbstractElement getElement() {
        return ElementReg.inferno();
    }

    @Override
    public void greaterEffect(LivingEntity targetEntity) {
        getOuterRingOfRadiusRandom(targetEntity.position(), 1.5, 10, pos -> setParticleNova(pos.add(0,0,0), targetEntity));
        novaDamageBehaviour(targetEntity);
    }

    @Override
    public AttachmentType<AbstractEntityEffect> getAttachment() {
        return AttachmentReg.INFERNO_EFFECT.get();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.INFERNO_ICON;
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
                .inflate(2,0,2)
                .deflate(0,1,0)
        ).forEach(
            livingEntity -> {
                if (avoidOwner(livingEntity)) setTypeEffect(InfernoEffect::new, livingEntity, livingEntity, false, 10, 10);
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
