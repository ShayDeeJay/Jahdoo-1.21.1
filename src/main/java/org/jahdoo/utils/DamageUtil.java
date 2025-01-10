package org.jahdoo.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.LevelAccessor;
import org.jahdoo.ability.AbstractElement;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.registers.DamageTypeRegistry.JAHDOO_SOURCE;
import static org.jahdoo.utils.ModHelpers.attributeModifierCalculator;

public class DamageUtil {
    static public DamageSource source(LevelAccessor level, ResourceKey<DamageType> key, @Nullable Entity direct, @Nullable Entity causing) {
        Holder.Reference<DamageType> type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
        if (direct != null && causing != null) return new DamageSource(type, direct, causing);
        if (direct != null) return new DamageSource(type, direct);
        return new DamageSource(type);
    }

    public static void damageWithJahdoo(Entity target, Entity cause, double damage){
        target.hurt(DamageUtil.source(target.level(), JAHDOO_SOURCE, target, cause), (float) damage);
    }

    public static void damageWithJahdoo(Entity target, double damage){
        target.hurt(DamageUtil.source(target.level(), JAHDOO_SOURCE, target, null), (float) damage);
    }

    @SafeVarargs
    public static void damageEntityWithModifiers(LivingEntity target, LivingEntity player, float currentDamage, Holder<Attribute> ... attributes){
        target.hurt(DamageUtil.source(target.level(), JAHDOO_SOURCE, target, player), attributeModifierCalculator(player, currentDamage, true, attributes));
    }
}
