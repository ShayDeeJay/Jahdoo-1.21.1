package org.jahdoo.ascension;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.utils.Maths;

public class LevelStageModifiers {

    public static void attributeWithChance(
        Holder<Attribute> attributes,
        LivingEntity getEntity,
        int multiplier,
        int chance
    ){
        if(Maths.percentageChance(chance) && getEntity.getAttributes().hasAttribute(attributes)){
            var attributeInstance = getEntity.getAttributes().getInstance(attributes);
            if (attributeInstance == null) return;
            attributeInstance.setBaseValue(Maths.getPercentageTotal(multiplier, attributeInstance.getValue()));
        }
    }

    public static void addBaseAttribute(
        Holder<Attribute> attributes,
        LivingEntity getEntity,
        int multiplier
    ){
        if(getEntity.getAttributes().hasAttribute(attributes)){
            var attributeInstance = getEntity.getAttributes().getInstance(attributes);
            if (attributeInstance == null) return;
            attributeInstance.setBaseValue(Maths.getPercentageTotal(multiplier, attributeInstance.getValue()));
        }
    }

    public static void effectWithChance(LivingEntity livingEntity, Holder<MobEffect> effect, int amplifier, int chance) {
        if(Maths.percentageChance(chance)){
            if(!livingEntity.hasEffect(effect)){
                livingEntity.addEffect(new JahdooMobEffect(effect, MobEffectInstance.INFINITE_DURATION, amplifier));
            }
        }
    }

}
