package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jahdoo.common.datagen.loot.ModBlockLootTables;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.loot.LootTableProvider.*;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.*;

public class ModLootTableProvider {

    public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new LootTableProvider(
            output,
            Set.of(),
            List.of(new SubProviderEntry(ModBlockLootTables::new, BLOCK)),
            registries
        );
    }

}
