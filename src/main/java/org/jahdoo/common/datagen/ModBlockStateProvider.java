package org.jahdoo.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.JahdooMod;

import static net.neoforged.neoforge.client.model.generators.ModelFile.*;
import static org.jahdoo.common.registers.BlockReg.*;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(
        PackOutput output,
        ExistingFileHelper fileHelper
    ) {
        super(output, JahdooMod.MOD_ID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(NEXITE_ORE);
        blockWithItem(NEXITE_DEEPSLATE_ORE);
        blockWithItem(NEXITE_BLOCK);
        blockWithItem(RAW_NEXITE_BLOCK);
        blockWithItem(ENCHANTED_BLOCK);

        simpleBlockWithItem(
            WAND_MANAGER_TABLE.get(),
            new UncheckedModelFile(modLoc("block/wand_manager_table"))
        );

        simpleBlockWithItem(
            TANK.get(),
            new UncheckedModelFile(modLoc("block/tank"))
        );

        simpleBlockWithItem(
            LOCK.get(),
            new UncheckedModelFile(modLoc("block/lock"))
        );

        simpleBlockWithItem(
            SHOPPING_TABLE.get(),
            new UncheckedModelFile(modLoc("block/shopping_table"))
        );

        simpleBlockWithItem(
            RUNE_TABLE.get(),
            new UncheckedModelFile(modLoc("block/rune_table"))
        );

        simpleBlockWithItem(
            INFUSER.get(),
            new UncheckedModelFile(modLoc("block/infuser"))
        );

        simpleBlockWithItem(
            CHALLENGE_ALTAR.get(),
            new UncheckedModelFile(modLoc("block/challenge_altar"))
        );

        simpleBlockWithItem(
            LOOT_CHEST.get(),
            new UncheckedModelFile(modLoc("block/loot_chest"))
        );

        simpleBlockWithItem(
            MODULAR_CHAOS_CUBE.get(),
            new UncheckedModelFile(modLoc("block/modular_chaos_cube"))
        );

        simpleBlockWithItem(
            LIGHTING.get(),
            new UncheckedModelFile(modLoc("block/lighting"))
        );

        simpleBlockWithItem(
            PERK_TABLE.get(),
            new UncheckedModelFile(modLoc("block/perk_table"))
        );

        simpleBlockWithItem(
            NEXITE_POWDER_BLOCK.get(),
            new UncheckedModelFile(modLoc("block/nexite_powder_block"))
        );

    }

    private void blockWithItem(DeferredHolder<Block, Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

}
