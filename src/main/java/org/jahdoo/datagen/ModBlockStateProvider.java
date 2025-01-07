package org.jahdoo.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.JahdooMod;
import org.jahdoo.registers.BlocksRegister;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, JahdooMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(BlocksRegister.NEXITE_ORE);
        blockWithItem(BlocksRegister.NEXITE_DEEPSLATE_ORE);
        blockWithItem(BlocksRegister.NEXITE_BLOCK);
        blockWithItem(BlocksRegister.RAW_NEXITE_BLOCK);
        blockWithItem(BlocksRegister.ENCHANTED_BLOCK);

        simpleBlockWithItem(BlocksRegister.WAND_MANAGER_TABLE.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/wand_manager_table")));

        simpleBlockWithItem(BlocksRegister.TANK.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/tank")));

        simpleBlockWithItem(BlocksRegister.INFUSER.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/infuser")));

        simpleBlockWithItem(BlocksRegister.CHALLENGE_ALTAR.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/challenge_altar")));

        simpleBlockWithItem(BlocksRegister.LOOT_CHEST.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/loot_chest")));

        simpleBlockWithItem(BlocksRegister.MODULAR_CHAOS_CUBE.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/modular_chaos_cube")));

        simpleBlockWithItem(BlocksRegister.LIGHTING.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/lighting")));

        simpleBlockWithItem(BlocksRegister.NEXITE_POWDER_BLOCK.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/nexite_powder_block")));

        simpleBlock(BlocksRegister.WAND.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/wand_mystic")));

        simpleBlockWithItem(BlocksRegister.CREATOR.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/creator")));

        simpleBlockWithItem(BlocksRegister.AUGMENT_MODIFICATION_STATION.get(),
            new ModelFile.UncheckedModelFile(modLoc("block/augment_modification_station")));
    }

    private void blockWithItem(DeferredHolder<Block, Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

}
