package org.jahdoo.common.registers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.block.TrialPortalBlock;
import org.jahdoo.common.block.augment_modification_station.AugmentModificationBlock;
import org.jahdoo.common.block.altar.AltarBlock;
import org.jahdoo.common.block.enchanted_block.EnchantedBlock;
import org.jahdoo.common.block.lock.LockBlock;
import org.jahdoo.common.block.loot_chest.LootChestBlock;
import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeBlock;
import org.jahdoo.common.block.infuser.InfuserBlock;
import org.jahdoo.common.block.light_block.LightBlock;
import org.jahdoo.common.block.perk_table.PerkTable;
import org.jahdoo.common.block.rune_table.RuneTable;
import org.jahdoo.common.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.common.block.tank.TankBlock;
import org.jahdoo.common.block.wand.WandBlock;
import org.jahdoo.common.block.wand_manager.WandManagerBlock;

import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.*;

public class BlockReg {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, JahdooMod.MOD_ID);

    public static DeferredHolder<Block, Block> LIGHTING =
        registerBlock("lighting", LightBlock::new);

    public static DeferredHolder<Block, Block> CHALLENGE_ALTAR =
        registerBlock("challenge_altar", AltarBlock::new);

    public static DeferredHolder<Block, Block> LOOT_CHEST =
        registerBlock("loot_chest", LootChestBlock::new);

    public static DeferredHolder<Block, Block> INFUSER =
        registerBlock("infuser", InfuserBlock::new);

    public static DeferredHolder<Block, Block> MODULAR_CHAOS_CUBE =
        registerBlock("modular_chaos_cube", ModularChaosCubeBlock::new);

    public static DeferredHolder<Block, Block> WAND =
        registerBlock("wand_mystic", WandBlock::new);

    public static DeferredHolder<Block, Block> WAND_MANAGER_TABLE =
        registerBlockWithItem("wand_manager_table", WandManagerBlock::new);

    public static DeferredHolder<Block, Block> AUGMENT_MODIFICATION_STATION =
        registerBlockWithItem("augment_modification_station", AugmentModificationBlock::new);

    public static DeferredHolder<Block, Block> ENCHANTED_BLOCK =
        registerBlockWithItem("enchanted_block", EnchantedBlock::new);

    public static DeferredHolder<Block, Block> TANK =
        registerBlockWithItem("tank", TankBlock::new);

    public static DeferredHolder<Block, Block> LOCK =
        registerBlockWithItem("lock", LockBlock::new);

    public static DeferredHolder<Block, Block> SHOPPING_TABLE =
        registerBlockWithItem("shopping_table", ShoppingTableBlock::new);

    public static DeferredHolder<Block, Block> PERK_TABLE =
        registerBlockWithItem("perk_table", PerkTable::new);

    public static DeferredHolder<Block, Block> RUNE_TABLE =
        registerBlockWithItem("rune_table", RuneTable::new);

    public static DeferredHolder<Block, Block> TRAIL_PORTAL =
        registerBlockWithItem("trial_portal", TrialPortalBlock::new);

    public static DeferredHolder<Block, Block> NEXITE_ORE = registerBlockWithItem("nexite_ore",
        () -> new DropExperienceBlock(UniformInt.of(3, 6), ofFullCopy(DIAMOND_ORE))
    );

    public static DeferredHolder<Block, Block> NEXITE_DEEPSLATE_ORE = registerBlockWithItem("nexite_deepslate_ore",
        () -> new DropExperienceBlock(UniformInt.of(3, 6), ofFullCopy(DEEPSLATE_DIAMOND_ORE))
    );

    public static DeferredHolder<Block, Block> NEXITE_BLOCK = registerBlockWithItem("nexite_block",
        () -> new Block(of().strength(DIAMOND_BLOCK.defaultDestroyTime()).sound(SoundType.STONE).noOcclusion())
    );

    public static DeferredHolder<Block, Block> RAW_NEXITE_BLOCK = registerBlockWithItem("raw_nexite_block",
        () -> new Block(of().strength(RAW_GOLD_BLOCK.defaultDestroyTime()).noOcclusion())
    );

    public static DeferredHolder<Block, Block> NEXITE_POWDER_BLOCK = registerBlockWithItem("nexite_powder_block",
        () -> new Block(of())
    );

    public static Properties sharedBehaviour = of().strength(1f).sound(SoundType.DEEPSLATE_BRICKS).noOcclusion();

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> DeferredHolder<Block, T> registerBlockWithItem(String name, Supplier<T> block) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredHolder<Block, T> block) {
        ItemReg.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
