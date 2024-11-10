package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.block.crafter.CreatorEntity;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.block.tank.TankBlockEntity;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.block.wandBlockManager.WandManagerTableEntity;

import java.util.function.Supplier;

public class BlockEntitiesRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JahdooMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreatorEntity>> CREATOR_BE =
        BLOCK_ENTITIES.register("creator_be", () -> BlockEntityType.Builder.of(CreatorEntity::new, BlocksRegister.CREATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WandManagerTableEntity>> WAND_MANAGER_TABLE_BE =
        BLOCK_ENTITIES.register("infusion_table_be", () -> BlockEntityType.Builder.of(WandManagerTableEntity::new, BlocksRegister.WAND_MANAGER_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WandBlockEntity>> WAND_BE =
        BLOCK_ENTITIES.register("wand_be", () -> BlockEntityType.Builder.of(WandBlockEntity::new, BlocksRegister.WAND.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TankBlockEntity>> TANK_BE =
        BLOCK_ENTITIES.register("tank_be", () -> BlockEntityType.Builder.of(TankBlockEntity::new, BlocksRegister.TANK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserBlockEntity>> INFUSER_BE =
        BLOCK_ENTITIES.register("infuser_be", () -> BlockEntityType.Builder.of(InfuserBlockEntity::new, BlocksRegister.INFUSER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutomationBlockEntity>> AUTOMATION_BLOCK =
        BLOCK_ENTITIES.register("automation_block", () -> BlockEntityType.Builder.of(AutomationBlockEntity::new, BlocksRegister.AUTOMATION_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

