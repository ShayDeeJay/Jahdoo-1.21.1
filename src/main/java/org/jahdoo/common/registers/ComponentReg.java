package org.jahdoo.common.registers;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.components.LootCrateData;
import org.jahdoo.common.items.magnet.MagnetData;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.items.runes.rune_data.RuneData;

import java.util.function.UnaryOperator;

public class ComponentReg {

    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JahdooMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AbilityHolder>> ABILITY_HOLDER =
        register("ability_holder", builder ->
            builder
                .persistent(AbilityHolder.CODEC)
                .networkSynchronized(AbilityHolder.STREAM_CODEC)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> AUGMENT_RATING =
        register("augment_rating", builder ->
            builder
                .persistent(Codec.DOUBLE)
                .networkSynchronized(ByteBufCodecs.DOUBLE)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> HEART_CONTAINER =
        register("heart_container", builder ->
            builder.persistent(Codec.FLOAT)
                .networkSynchronized(ByteBufCodecs.FLOAT)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> JAHDOO_RARITY =
        register("rarity", builder ->
            builder
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.INT)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORE_INTEGER =
        register("number", builder ->
            builder
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.INT)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SHIELD_BLOCK_CHANCE =
        register("shield_block_chance", builder ->
            builder
                .persistent(Codec.DOUBLE)
                .networkSynchronized(ByteBufCodecs.DOUBLE)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ID =
        register("item_ids", builder ->
            builder
                .persistent(Codec.STRING)
                .networkSynchronized(ByteBufCodecs.STRING_UTF8)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> INTERACTION_HAND =
        register("interaction_hand", builder ->
            builder
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.INT)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RuneData>> RUNE_DATA =
        register("rune_data", builder ->
            builder
                .persistent(RuneData.CODEC)
                .networkSynchronized(RuneData.STREAM_CODEC)
                .cacheEncoding()
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<JahdooGearData>> JAHDOO_GEAR_DATA =
        register("rune_holder", builder ->
            builder
                .persistent(JahdooGearData.CODEC)
                .networkSynchronized(JahdooGearData.STREAM_CODEC)
                .cacheEncoding()
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MagnetData>> MAGNET_DATA =
        register("magnet_data", builder ->
            builder
                .persistent(MagnetData.CODEC)
                .networkSynchronized(MagnetData.STREAM_CODEC)
                .cacheEncoding()
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CoreData>> CORE_DATA =
        register("core_data", builder ->
            builder
                .persistent(CoreData.CODEC)
                .networkSynchronized(CoreData.STREAM_CODEC)
                .cacheEncoding()
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LootCrateData>> LOOT_CRATE_DATA =
        register("loot_crate_data", builder ->
            builder
                .persistent(LootCrateData.CODEC)
                .networkSynchronized(LootCrateData.STREAM_CODEC)
                .cacheEncoding()
        );


    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator) {
        return COMPONENTS.register(name, () -> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
