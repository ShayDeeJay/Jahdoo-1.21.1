package org.jahdoo.common.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.utils.Helpers;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.jahdoo.common.registers.DamageTypeReg.BIOME_SOURCE;
import static org.jahdoo.common.registers.DamageTypeReg.JAHDOO_SOURCE;

public class BiomeProvider extends DatapackBuiltinEntriesProvider {

    public static final String JAHDOO_DAMAGE = "jahdoo_magic";

    private static final RegistrySetBuilder BUILDER =
        new RegistrySetBuilder().add(Registries.BIOME, BiomeProvider::bootstrap);

    public static void bootstrap(BootstrapContext<Biome> ctx) {
        ctx.register(BIOME_SOURCE, Helpers.newBiome());
    }

    public BiomeProvider(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries, BUILDER, Set.of(JahdooMod.MOD_ID));
    }

    @Override
    public String getName() {
        return "Biome type";
    }
}
