package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.block.loot_chest.LootChestEntity;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.block.crafter.CreatorEntity;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.block.rune_table.RuneTableEntity;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.block.tank.NexiteTankBlockEntity;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.block.wand_block_manager.WandManagerEntity;

public class BlockEntitiesRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JahdooMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AugmentModificationEntity>> AUGMENT_MODIFICATION_STATION_BE =
        registerBlockEntity("augment_modification_station_be", AugmentModificationEntity::new, BlocksRegister.AUGMENT_MODIFICATION_STATION);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreatorEntity>> CREATOR_BE =
        registerBlockEntity("creator_be", CreatorEntity::new, BlocksRegister.CREATOR);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WandManagerEntity>> WAND_MANAGER_TABLE_BE =
        registerBlockEntity("wand_manager_table_be", WandManagerEntity::new, BlocksRegister.WAND_MANAGER_TABLE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WandBlockEntity>> WAND_BE =
        registerBlockEntity("wand_be", WandBlockEntity::new, BlocksRegister.WAND);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NexiteTankBlockEntity>> TANK_BE =
        registerBlockEntity("tank_be", NexiteTankBlockEntity::new, BlocksRegister.TANK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchantedBlockEntity>> ENCHANTED_BE =
        registerBlockEntity("enchanted_be", EnchantedBlockEntity::new, BlocksRegister.ENCHANTED_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserBlockEntity>> INFUSER_BE =
        registerBlockEntity("infuser_be", InfuserBlockEntity::new, BlocksRegister.INFUSER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChallengeAltarBlockEntity>> CHALLENGE_ALTAR_BE =
        registerBlockEntity("challenge_altar_be", ChallengeAltarBlockEntity::new, BlocksRegister.CHALLENGE_ALTAR);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModularChaosCubeEntity>> MODULAR_CHAOS_CUBE_BE =
        registerBlockEntity("modular_chaos_cube_be", ModularChaosCubeEntity::new, BlocksRegister.MODULAR_CHAOS_CUBE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootChestEntity>> LOOT_CHEST_BE =
        registerBlockEntity("loot_chest_be", LootChestEntity::new, BlocksRegister.LOOT_CHEST);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShoppingTableEntity>> SHOPPING_TABLE_BE =
        registerBlockEntity("shopping_table_be", ShoppingTableEntity::new, BlocksRegister.SHOPPING_TABLE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RuneTableEntity>> RUNE_TABLE_BE =
            registerBlockEntity("rune_table_be", RuneTableEntity::new, BlocksRegister.RUNE_TABLE);

    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntity(
        String name,
        BlockEntityType.BlockEntitySupplier<T> factory,
        DeferredHolder<Block, Block> blocks
    ) {
        return BLOCK_ENTITIES.register(name,
            () -> BlockEntityType.Builder.of(factory, blocks.get()).build(null));
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

