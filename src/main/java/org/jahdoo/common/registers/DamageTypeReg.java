package org.jahdoo.common.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.common.datagen.DamageTypesProvider.JAHDOO_DAMAGE;

public class DamageTypeReg {
    public static final ResourceKey<DamageType> JAHDOO_SOURCE =
        ResourceKey.create(Registries.DAMAGE_TYPE, Helpers.res(JAHDOO_DAMAGE));
}
