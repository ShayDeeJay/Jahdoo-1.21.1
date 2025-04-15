package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.datagen.loot.AddItemModifier;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.storage.loot.BuiltInLootTables.all;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifiersProvider(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries, JahdooMod.MOD_ID);
    }

    @Override
    protected void start() {
        var chestLoot = all().stream().filter(it -> it.location().getPath().intern().contains("chest")).toList();
        chestLoot.forEach(entries -> commonLootTables(entries.location(), chestLoot.indexOf(entries)));
    }

    public static AddItemModifier addLoot(ResourceLocation resourceLocation, Item item, float chance){

        return new AddItemModifier(
            new LootItemCondition[] {
                LootItemRandomChanceCondition.randomChance(chance).build(),
                new LootTableIdCondition.Builder(resourceLocation).build(),
            },
            item
        );

    }

    private void commonLootTables(
        ResourceLocation resourceLocation,
        int additional
    ) {
//        add("augments_chest" + additional, addLoot(resourceLocation, ItemReg.AUGMENT.get(), 0.2f));
//        add("augments_core_chest" + additional, addLoot(resourceLocation, ItemReg.AUGMENT_CORE.get(), 0.35f));
    }

}
