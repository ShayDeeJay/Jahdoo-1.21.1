package org.jahdoo.trial_nexus.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> WAND_TAGS = tag("wand_tag");
        public static final TagKey<Item> ESSENCE_FRAGMENT = tag("essence_fragment");
        public static final TagKey<Item> AUGMENT_CORE = tag("augment_core");


        private static TagKey<Item> tag(String name) {
            return ItemTags.create(JahdooHelpers.res(name));
        }
    }

    public static class Entities{
        public static final TagKey<EntityType<?>> IGNORE_ENTITY = create("ignore_entity_collision");
        public static final TagKey<EntityType<?>> HORDE_MOBS = create("horde_mobs");

        private static TagKey<EntityType<?>> create(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace(pName));
        }
    }

    public static class Block {
        
        public static final TagKey<net.minecraft.world.level.block.Block> ALLOWED_BLOCK_INTERACTIONS = tag("block_interactions");
        public static final TagKey<net.minecraft.world.level.block.Block> CAN_REPLACE_BLOCK = tag("replace");
        public static final TagKey<net.minecraft.world.level.block.Block> POWER_UP_SUPPORT = tag("power_up_support");
        public static final TagKey<net.minecraft.world.level.block.Block> MINEABLE_NEXUS = tag("nexus_mineable");
        public static final TagKey<net.minecraft.world.level.block.Block> CHEAP_BLOCK = tag("nexus_mineable");
        public static final TagKey<net.minecraft.world.level.block.Block> MID_RANGE_BLOCK = tag("nexus_mineable");
        public static final TagKey<net.minecraft.world.level.block.Block> VALUABLE_BLOCK = tag("nexus_mineable");

        //Enchanted Block
        public static final TagKey<net.minecraft.world.level.block.Block> GARBAGE_BLOCKS = tag("garbage_blocks");
        public static final TagKey<net.minecraft.world.level.block.Block> RARE_ORE = tag("rare_ore");
        public static final TagKey<net.minecraft.world.level.block.Block> COMMON_ORE = tag("common_ore");
        public static final TagKey<net.minecraft.world.level.block.Block> RARE_BLOCKS = tag("rare_block");
        public static final TagKey<net.minecraft.world.level.block.Block> OPULENT_BLOCKS = tag("opulent_block");

        private static TagKey<net.minecraft.world.level.block.Block> tag(String name) {
            return BlockTags.create(JahdooHelpers.res(name));
        }
        
    }
    
}