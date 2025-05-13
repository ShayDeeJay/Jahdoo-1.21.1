package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BlockTags.*;
import static net.minecraft.tags.BlockTags.SNOW;
import static net.minecraft.world.level.block.Blocks.*;
import static net.minecraft.world.level.block.Blocks.AIR;
import static net.minecraft.world.level.block.Blocks.SAND;
import static org.jahdoo.trial_nexus.utils.ModTags.Block.*;
import static org.jahdoo.common.registers.BlockReg.*;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> lookupProvider,
        ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, JahdooMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(ALLOWED_BLOCK_INTERACTIONS)
            .add(WAND_MANAGER_TABLE.value())
            .add(RUNE_TABLE.value())
            .add(CREATOR_BLOCK.value())
            .add(DISSEMBLER.value())
            .add(CHALLENGE_ALTAR.value())
            .add(PERK_TABLE.value())
            .add(SHOPPING_TABLE.value())
            .add(LOOT_CHEST.value())
            .add(LOCK.value())
            .add(POWER_UP_STATION.value());

        this.tag(POWER_UP_SUPPORT)
            .add(GRASS_BLOCK)
            .add(Blocks.STONE_BRICKS)
            .add(MOSSY_STONE_BRICKS)
            .add(CRIMSON_NYLIUM)
            .add(SAND)
            .add(SANDSTONE);

        this.tag(RARE_BLOCKS)
            .add(BEACON)
            .add(EMERALD_BLOCK)
            .add(NETHERITE_BLOCK)
            .add(DIAMOND_BLOCK);

        this.tag(RARE_ORE)
            .add(NETHER_QUARTZ_ORE)
            .add(ANCIENT_DEBRIS)
            .addTag(GOLD_ORES)
            .addTag(DIAMOND_ORES)
            .addTag(EMERALD_ORES);

        this.tag(COMMON_ORE)
            .add(NEXITE_DEEPSLATE_ORE.get())
            .add(NEXITE_ORE.get())
            .addTag(IRON_ORES)
            .addTag(LAPIS_ORES)
            .addTag(COPPER_ORES)
            .addTag(REDSTONE_ORES)
            .addTag(COAL_ORES);

        this.tag(CAN_REPLACE_BLOCK)
            .add(AIR)
            .add(SHORT_GRASS)
            .add(TALL_GRASS)
            .addTag(FLOWERS)
            .addTag(SNOW)
            .addTag(CROPS)
            .addTag(LEAVES)
            .addTag(REPLACEABLE);

        this.tag(MINEABLE_WITH_PICKAXE)
            .add(LOOT_CRATE.value())
            .add(TANK.get())
            .add(CHALLENGE_ALTAR.get())
            .add(NEXITE_ORE.get())
            .add(NEXITE_DEEPSLATE_ORE.get())
            .add(NEXITE_BLOCK.get())
            .add(RAW_NEXITE_BLOCK.get());

        this.tag(GARBAGE_BLOCKS)
            .add(COBBLESTONE)
            .add(COBBLED_DEEPSLATE)
            .add(STONE)
            .add(ANDESITE)
            .add(TUFF)
            .add(DEEPSLATE)
            .add(DIORITE)
            .add(ANDESITE)
            .add(SAND)
            .add(GRAVEL)
            .add(BASALT)
            .add(BLACKSTONE)
            .add(CALCITE)
            .add(MOSS_BLOCK)
            .add(DRIPSTONE_BLOCK)
            .add(NETHERRACK)
            .add(GRANITE);
    }
}
