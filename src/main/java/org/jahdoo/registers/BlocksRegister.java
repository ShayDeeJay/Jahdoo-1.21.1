package org.jahdoo.registers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.block.TrialPortalBlock;
import org.jahdoo.block.augment_modification_station.AugmentModificationBlock;
import org.jahdoo.block.challange_altar.ChallengeAltarBlock;
import org.jahdoo.block.enchanted_block.EnchantedBlock;
import org.jahdoo.block.loot_chest.LootChestBlock;
import org.jahdoo.block.loot_chest.LootChestEntity;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeBlock;
import org.jahdoo.block.crafter.CreatorBlock;
import org.jahdoo.block.infuser.InfuserBlock;
import org.jahdoo.block.light_block.LightBlock;
import org.jahdoo.block.tank.NexiteTankBlock;
import org.jahdoo.block.wand.WandBlock;
import org.jahdoo.block.wand_block_manager.WandManagerBlock;

import java.util.function.Supplier;

public class BlocksRegister {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, JahdooMod.MOD_ID);

    public static DeferredHolder<Block, Block> WAND_MANAGER_TABLE = registerBlock("wand_manager_table", WandManagerBlock::new);
    public static DeferredHolder<Block, Block> CREATOR = registerBlock("creator", CreatorBlock::new);
    public static DeferredHolder<Block, Block> INFUSER = BLOCKS.register("infuser", InfuserBlock::new);
    public static DeferredHolder<Block, Block> CHALLENGE_ALTAR = BLOCKS.register("challenge_altar", ChallengeAltarBlock::new);
    public static DeferredHolder<Block, Block> LOOT_CHEST = BLOCKS.register("loot_chest", LootChestBlock::new);
    public static DeferredHolder<Block, Block> MODULAR_CHAOS_CUBE = BLOCKS.register("modular_chaos_cube", ModularChaosCubeBlock::new);
    public static DeferredHolder<Block, Block> AUGMENT_MODIFICATION_STATION = registerBlock("augment_modification_station", AugmentModificationBlock::new);
    public static DeferredHolder<Block, Block> ENCHANTED_BLOCK = registerBlock("enchanted_block", EnchantedBlock::new);
    public static DeferredHolder<Block, Block> WAND = BLOCKS.register("wand_mystic", WandBlock::new);
    public static DeferredHolder<Block, Block> TANK = registerBlock("tank", NexiteTankBlock::new);
    public static DeferredHolder<Block, Block> LIGHTING = BLOCKS.register("lighting", LightBlock::new);

    public static DeferredHolder<Block, Block> NEXITE_ORE = registerBlock("nexite_ore",
        () -> new DropExperienceBlock(
            UniformInt.of(3, 6),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE)
        )
    );

    public static DeferredHolder<Block, Block> NEXITE_DEEPSLATE_ORE = registerBlock("nexite_deepslate_ore",
        () -> new DropExperienceBlock(
            UniformInt.of(3, 6),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE)
        )
    );

    public static DeferredHolder<Block, Block> NEXITE_BLOCK = registerBlock("nexite_block",
        () -> new Block(
            BlockBehaviour.Properties.of()
                .strength(Blocks.DIAMOND_BLOCK.defaultDestroyTime())
                .sound(SoundType.STONE)
                .noOcclusion()
        )
    );

    public static final DeferredHolder<Block, Block> TRAIL_PORTAL = registerBlock("trial_portal",
        () -> new TrialPortalBlock(
            BlockBehaviour.Properties.of()
                .noCollission()
                .randomTicks()
                .strength(-1.0F)
                .sound(SoundType.GLASS)
                .lightLevel(p_50870_ -> 11)
                .pushReaction(PushReaction.BLOCK)
        )
    );

    public static DeferredHolder<Block, Block> RAW_NEXITE_BLOCK = registerBlock("raw_nexite_block",
        () -> new Block(BlockBehaviour.Properties.of().strength(Blocks.RAW_GOLD_BLOCK.defaultDestroyTime()).noOcclusion())
    );

    public static DeferredHolder<Block, Block> NEXITE_POWDER_BLOCK = registerBlock("nexite_powder_block",
        () -> new Block(BlockBehaviour.Properties.of())
    );

    public static BlockBehaviour.Properties sharedBlockBehaviour(){
        return BlockBehaviour.Properties.of().strength(1f)
            .sound(SoundType.DEEPSLATE_BRICKS).noOcclusion();
    }

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredHolder<Block, T> block) {
        ItemsRegister.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
