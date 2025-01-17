package org.jahdoo.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;

import java.util.Set;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootSubProvider {
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlocksRegister.BLOCKS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toList());
    }

    public ModBlockLootTables(HolderLookup.Provider pRegistries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), pRegistries);
    }

    @Override
    protected void generate() {
        this.dropSelf(BlocksRegister.WAND_MANAGER_TABLE.get());
        this.dropSelf(BlocksRegister.TANK.value());
        this.dropSelf(BlocksRegister.INFUSER.get());
        this.dropSelf(BlocksRegister.CHALLENGE_ALTAR.get());
        this.dropSelf(BlocksRegister.LOOT_CHEST.get());
        this.dropSelf(BlocksRegister.MODULAR_CHAOS_CUBE.get());
        this.dropSelf(BlocksRegister.CREATOR.get());
        this.dropSelf(BlocksRegister.AUGMENT_MODIFICATION_STATION.get());
        this.dropSelf(BlocksRegister.SHOPPING_TABLE.get());
        this.dropOther(BlocksRegister.WAND.get(), Items.AIR);
        this.dropOther(BlocksRegister.LIGHTING.get(), Items.AIR);
        this.dropOther(BlocksRegister.ENCHANTED_BLOCK.get(), Items.AIR);
        this.dropOther(BlocksRegister.NEXITE_POWDER_BLOCK.get(), Items.AIR);
        this.dropOther(BlocksRegister.TRAIL_PORTAL.get(), Items.AIR);
        this.add(BlocksRegister.NEXITE_ORE.get(),
            block -> createCopperLikeOreDrops(BlocksRegister.NEXITE_ORE.get(), ItemsRegister.NEXITE_POWDER.get())
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0f, 1.0F)))
        );
        this.add(BlocksRegister.NEXITE_DEEPSLATE_ORE.get(),
            block -> createCopperLikeOreDrops(BlocksRegister.NEXITE_ORE.get(), ItemsRegister.NEXITE_POWDER.get())
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0f, 1.0F)))
        );
        this.dropSelf(BlocksRegister.NEXITE_BLOCK.get());
        this.dropSelf(BlocksRegister.RAW_NEXITE_BLOCK.get());
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
            this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
            )
        );
    }
}
