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
            .add(BlocksRegister.CHALLENGE_ALTAR.get())
            .add(BlocksRegister.NEXITE_ORE.get())
            .add(BlocksRegister.NEXITE_DEEPSLATE_ORE.get())
            .add(BlocksRegister.NEXITE_BLOCK.get())
            .add(BlocksRegister.RAW_NEXITE_BLOCK.get())
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

        this.tag(ModTags.Block.RARE_ORE)
            .add(Blocks.NETHER_QUARTZ_ORE)
            .add(Blocks.ANCIENT_DEBRIS)
            .addTag(BlockTags.GOLD_ORES)
            .addTag(BlockTags.DIAMOND_ORES)
            .addTag(BlockTags.EMERALD_ORES);

        this.tag(ModTags.Block.COMMON_ORE)
            .add(BlocksRegister.NEXITE_DEEPSLATE_ORE.get())
            .add(BlocksRegister.NEXITE_ORE.get())
            .addTag(BlockTags.IRON_ORES)
            .addTag(BlockTags.LAPIS_ORES)
            .addTag(BlockTags.COPPER_ORES)
            .addTag(BlockTags.REDSTONE_ORES)
            .addTag(BlockTags.COAL_ORES);

        this.tag(ModTags.Block.RARE_BLOCKS)
            .add(Blocks.BEACON)
            .add(Blocks.EMERALD_BLOCK)
            .add(Blocks.NETHERITE_BLOCK)
            .add(Blocks.DIAMOND_BLOCK);

        this.tag(ModTags.Block.GARBAGE_BLOCKS)
            .add(Blocks.COBBLESTONE)
            .add(Blocks.COBBLED_DEEPSLATE)
            .add(Blocks.STONE)
            .add(Blocks.ANDESITE)
            .add(Blocks.TUFF)
            .add(Blocks.DEEPSLATE)
            .add(Blocks.DIORITE)
            .add(Blocks.ANDESITE)
            .add(Blocks.SAND)
            .add(Blocks.GRAVEL)
            .add(Blocks.BASALT)
            .add(Blocks.BLACKSTONE)
            .add(Blocks.CALCITE)
            .add(Blocks.MOSS_BLOCK)
            .add(Blocks.DRIPSTONE_BLOCK)
            .add(Blocks.NETHERRACK)
            .add(Blocks.GRANITE);
    }
}
