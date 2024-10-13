package org.jahdoo.all_magic.effects.custom_effects.type_effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.effects.EffectParticles;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;

public class VitalityEffect extends MobEffect {


    public VitalityEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int amplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = GeneralHelpers.Random.nextInt(0,Math.max((20-amplifier), 1));
            if(getRandomChance == 0) {
                if(GeneralHelpers.Random.nextInt(0,(20-amplifier)) == 0){
                    ItemStack heartContainer = new ItemStack(ItemsRegister.HEALTH_CONTAINER.get());
                    heartContainer.set(DataComponentRegistry.HEART_CONTAINER.get(), (float) (0.1 * amplifier));
                    double randomAngle = GeneralHelpers.Random.nextDouble() * 2 * Math.PI;
                    double offsetX = -Math.sin(randomAngle) * 2;
                    double offsetZ = Math.cos(randomAngle) * 2;
                    double spawnX = targetEntity.getX() + offsetX;
                    double spawnY = targetEntity.getY() + targetEntity.getEyeHeight() - 0.7; // No vertical offset
                    double spawnZ = targetEntity.getZ() + offsetZ;
                    BehaviorUtils.throwItem(targetEntity, heartContainer, new Vec3(spawnX, spawnY, spawnZ));
                }
                targetEntity.hurt(targetEntity.level().damageSources().magic(), 1);
            }
            EffectParticles.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.VITALITY.get(), SoundEvents.SOUL_ESCAPE.value());
        }
        return true;
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
        return true;
    }
}
