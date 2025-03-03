package org.jahdoo.ascension.ability.effects.type_effects.mystic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.ability.effects.EffectHelpers;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.networking.server2client.EffectSyncS2CP;
import org.jahdoo.common.networking.server2client.MoveClientEntityS2CP;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.List;

import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;

public class GreaterMysticEffect extends MobEffect {

    public GreaterMysticEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(151, 77, 178));
    }

    private static AbstractElement getElement() {
        return ElementReg.mystic();
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
        return false;
    }

    private static void removeThis(LivingEntity targetEntity) {
        targetEntity.removeEffect(MobEffects.GLOWING);
        targetEntity.removeEffect(EffectReg.MYSTIC_EFFECT);
    }

    private static void idleAnim(LivingEntity targetEntity, ServerLevel serverLevel, AbstractElement element) {
        var getRandomChance = Random.nextInt(0, 10);
        var sound = SoundEvents.SOUL_ESCAPE.value();
        EffectHelpers.setEffectParticle(getRandomChance, targetEntity, serverLevel, element, sound);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        if(livingEntity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntityS2CP(0, 0.7, 0, serverPlayer.getId()));
        } else {
            livingEntity.setDeltaMovement(0, 0.5, 0);
        }

        livingEntity.playSound(getElement().sound());
        livingEntity.playSound(SoundReg.DASH_EFFECT_INSTANT.get(), 1, 0.6f);
        super.onEffectAdded(livingEntity, amplifier);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {

        if(targetEntity.isAlive()){
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                onTickApply(targetEntity, pAmplifier, serverLevel, getElement());
                sendEffectPacketsToPlayerDistance(targetEntity.position(), 50, serverLevel, targetEntity.getId(), new JahdooMobEffect(EffectReg.MYSTIC_EFFECT, 10, pAmplifier));
                sendPacketsToPlayer(serverLevel, new EffectSyncS2CP(targetEntity.getId(), 4, pAmplifier));
            }
        } else removeThis(targetEntity);

        return true;
    }

    private static void explosionHandler(LivingEntity targetEntity, int pAmplifier, ServerLevel serverLevel) {
        DamageUtils.damageWithJahdoo(targetEntity, pAmplifier);
        targetEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            targetEntity,
            targetEntity.getBoundingBox().inflate(4)
        ).forEach(damage -> DamageUtils.damageWithJahdoo(damage, pAmplifier /2));
        Helpers.getSoundWithPosition(serverLevel, targetEntity.blockPosition(), SoundReg.EXPLOSION.get(), 1, 1.4f);
        Helpers.getSoundWithPosition(serverLevel, targetEntity.blockPosition(), SoundEvents.AMETHYST_CLUSTER_BREAK, 0.5f, 0.1f);
    }

    private void setParticleNova(LivingEntity livingEntity, Vec3 worldPosition, AbstractElement element){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), (float) 4);
        var directions = positionScrambler.subtract(livingEntity.position()).normalize();
        var colourPrimary = element.partColourA();
        var colourSecondary = element.partColourB();
        var bakedParticle = bakedParticleOptions(element.id(), 6, 8, false);
        var genericParticle = genericParticleOptions(MAGIC_PARTICLE_SELECTION, 6, 4, colourPrimary, colourSecondary, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle);

        ParticleHandlers.sendParticles(
            livingEntity.level(),
            getRandomParticle.get(Random.nextInt(2)),
            worldPosition.add(0, livingEntity.getBbHeight()/2, 0),
            0, directions.x, directions.y, directions.z,
            Random.nextDouble(0.8,1.0)
        );
    }

    private void onTickApply(LivingEntity targetEntity, int pAmplifier, ServerLevel serverLevel, AbstractElement element) {
        targetEntity.addEffect(new JahdooMobEffect(MobEffects.GLOWING.getDelegate(), 2, 1));
        var currentYVelocity = targetEntity.getDeltaMovement().y;
        var newYVelocity = Math.max(currentYVelocity, 0.01);

        if(targetEntity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntityS2CP(0, newYVelocity, 0, serverPlayer.getId()));
        } else {
            targetEntity.setDeltaMovement(0, newYVelocity, 0);
        }

        var ampFix = Math.min(pAmplifier, 10);
        if (Random.nextInt(0, 30 - ampFix) == 0) {
            PositionFinders.getOuterRingOfRadiusRandom(targetEntity.position(), targetEntity.getBbWidth() / 4, 40,
                worldPosition -> this.setParticleNova(targetEntity, worldPosition, element)
            );
            explosionHandler(targetEntity, ampFix, serverLevel);
        } else {
            idleAnim(targetEntity, serverLevel, element);
        }
    }

}
