package org.jahdoo.datagen;

import com.sun.jna.platform.win32.WinNT;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import org.jahdoo.JahdooMod;
import org.jahdoo.loot.AddItemModifier;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, JahdooMod.MOD_ID);
    }

    @Override
    protected void start() {
        this.lootTableIdList.forEach(entries -> lootTableIdCondition(entries, lootTableIdList.indexOf(entries)));
    }

    List<String> lootTableIdList = List.of(
        "ancient_city",
        "abandoned_mineshaft",
        "ancient_city_ice_box",
        "bastion_hoglin_stable",
        "bastion_treasure",
        "buried_treasure",
        "desert_pyramid",
        "end_city_treasure",
        "jungle_temple",
        "pillager_outpost",
        "shipwreck_supply",
        "shipwreck_treasure",
        "simple_dungeon",
        "stronghold_corridor",
        "stronghold_crossing",
        "stronghold_library",
        "woodland_mansion",
        "village_armorer",
        "village_butcher",
        "village_cartographer",
        "village_desert_house",
        "village_fisher",
        "village_fletcher",
        "village_mason",
        "village_plains_house",
        "village_savanna_house",
        "village_snowy_house",
        "village_taiga_house",
        "village_tannery",
        "village_toolsmith",
        "village_weaponsmith"
    );

    private void lootTableIdCondition(String location, int additional) {
        add("augments_chest" + additional,
            new AddItemModifier(
                new LootItemCondition[] {
                    LootItemRandomChanceCondition.randomChance(0.5f).build(),
                    new LootTableIdCondition.Builder(ResourceLocation.parse("chests/"+location)).build(),
                },
                ItemsRegister.AUGMENT_ITEM.get()
            )
        );

        add("augments_core_chest" + additional,
            new AddItemModifier(
                new LootItemCondition[] {
                    LootItemRandomChanceCondition.randomChance(0.5f).build(),
                    new LootTableIdCondition.Builder(ResourceLocation.parse("chests/"+location)).build(),
                },
                ItemsRegister.AUGMENT_CORE.get()
            )
        );

        add("wand_chest" + additional,
            new AddItemModifier(
                new LootItemCondition[] {
                    LootItemRandomChanceCondition.randomChance(0.5f).build(),
                    new LootTableIdCondition.Builder(ResourceLocation.parse("chests/"+location)).build(),
                },
                ItemsRegister.WAND_ITEM_MYSTIC.get()
            )
        );

        add("tome_chest" + additional,
            new AddItemModifier(
                new LootItemCondition[] {
                    LootItemRandomChanceCondition.randomChance(0.5f).build(),
                    new LootTableIdCondition.Builder(ResourceLocation.parse("chests/"+location)).build(),
                },
                ItemsRegister.TOME_OF_UNITY.get()
            )
        );
    }
}
