package org.jahdoo.ability.effects.custom_effects.type_effects.mystic_effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.ability.effects.EffectParticles;
import org.jahdoo.networking.packet.server2client.EffectSyncS2CPacket;
import org.jahdoo.networking.packet.server2client.MoveClientEntitySyncS2CPacket;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.ModHelpers.sendPacketsToPlayer;

public class MysticEffect extends MobEffect {
    public static final ResourceLocation att = ModHelpers.res("arcane_effects");

    public MysticEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {

        if(targetEntity.isAlive()){
            var element = ElementRegistry.MYSTIC.get();
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                onTickApply(targetEntity, pAmplifier, serverLevel, element);
                var packet = new EffectSyncS2CPacket(targetEntity.getId(), 4, pAmplifier);
                sendPacketsToPlayer(serverLevel, packet);
            }
        } else removeThis(targetEntity);

        return true;
    }

    private void onTickApply(LivingEntity targetEntity, int pAmplifier, ServerLevel serverLevel, AbstractElement element) {
        targetEntity.addEffect(new CustomMobEffect(MobEffects.GLOWING.getDelegate(), 2, 1));
        var currentYVelocity = targetEntity.getDeltaMovement().y;
        var newYVelocity = Math.max(currentYVelocity, 0.01);

        if(targetEntity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntitySyncS2CPacket(0, newYVelocity, 0, serverPlayer.getId()));
        } else {
            targetEntity.setDeltaMovement(0, newYVelocity, 0);
        }

        var ampFix = Math.min(pAmplifier, 10);
        if (Random.nextInt(0, 30 - ampFix) == 0) {
            PositionGetters.getOuterRingOfRadiusRandom(targetEntity.position(), targetEntity.getBbWidth() / 4, 40,
                worldPosition -> this.setParticleNova(targetEntity, worldPosition, element)
            );
            explosionHandler(targetEntity, ampFix, serverLevel);
        } else {
            idleAnim(targetEntity, serverLevel, element);
        }
    }

    private static void removeThis(LivingEntity targetEntity) {
        targetEntity.removeEffect(MobEffects.GLOWING);
        targetEntity.removeEffect(EffectsRegister.MYSTIC_EFFECT);
    }

    private static void idleAnim(LivingEntity targetEntity, ServerLevel serverLevel, AbstractElement element) {
        var getRandomChance = Random.nextInt(0, 10);
        var sound = SoundEvents.SOUL_ESCAPE.value();
        EffectParticles.setEffectParticle(getRandomChance, targetEntity, serverLevel, element, sound);
    }

    private static void explosionHandler(LivingEntity targetEntity, int pAmplifier, ServerLevel serverLevel) {
        DamageUtil.damageWithJahdoo(targetEntity, pAmplifier);
        targetEntity.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            targetEntity,
            targetEntity.getBoundingBox().inflate(4)
        ).forEach(damage -> DamageUtil.damageWithJahdoo(damage, pAmplifier /2));
        ModHelpers.getSoundWithPosition(serverLevel, targetEntity.blockPosition(), SoundRegister.EXPLOSION.get(), 1, 1.4f);
        ModHelpers.getSoundWithPosition(serverLevel, targetEntity.blockPosition(), SoundEvents.AMETHYST_CLUSTER_BREAK, 0.5f, 0.1f);
    }

    private void setParticleNova(LivingEntity livingEntity, Vec3 worldPosition, AbstractElement element){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), (float) 4);
        var directions = positionScrambler.subtract(livingEntity.position()).normalize();
        var genericParticle = genericParticleOptions(
            MAGIC_PARTICLE_SELECTION,
            6, 4, element.particleColourPrimary(), element.particleColourSecondary(), false
        );
        var bakedParticle = new BakedParticleOptions(
            element.getTypeId(), 6, 8, false
        );
        var getRandomParticle = List.of(bakedParticle, genericParticle);

        ParticleHandlers.sendParticles(
            livingEntity.level(),
            getRandomParticle.get(Random.nextInt(2)),
            worldPosition.add(0, livingEntity.getBbHeight()/2, 0),
            0, directions.x, directions.y, directions.z,
            Random.nextDouble(0.8,1.0)
        );
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {

        if(livingEntity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntitySyncS2CPacket(0, 0.7, 0, serverPlayer.getId()));
        } else {
            livingEntity.setDeltaMovement(0, 0.5, 0);
        }

        livingEntity.playSound(ElementRegistry.MYSTIC.get().getElementSound());
        livingEntity.playSound(SoundRegister.DASH_EFFECT_INSTANT.get(), 1, 0.6f);
        super.onEffectAdded(livingEntity, amplifier);
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
}
