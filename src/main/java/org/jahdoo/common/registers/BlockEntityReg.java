package org.jahdoo.common.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.block.creator.CreatorEntity;
import org.jahdoo.common.block.dissembler.DisassemblerBlockEntity;
import org.jahdoo.common.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.block.loot_chest.LootChestEntity;
import org.jahdoo.common.block.loot_crate.LootCrateEntity;
import org.jahdoo.common.block.loot_pot.LootPotBlockEntity;
import org.jahdoo.common.block.perk_table.PerkTableEntity;
import org.jahdoo.common.block.power_up_station.PowerUpStationEntity;
import org.jahdoo.common.block.divine_forge.DivineForgeEntity;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.block.tank.TankBlockEntity;
import org.jahdoo.common.block.ticket_bureau.TicketBureauBlockEntity;
import org.jahdoo.common.block.wand_manager.WandManagerEntity;

import static org.jahdoo.common.registers.BlockReg.*;

public class BlockEntityReg {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JahdooMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TankBlockEntity>> TANK_BE =
        registerBlockEntity("tank_be", TankBlockEntity::new, TANK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootPotBlockEntity>> LOOT_POT_BE =
        registerBlockEntity("loot_pot_be", LootPotBlockEntity::new, LOOT_POT);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TicketBureauBlockEntity>> TICKET_BUREAU_BE =
        registerBlockEntity("ticket_bureau_be", TicketBureauBlockEntity::new, TICKET_BUREAU);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootCrateEntity>> LOOT_CRATE_BE =
        registerBlockEntity("loot_crate_be", LootCrateEntity::new, LOOT_CRATE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PerkTableEntity>> PERK_TABLE_BE =
        registerBlockEntity("perk_table_be", PerkTableEntity::new, PERK_TABLE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LockBlockEntity>> LOCK_BE =
        registerBlockEntity("lock_be", LockBlockEntity::new, LOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PowerUpStationEntity>> POWER_UP_BE =
        registerBlockEntity("power_up_station", PowerUpStationEntity::new, POWER_UP_STATION);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchantedBlockEntity>> ENCHANTED_BE =
        registerBlockEntity("enchanted_be", EnchantedBlockEntity::new, ENCHANTED_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisassemblerBlockEntity>> INFUSER_BE =
        registerBlockEntity("dissembler_be", DisassemblerBlockEntity::new, DISSEMBLER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AltarBlockEntity>> CHALLENGE_ALTAR_BE =
        registerBlockEntity("challenge_altar_be", AltarBlockEntity::new, CHALLENGE_ALTAR);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChaosCubeEntity>> MODULAR_CHAOS_CUBE_BE =
        registerBlockEntity("modular_chaos_cube_be", ChaosCubeEntity::new, MODULAR_CHAOS_CUBE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootChestEntity>> LOOT_CHEST_BE =
        registerBlockEntity("loot_chest_be", LootChestEntity::new, LOOT_CHEST);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShoppingTableEntity>> SHOPPING_TABLE_BE =
        registerBlockEntity("shopping_table_be", ShoppingTableEntity::new, SHOPPING_TABLE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DivineForgeEntity>> RUNE_TABLE_BE =
        registerBlockEntity("rune_table_be", DivineForgeEntity::new, RUNE_TABLE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WandManagerEntity>> WAND_MANAGER_TABLE_BE =
        registerBlockEntity("wand_manager_table_be", WandManagerEntity::new, WAND_MANAGER_TABLE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreatorEntity>> CREATOR_BE =
        registerBlockEntity("creator_be", CreatorEntity::new, CREATOR_BLOCK);

    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntity(
        String name,
        BlockEntityType.BlockEntitySupplier<T> factory,
        DeferredHolder<Block, Block> blocks
    ) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, blocks.get()).build(null));
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

