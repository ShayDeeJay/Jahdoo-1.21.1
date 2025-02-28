package org.jahdoo.ascension.ability.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;
import static net.minecraft.world.effect.MobEffectCategory.HARMFUL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public class StunEffect extends MobEffect {

    public StunEffect() {
        super(HARMFUL, 3436524);
    }

    @Override
    public boolean isBeneficial() {
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
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        var id = withDefaultNamespace("assets.jahdoo.movement_speed_stun");

        this.addAttributeModifier(MOVEMENT_SPEED, id, -((double) pAmplifier / 100), ADD_MULTIPLIED_BASE);
        return true;
    }

}
