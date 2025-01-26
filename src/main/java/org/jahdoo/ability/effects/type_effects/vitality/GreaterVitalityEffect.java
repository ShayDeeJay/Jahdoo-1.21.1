package org.jahdoo.ability.effects.type_effects.vitality;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.effects.EffectHelpers;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.ability.effects.EffectHelpers.getGetRandomChance;
import static org.jahdoo.items.augments.AugmentItemHelper.throwNewItem;

public class GreaterVitalityEffect extends MobEffect {


    public GreaterVitalityEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(226, 51, 119));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int amplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            EffectHelpers.GreaterGlowSync(targetEntity, amplifier, serverLevel, EffectsRegister.VITALITY_EFFECT);
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
