package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.datagen.DamageTypesProvider.JAHDOO_DAMAGE;

public class DamageTypeRegistry {
    public static final ResourceKey<DamageType> JAHDOO_SOURCE = ResourceKey.create(Registries.DAMAGE_TYPE, ModHelpers.res(JAHDOO_DAMAGE));
}
