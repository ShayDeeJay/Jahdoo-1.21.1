package org.jahdoo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import org.jahdoo.JahdooMod;
import org.jahdoo.loot.AddItemModifier;
import org.jahdoo.registers.ItemsRegister;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.storage.loot.BuiltInLootTables.*;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, JahdooMod.MOD_ID);
    }

    @Override
    protected void start() {
        var chestLoot = BuiltInLootTables.all().stream().filter(it -> it.location().getPath().intern().contains("chest")).toList();
        chestLoot.forEach(entries -> commonLootTables(entries.location(), chestLoot.indexOf(entries)));
        rareLootTableLocations.forEach(entries -> rareLootTales(entries.location(), rareLootTableLocations.indexOf(entries)));
    }

    public static final List<ResourceKey<LootTable>> rareLootTableLocations = List.of(
        END_CITY_TREASURE,
        VILLAGE_WEAPONSMITH,
        VILLAGE_TOOLSMITH,
        STRONGHOLD_LIBRARY,
        DESERT_PYRAMID,
        JUNGLE_TEMPLE,
        WOODLAND_MANSION,
        IGLOO_CHEST,
        SHIPWRECK_TREASURE,
        BASTION_TREASURE,
        ANCIENT_CITY,
        ANCIENT_CITY_ICE_BOX,
        RUINED_PORTAL
    );

    public static AddItemModifier addLoot(ResourceLocation resourceLocation, Item item, float chance){
        return new AddItemModifier(
            new LootItemCondition[] {
                LootItemRandomChanceCondition.randomChance(chance).build(),
                new LootTableIdCondition.Builder(resourceLocation).build(),
            },
            item
        );
    }

    private void commonLootTables(ResourceLocation resourceLocation, int additional) {
        add("augments_chest" + additional, addLoot(resourceLocation, ItemsRegister.AUGMENT_ITEM.get(), 0.5f));
        add("augments_core_chest" + additional, addLoot(resourceLocation, ItemsRegister.AUGMENT_CORE.get(), 0.5f));
    }

    private void rareLootTales(ResourceLocation resourceLocation, int additional) {
        add("wand_chest" + additional, addLoot(resourceLocation, ItemsRegister.WAND_ITEM_MYSTIC.get(), 0.2f));
        add("tome_chest" + additional, addLoot(resourceLocation, ItemsRegister.TOME_OF_UNITY.get(), 0.3f));
    }
}
