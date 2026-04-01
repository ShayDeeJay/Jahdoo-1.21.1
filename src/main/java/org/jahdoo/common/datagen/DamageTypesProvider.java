package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.jahdoo.JahdooMod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.jahdoo.common.registers.DamageTypeReg.*;

public class DamageTypesProvider extends DatapackBuiltinEntriesProvider {

    public static final String JAHDOO_DAMAGE = "jahdoo_magic";
    public static final String INFERNO_DAMAGE = "inferno_magic";
    public static final String MYSTIC_DAMAGE = "mystic_magic";
    public static final String VITALITY_DAMAGE = "vitality_magic";
    public static final String FROST_DAMAGE = "frost_magic";

    public DamageTypesProvider(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries, BUILDER, Set.of(JahdooMod.MOD_ID));
    }

    private static final RegistrySetBuilder BUILDER =
        new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, DamageTypesProvider::bootstrap);

    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        ctx.register(JAHDOO_SOURCE, new DamageType(JAHDOO_DAMAGE, 0.1F));
        ctx.register(INFERNO_SOURCE, new DamageType(INFERNO_DAMAGE, 0.1F));
        ctx.register(MYSTIC_SOURCE, new DamageType(MYSTIC_DAMAGE, 0.1F));
        ctx.register(VITALITY_SOURCE, new DamageType(VITALITY_DAMAGE, 0.1F));
        ctx.register(FROST_SOURCE, new DamageType(FROST_DAMAGE, 0.1F));
    }

    @Override
    public String getName() {
        return "Jahdoo Damage Type";
    }
}
