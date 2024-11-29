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
import org.jahdoo.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.block.crafter.CreatorEntity;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.block.tank.NexiteTankBlockEntity;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.block.wand_block_manager.WandManagerTableEntity;

public class BlockEntitiesRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JahdooMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AugmentModificationEntity>> AUGMENT_MODIFICATION_STATION_BE =
        BLOCK_ENTITIES.register("augment_modification_station_be", () -> BlockEntityType.Builder.of(AugmentModificationEntity::new, BlocksRegister.AUGMENT_MODIFICATION_STATION.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreatorEntity>> CREATOR_BE =
        BLOCK_ENTITIES.register("creator_be", () -> BlockEntityType.Builder.of(CreatorEntity::new, BlocksRegister.CREATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WandManagerTableEntity>> WAND_MANAGER_TABLE_BE =
        BLOCK_ENTITIES.register("infusion_table_be", () -> BlockEntityType.Builder.of(WandManagerTableEntity::new, BlocksRegister.WAND_MANAGER_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WandBlockEntity>> WAND_BE =
        BLOCK_ENTITIES.register("wand_be", () -> BlockEntityType.Builder.of(WandBlockEntity::new, BlocksRegister.WAND.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NexiteTankBlockEntity>> TANK_BE =
        BLOCK_ENTITIES.register("tank_be", () -> BlockEntityType.Builder.of(NexiteTankBlockEntity::new, BlocksRegister.TANK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchantedBlockEntity>> ENCHANTED_BE =
        BLOCK_ENTITIES.register("enchanted_be", () -> BlockEntityType.Builder.of(EnchantedBlockEntity::new, BlocksRegister.ENCHANTED_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserBlockEntity>> INFUSER_BE =
        BLOCK_ENTITIES.register("infuser_be", () -> BlockEntityType.Builder.of(InfuserBlockEntity::new, BlocksRegister.INFUSER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModularChaosCubeEntity>> MODULAR_CHAOS_CUBE_BE =
        BLOCK_ENTITIES.register("modular_chaos_cube_be", () -> BlockEntityType.Builder.of(ModularChaosCubeEntity::new, BlocksRegister.MODULAR_CHAOS_CUBE.get()).build(null));


    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntity(
        String name,
        BlockEntityType.BlockEntitySupplier<T> factory,
        Block blocks
    ) {
        return BLOCK_ENTITIES.register(name,
            () -> BlockEntityType.Builder.of(factory, blocks).build(null));
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

