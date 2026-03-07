package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.common.networking.server2client.SyncEntityEffectValuesS2CP;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.magic.effects.EffectHelpers;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.List;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class MysticEffect extends AbstractEntityEffect {

    @Override
    public AbstractElement getElement() {
        return ElementReg.mystic();
    }

    @Override
    public AttachmentType<?> getAttachment() {
        return AttachmentReg.MYSTIC_EFFECT.get();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.MYSTIC_ICON;
    }

    @Override
    public void getSecondary(LivingEntity entity) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            if (Random.nextInt(0, 30) == 0) {
                PositionFinders.getOuterRingOfRadiusRandom(entity.position(), entity.getBbWidth() / 4, 40,
                    worldPosition -> this.setParticleNova(entity, worldPosition, getElement())
                );
                explosionHandler(entity, serverLevel);
            }
        }
    }

    @Override
    public void onTick(LivingEntity entity) {
        if(this.started() && this.getTimer() > 0){
            entity.playSound(SoundReg.SUSPEND.get(), 1.0F, 0.6F);
        }

        if(this.isActive()){
            if(entity.level() instanceof ServerLevel serverLevel) {
                var getRandomChance = Random.nextInt(0, 10);
                var sound = SoundReg.QUANTUM.get();
                EffectHelpers.setEffectParticle(getRandomChance, entity, serverLevel, ElementReg.mystic(), sound, 0.05F, Random.nextFloat(1.4F, 2F));
            }

            var getMaxHeight = (double) this.getTimer() / 500;
            entity.setDeltaMovement(0, this.getTimer() > (getMaxTime() - 10) ? getMaxHeight : 0 , 0);
        }

        if(ended()) entity.removeData(AttachmentReg.MYSTIC_EFFECT.get());

        super.onTick(entity);
    }

    private void explosionHandler(LivingEntity targetEntity, ServerLevel serverLevel) {
        //Damage Applied To Effected second ones For Around
        var pAmplifier = getDamage();
        DamageUtils.damageWithJahdoo(targetEntity, pAmplifier, getElement().damageTypeResourceKey());
        targetEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            targetEntity,
            targetEntity.getBoundingBox().inflate(targetEntity.getBbWidth())
        ).forEach(damage -> DamageUtils.damageWithJahdoo(damage, (double) pAmplifier /2, getElement().damageTypeResourceKey()));
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

    public static void serverOnTick(LivingEntity livingEntity) {
        var mysticEffect = AttachmentReg.MYSTIC_EFFECT;
        if(livingEntity.hasData(mysticEffect)) {
            var mystic = livingEntity.getData(mysticEffect);
            mystic.onTick(livingEntity);

            for (var player : livingEntity.level().players()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    sendToPlayer(serverPlayer, new SyncEntityEffectValuesS2CP(livingEntity, livingEntity.getData(mysticEffect)));
                }
            }
        }
    }

    public static void setMysticEffect(LivingEntity entity, LivingEntity target, boolean isSecondary, int time, float damage) {
        if(!target.hasData(AttachmentReg.MYSTIC_EFFECT)) {
            var mystic = new MysticEffect();
            mystic.createEffect(entity, isSecondary, time, damage);

            target.setData(AttachmentReg.MYSTIC_EFFECT, mystic);
        }
    }

}
