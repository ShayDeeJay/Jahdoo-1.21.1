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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
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
        this.dropSelf(BlocksRegister.CREATOR.get());
        this.dropOther(BlocksRegister.WAND.get(), Items.AIR);
        this.dropOther(BlocksRegister.LIGHTING.get(), Items.AIR);
        this.dropOther(BlocksRegister.JIDE_POWDER_BLOCk.get(), Items.AIR);
        this.add(BlocksRegister.CRYSTAL_ORE.get(),
            block -> createCopperLikeOreDrops(BlocksRegister.CRYSTAL_ORE.get(), ItemsRegister.JIDE_POWDER.get())
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0f, 1.0F)))
        );
    }


    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
            this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
            )
        );
    }

}
