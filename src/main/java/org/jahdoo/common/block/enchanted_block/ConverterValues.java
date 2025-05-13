package org.jahdoo.common.block.enchanted_block;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.trial_nexus.utils.ModTags;

public enum ConverterValues {

    GARBAGE("garbage", ModTags.Block.GARBAGE_BLOCKS, 300, 3),
    LEAVES("leaves", BlockTags.LEAVES, 200, 3),
    LOGS("logs", BlockTags.LOGS, 140, 2),
    COMMON_ORE("common_ore", ModTags.Block.COMMON_ORE, 80, 2),
    RARE_ORES("rare_ore", ModTags.Block.RARE_ORE, 40, 1),
    OPULENT("opulent", ModTags.Block.RARE_BLOCKS, 10, 0);;

    private final String name;
    private final TagKey<Block> type;
    private final int progressChance;
    private final int fade;

    ConverterValues(String name, TagKey<Block> type, int progressChance, int fade) {
        this.name = name;
        this.type = type;
        this.fade = fade;
        this.progressChance = progressChance;
    }

    public int getFade() {
        return fade;
    }

    public int getProgressChance() {
        return progressChance;
    }

    public String getName() {
        return name;
    }

    public TagKey<Block> getType() {
        return type;
    }

    public static int setBlockType(Block block){
        for (ConverterValues types : ConverterValues.values()){
            if(block.defaultBlockState().is(types.getType())) return types.getProgressChance();
        }
        return 0;
    }

    public static int setSpreadChance(Block block){
        for (ConverterValues types : ConverterValues.values()){
            if(block.defaultBlockState().is(types.getType())) return types.getFade();
        }
        return 0;
    }

    public static boolean isMatching(BlockState comparison, BlockState current) {
        for (ConverterValues types : ConverterValues.values()){
            if(comparison.is(types.getType()) && current.is(types.getType())) return true;
        }
        return false;
    }

    public static boolean isConvertibleBlock(Block block){
        for (ConverterValues types : ConverterValues.values()){
            if(block.defaultBlockState().is(types.getType())) return true;
        }
        return false;
    }

};
