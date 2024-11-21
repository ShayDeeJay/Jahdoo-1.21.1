package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import org.jahdoo.utils.ModHelpers;

public class DamageTypeRegistry {
    public static final ResourceKey<DamageType> MYSTIC_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ModHelpers.res( "mystic"));
}
