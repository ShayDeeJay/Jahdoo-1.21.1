package org.jahdoo.ability.effects.type_effects.frost;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ability.effects.EffectHelpers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

public class FrostEffect extends MobEffect {
    public FrostEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(45, 169, 255));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = ModHelpers.Random.nextInt(0,20);
            EffectHelpers.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.FROST.get(), SoundRegister.ICE_ATTACH.get());
        }
        return true;
    }

    @Override
    public @NotNull MobEffectCategory getCategory() {
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
