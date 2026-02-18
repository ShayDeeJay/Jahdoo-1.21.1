package org.jahdoo.common.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

import static java.util.stream.Collectors.*;
import static net.minecraft.world.flag.FeatureFlags.*;
import static net.minecraft.world.level.storage.loot.entries.LootItem.*;
import static net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.*;
import static net.minecraft.world.level.storage.loot.providers.number.UniformGenerator.*;
import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.common.registers.ItemReg.*;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables(HolderLookup.Provider pregistries) {
        super(Set.of(), REGISTRY.allFlags(), pregistries);
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block block, Item item) {
        return createSilkTouchDispatchTable(block,
            this.applyExplosionDecay(block, lootTableItem(item).apply(setCount(between(1.0F, 3.0F))))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BLOCKS.getEntries()
            .stream()
            .map(DeferredHolder::get)
            .collect(toList());
    }

    @Override
    protected void generate() {
        this.dropSelf(WAND_MANAGER_TABLE.get());
        this.dropSelf(TANK.value());
        this.dropSelf(TICKET_BUREAU.value());
        this.dropSelf(DISSEMBLER.get());
        this.dropSelf(CHALLENGE_ALTAR.get());
        this.dropSelf(LOOT_CHEST.get());
        this.dropSelf(MODULAR_CHAOS_CUBE.get());
        this.dropSelf(MYSTICAL_AUGMENTER.get());
        this.dropSelf(SHOPPING_TABLE.get());
        this.dropSelf(RUNE_TABLE.get());
        this.dropSelf(CREATOR_BLOCK.get());
        this.dropSelf(LOOT_CRATE.value());
        this.dropOther(LOOT_POT.value(), Items.AIR);
        this.dropOther(POWER_UP_STATION.get(), Items.AIR);
        this.dropOther(LIGHTING.get(), Items.AIR);
        this.dropOther(ENCHANTED_BLOCK.get(), Items.AIR);
        this.dropOther(NEXITE_POWDER_BLOCK.get(), Items.AIR);
        this.dropOther(TRAIL_PORTAL.get(), Items.AIR);
        this.dropOther(PERK_TABLE.get(), Items.AIR);
        this.dropOther(LOCK.get(), Items.AIR);
        this.dropOther(LOCK_SUPPORT.get(), Items.AIR);
        this.dropSelf(NEXITE_BLOCK.get());
        this.dropSelf(ENCHANTED_DIAMOND_BLOCK.get());
        this.dropSelf(ROSE_QUARTZ_BLOCK.get());
        this.dropSelf(PACKED_MUD_CLAY.get());
        this.dropSelf(RAW_NEXITE_BLOCK.get());

        addDrops(this, LISITE_ORE.get(), LISITE_SHARD.get());
        addDrops(this, ENCHANTED_DIAMOND_ORE.get(), ENCHANTED_DIAMOND.get());
        addDrops(this, ROSE_QUARTZ_ORE.get(), ROSE_QUARTZ.get());
        addDrops(this, NEXITE_DEEPSLATE_ORE.get(), NEXITE_POWDER.get());
        addDrops(this, NEXITE_ORE.get(), NEXITE_POWDER.get());
    }

    public static void addDrops(ModBlockLootTables tables, Block blocks, Item drop  ) {
        tables.add(
            blocks, block -> tables
                .createCopperLikeOreDrops(blocks, drop)
                .withPool(LootPool.lootPool().setRolls(between(0f, 1.0F)))
        );
    }

}
