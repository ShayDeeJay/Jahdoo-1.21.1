package org.jahdoo.trial_nexus.ability.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.particle.ParticleHandlers;

import java.util.List;

import static net.minecraft.world.effect.MobEffectCategory.BENEFICIAL;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class ChampionEffect extends MobEffect {

    public ChampionEffect() {
        super(BENEFICIAL, 3436524);
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }

    @Override
    public MobEffectCategory getCategory() {
        return BENEFICIAL;
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        var level = targetEntity.level();

        if(level.isClientSide){
            var width = targetEntity.getBbWidth();
            var color = Helpers.listRandom(List.of(AETHER_BLUE, CHAMPION_GOLD, EXPERIENCE_GREEN, NEGATIVE_RED));
            var particle = ParticleHandlers.getNonBakedParticles(color, color, 27, Random.nextFloat(Math.max(width, 1F), Math.max(width * 1.5F, 1.5F)));
            var x = targetEntity.getRandomX(0.5);
            var y = targetEntity.getRandomY();
            var z = targetEntity.getRandomZ(0.5);
            double xSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
            double ySpeed = Random.nextDouble(0.1, 0.3);
            double zSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
            ParticleHandlers.sendParticles(level, particle, new Vec3(x, y, z), 1, xSpeed, ySpeed, zSpeed, 60);
        }

        return true;
    }

}
