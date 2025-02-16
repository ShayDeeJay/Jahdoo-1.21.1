package org.jahdoo.ability.effects.type_effects.vitality;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.effects.EffectHelpers;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.entity.ai.behavior.BehaviorUtils.throwItem;
import static org.jahdoo.ability.effects.EffectHelpers.getGetRandomChance;
import static org.jahdoo.items.augments.AugmentItemHelper.throwNewItem;

public class VitalityEffect extends MobEffect {


    public VitalityEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int amplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = getGetRandomChance(amplifier);
            if(getRandomChance == 0) {
                if(ModHelpers.Random.nextInt(0,(20-amplifier)) == 0){
                    var heartContainer = createHearContainer(amplifier);
                    throwNewItem(targetEntity, heartContainer);
                }
                DamageUtil.damageWithJahdoo(targetEntity, 1);
            }
            EffectHelpers.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.VITALITY.get(), SoundEvents.SOUL_ESCAPE.value());
        }
        return true;
    }

    private static @NotNull ItemStack createHearContainer(int amplifier) {
        var heartContainer = new ItemStack(ItemsRegister.HEALTH_CONTAINER.get());
        heartContainer.set(DataComponentRegistry.HEART_CONTAINER.get(), (float) (0.1 * amplifier));
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
