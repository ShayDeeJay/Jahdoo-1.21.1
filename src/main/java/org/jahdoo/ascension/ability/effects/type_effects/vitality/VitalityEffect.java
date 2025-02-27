package org.jahdoo.ascension.ability.effects.type_effects.vitality;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.effects.EffectHelpers;
import org.jahdoo.common.registers.DataComponentRegistry;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.entity.ai.behavior.BehaviorUtils.throwItem;
import static org.jahdoo.ascension.ability.effects.EffectHelpers.getGetRandomChance;
import static org.jahdoo.common.items.augments.AugmentItemHelper.throwNewItem;

public class VitalityEffect extends MobEffect {


    public VitalityEffect() {
        super(MobEffectCategory.HARMFUL, FastColor.ARGB32.color(226, 51, 119));
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int amplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = getGetRandomChance(amplifier);
            if(getRandomChance == 0) {
                if(Helpers.Random.nextInt(0,(20-amplifier)) == 0){
                    throwHeartContainer(targetEntity, (float) (0.1 * amplifier));
                }
                DamageUtils.damageWithJahdoo(targetEntity, 1);
            }
            EffectHelpers.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.vitality(), SoundEvents.SOUL_ESCAPE.value());
        }
        return true;
    }

    public static void throwHeartContainer(LivingEntity targetEntity, float healAmount) {
        var heartContainer = createHearContainer(healAmount);
        throwNewItem(targetEntity, heartContainer);
    }

    private static @NotNull ItemStack createHearContainer(float amplifier) {
        var heartContainer = new ItemStack(ItemsRegister.HEALTH_CONTAINER.get());
        heartContainer.set(DataComponentRegistry.HEART_CONTAINER.get(), amplifier);
        return heartContainer;
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
