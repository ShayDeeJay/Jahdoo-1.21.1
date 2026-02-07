package org.jahdoo.common.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static org.jahdoo.common.datagen.DamageTypesProvider.*;

public class DamageTypeReg {

    public static final ResourceKey<DamageType> JAHDOO_SOURCE =
        ResourceKey.create(Registries.DAMAGE_TYPE, JahdooHelpers.res(JAHDOO_DAMAGE));

    public static final ResourceKey<DamageType> INFERNO_SOURCE =
        ResourceKey.create(Registries.DAMAGE_TYPE, JahdooHelpers.res(INFERNO_DAMAGE));

    public static final ResourceKey<DamageType> FROST_SOURCE =
        ResourceKey.create(Registries.DAMAGE_TYPE, JahdooHelpers.res(FROST_DAMAGE));

    public static final ResourceKey<DamageType> VITALITY_SOURCE =
        ResourceKey.create(Registries.DAMAGE_TYPE, JahdooHelpers.res(VITALITY_DAMAGE));

    public static final ResourceKey<DamageType> MYSTIC_SOURCE =
        ResourceKey.create(Registries.DAMAGE_TYPE, JahdooHelpers.res(MYSTIC_DAMAGE));

    public static final ResourceKey<Biome> BIOME_SOURCE =
        ResourceKey.create(Registries.BIOME, JahdooHelpers.res("biome_test"));

}
