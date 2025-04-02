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
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.items.magnet.MagnetData;
import org.jahdoo.common.items.runes.rune_data.RuneData;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.items.wand.WandData;

import java.util.function.UnaryOperator;

public class ComponentReg {

    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JahdooMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandAbilityHolder>> WAND_ABILITY_HOLDER =
        register("wand_ability_holder", builder ->
            builder
                .persistent(WandAbilityHolder.CODEC)
                .networkSynchronized(WandAbilityHolder.STREAM_CODEC)
        );

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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NUMBER =
        register("number", builder ->
            builder
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.INT)
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> INTERACTION_HAND =
        register("interaction_hand", builder ->
            builder
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.INT)
        );


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandData>> WAND_DATA =
        register("wand_data", builder ->
            builder
                .persistent(WandData.CODEC)
                .networkSynchronized(WandData.STREAM_CODEC)
                .cacheEncoding()
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RuneData>> RUNE_DATA =
        register("rune_data", builder ->
            builder
                .persistent(RuneData.CODEC)
                .networkSynchronized(RuneData.STREAM_CODEC)
                .cacheEncoding()
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RuneHolder>> RUNE_HOLDER =
        register("rune_holder", builder ->
            builder
                .persistent(RuneHolder.CODEC)
                .networkSynchronized(RuneHolder.STREAM_CODEC)
                .cacheEncoding()
        );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MagnetData>> MAGNET_DATA =
        register("magnet_data", builder ->
            builder
                .persistent(MagnetData.CODEC)
                .networkSynchronized(MagnetData.STREAM_CODEC)
                .cacheEncoding()
        );

//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AbilityData>> ABILITY_HOLDER =
//        register("ability_holder", builder ->
//            builder
//                .persistent(AbilityData.CODEC)
//                .networkSynchronized(AbilityData.STREAM_CODEC)
//                .cacheEncoding()
//        );

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator) {
        return COMPONENTS.register(name, () -> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
