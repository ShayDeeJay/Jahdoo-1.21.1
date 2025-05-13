package org.jahdoo.trial_nexus.ability.effects.type_effects.inferno;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.trial_nexus.ability.effects.EffectHelpers;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.DamageUtils;

import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.effect.MobEffectCategory.HARMFUL;
import static org.jahdoo.trial_nexus.ability.effects.EffectHelpers.setEffectParticle;
import static org.jahdoo.common.registers.mod.ElementReg.inferno;

public class InfernoEffect extends MobEffect {


    public InfernoEffect() {
        super(HARMFUL, color(255, 68, 0));
    }

    private static AbstractElement getElement() {
        return inferno();
    }

    @Override
    public MobEffectCategory getCategory() {
        return HARMFUL;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int amplifier) {
        return true;
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int amplifier) {

        if(targetEntity.isAlive()){
            if (targetEntity.level() instanceof ServerLevel serverLevel) {
                var getRandomChance = EffectHelpers.getGetRandomChance(amplifier);
                if (getRandomChance == 0) DamageUtils.damageWithJahdoo(targetEntity, amplifier, getElement().damageTypeResourceKey());
                setEffectParticle(getRandomChance, targetEntity, serverLevel, getElement(), SoundEvents.PLAYER_HURT_ON_FIRE);
            }
        }

        return true;

    }

}
