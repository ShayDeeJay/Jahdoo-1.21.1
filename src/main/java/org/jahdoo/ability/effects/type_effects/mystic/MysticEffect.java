package org.jahdoo.ability.effects.type_effects.mystic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.ability.effects.EffectHelpers;
import org.jahdoo.networking.packet.server2client.MoveClientEntitySyncS2CPacket;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.utils.ModHelpers.*;

public class MysticEffect extends MobEffect {
    public MysticEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(151, 77, 178));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.isAlive()){
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                onTickApply(targetEntity, serverLevel, getElement());
                sendEffectPacketsToPlayerDistance(targetEntity.position(), 50, serverLevel, targetEntity.getId(), new JahdooMobEffect(EffectsRegister.MYSTIC_EFFECT, 10, pAmplifier));
            }
        } else removeThis(targetEntity);

        return true;
    }

    private static @NotNull AbstractElement getElement() {
        return ElementRegistry.MYSTIC.get();
    }

    private void onTickApply(LivingEntity targetEntity, ServerLevel serverLevel, AbstractElement element) {
        targetEntity.addEffect(new JahdooMobEffect(MobEffects.GLOWING.getDelegate(), 2, 1));
        var currentYVelocity = targetEntity.getDeltaMovement().y;
        var newYVelocity = Math.max(currentYVelocity, 0.01);

        if(targetEntity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntitySyncS2CPacket(0, newYVelocity, 0, serverPlayer.getId()));
        } else {
            targetEntity.setDeltaMovement(0, newYVelocity, 0);
        }

        idleAnim(targetEntity, serverLevel, element);
    }

    private static void removeThis(LivingEntity targetEntity) {
        targetEntity.removeEffect(MobEffects.GLOWING);
        targetEntity.removeEffect(EffectsRegister.MYSTIC_EFFECT);
    }

    private static void idleAnim(LivingEntity targetEntity, ServerLevel serverLevel, AbstractElement element) {
        var getRandomChance = Random.nextInt(0, 10);
        var sound = SoundEvents.SOUL_ESCAPE.value();
        EffectHelpers.setEffectParticle(getRandomChance, targetEntity, serverLevel, element, sound);
    }

    @Override
    public void onEffectAdded(@NotNull LivingEntity livingEntity, int amplifier) {
        if(livingEntity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntitySyncS2CPacket(0, 0.7, 0, serverPlayer.getId()));
        } else {
            livingEntity.setDeltaMovement(0, 0.5, 0);
        }

        livingEntity.playSound(getElement().getElementSound());
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
