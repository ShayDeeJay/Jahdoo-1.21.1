package org.jahdoo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.jahdoo.JahdooMod;
import org.jahdoo.registers.DamageTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DamageTypesProvider extends DatapackBuiltinEntriesProvider {
    public static final String JAHDOO_DAMAGE = "jahdoo_magic";
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
        .add(Registries.DAMAGE_TYPE, DamageTypesProvider::bootstrap);


    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        ctx.register(DamageTypeRegistry.JAHDOO_SOURCE, new DamageType(JAHDOO_DAMAGE, 0.1F));
    }

    public DamageTypesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(JahdooMod.MOD_ID));
    }

    @Override
    public @NotNull String getName() {
        return "Jahdoo Damage Type";
    }
}
