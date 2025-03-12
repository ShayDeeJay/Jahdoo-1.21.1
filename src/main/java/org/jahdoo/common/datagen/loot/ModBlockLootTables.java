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
        this.dropSelf(INFUSER.get());
        this.dropSelf(CHALLENGE_ALTAR.get());
        this.dropSelf(LOOT_CHEST.get());
        this.dropSelf(MODULAR_CHAOS_CUBE.get());
        this.dropSelf(AUGMENT_MODIFICATION_STATION.get());
        this.dropSelf(SHOPPING_TABLE.get());
        this.dropSelf(RUNE_TABLE.get());
        this.dropOther(WAND.get(), Items.AIR);
        this.dropOther(LIGHTING.get(), Items.AIR);
        this.dropOther(ENCHANTED_BLOCK.get(), Items.AIR);
        this.dropOther(NEXITE_POWDER_BLOCK.get(), Items.AIR);
        this.dropOther(TRAIL_PORTAL.get(), Items.AIR);
        this.dropOther(PERK_TABLE.get(), Items.AIR);
        this.dropOther(LOCK.get(), Items.AIR);
        this.dropSelf(NEXITE_BLOCK.get());
        this.dropSelf(RAW_NEXITE_BLOCK.get());

        this.add(
            NEXITE_ORE.get(),
            block -> createCopperLikeOreDrops(NEXITE_ORE.get(), NEXITE_POWDER.get())
                .withPool(LootPool.lootPool().setRolls(between(0f, 1.0F)))
        );

        this.add(NEXITE_DEEPSLATE_ORE.get(),
            block -> createCopperLikeOreDrops(NEXITE_ORE.get(), NEXITE_POWDER.get())
                .withPool(LootPool.lootPool().setRolls(between(0f, 1.0F)))
        );

    }

}
