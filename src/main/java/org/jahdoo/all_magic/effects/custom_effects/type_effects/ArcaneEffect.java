package org.jahdoo.all_magic.effects.custom_effects.type_effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.all_magic.effects.EffectParticles;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.all_magic.effects.CustomMobEffect;
import org.jahdoo.utils.ModHelpers;

public class ArcaneEffect extends MobEffect {
    public static final ResourceLocation att = ModHelpers.res("arcane_effects");

    public ArcaneEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        targetEntity.addEffect(new CustomMobEffect(MobEffects.GLOWING.getDelegate(), 2, 1));
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = ModHelpers.Random.nextInt(0,10);
            EffectParticles.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.MYSTIC.get(), SoundEvents.SOUL_ESCAPE.value());
        }
        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        var initialHealth = livingEntity.getAttributes().getValue(Attributes.MAX_HEALTH);
        var initialScale = livingEntity.getAttributes().getValue(Attributes.SCALE);
        this.addAttributeModifier(Attributes.SCALE, att, -(initialScale/4), AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.MAX_HEALTH, att, -(initialHealth/4), AttributeModifier.Operation.ADD_VALUE);
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
