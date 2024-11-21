package org.jahdoo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, JahdooMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(BlocksRegister.TANK.get())
            .add(BlocksRegister.INFUSER.get())
            .add(BlocksRegister.CRYSTAL_ORE.get())
            .add(BlocksRegister.CRYSTAL_DEEPSLATE_ORE.get())
            .add(BlocksRegister.WAND_MANAGER_TABLE.get())
            .add(BlocksRegister.CREATOR.get());

        this.tag(ModTags.Block.ALLOWED_BLOCK_INTERACTIONS)
            .add(BlocksRegister.CREATOR.get())
            .add(BlocksRegister.WAND_MANAGER_TABLE.get());

        this.tag(ModTags.Block.CAN_REPLACE_BLOCK)
            .add(Blocks.AIR)
            .add(Blocks.SHORT_GRASS)
            .add(Blocks.TALL_GRASS)
            .addTag(BlockTags.FLOWERS)
            .addTag(BlockTags.SNOW)
            .addTag(BlockTags.CROPS)
            .addTag(BlockTags.LEAVES)
            .addTag(BlockTags.REPLACEABLE);

    }
}
