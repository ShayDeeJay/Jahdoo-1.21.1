package org.jahdoo.trial_nexus.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.common.registers.DamageTypeReg.JAHDOO_SOURCE;
import static org.jahdoo.trial_nexus.utils.Helpers.attributeModifierCalculator;

public class DamageUtils {

    public static void damageWithJahdoo(Entity target, Entity cause, double damage, ResourceKey<DamageType> key){
        target.hurt(DamageUtils.source(target.level(), key, target, cause), (float) damage);
    }

    public static void damageWithJahdoo(Entity target, double damage, ResourceKey<DamageType> key){
        target.hurt(DamageUtils.source(target.level(), key, target, null), (float) damage);
    }

    public static void damageEntityWithModifiers(LivingEntity target, LivingEntity player, float currentDamage, Holder<Attribute> ... attributes){
        target.hurt(DamageUtils.source(target.level(), JAHDOO_SOURCE, target, player), attributeModifierCalculator(player, currentDamage, true, attributes));
    }

    static public DamageSource source(LevelAccessor level, ResourceKey<DamageType> key, @Nullable Entity direct, @Nullable Entity causing) {
        Holder.Reference<DamageType> type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
        if (direct != null && causing != null) return new DamageSource(type, direct, causing);
        if (direct != null) return new DamageSource(type, direct);
        return new DamageSource(type);
    }

}
