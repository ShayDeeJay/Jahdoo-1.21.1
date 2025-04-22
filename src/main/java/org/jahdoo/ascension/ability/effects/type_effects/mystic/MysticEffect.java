package org.jahdoo.ascension.ability.effects.type_effects.mystic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.ability.effects.EffectHelpers;
import org.jahdoo.common.networking.server2client.MoveClientEntityS2CP;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.ascension.utils.Helpers.*;

public class MysticEffect extends MobEffect {

    public MysticEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(151, 77, 178));
    }

    private static @NotNull AbstractElement getElement() {
        return ElementReg.mystic();
    }

    @Override
    public MobEffectCategory getCategory() {
        return MobEffectCategory.HARMFUL;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
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
    public boolean applyEffectTick(LivingEntity targetEntity, int amplifier) {
        if(targetEntity.isAlive()){
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                onTickApply(targetEntity, serverLevel, getElement());
            }
        } else removeThis(targetEntity);

        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        if(livingEntity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntityS2CP(0, 0.7, 0, serverPlayer.getId()));
        } else {
            livingEntity.setDeltaMovement(0, 0.5, 0);
        }

//        livingEntity.playSound(getElement().sound());
        livingEntity.playSound(SoundReg.SUSPEND.get());
        super.onEffectAdded(livingEntity, amplifier);
    }

    private void onTickApply(LivingEntity targetEntity, ServerLevel serverLevel, AbstractElement element) {
        targetEntity.addEffect(new JahdooMobEffect(MobEffects.GLOWING.getDelegate(), 2, 1));
        var currentYVelocity = targetEntity.getDeltaMovement().y;
        var newYVelocity = Math.max(currentYVelocity, 0.01);

        targetEntity.fallDistance = 20;

        if(targetEntity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntityS2CP(0, newYVelocity, 0, serverPlayer.getId()));
        } else {
            targetEntity.setDeltaMovement(0, newYVelocity, 0);
        }

        idleAnim(targetEntity, serverLevel, element);
    }


}
