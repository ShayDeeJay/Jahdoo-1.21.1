package org.jahdoo.common.datagen.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;

import java.util.function.Supplier;

import static net.neoforged.neoforge.registries.DeferredRegister.create;
import static net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class ModLootModifiers {

    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS =
        create(Registries.LOOT_FUNCTION_TYPE, JahdooMod.MOD_ID);

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
        create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, JahdooMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        LOOT_FUNCTIONS.register(eventBus);
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> APPEND_LOOT_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register("append_loot", AddItemModifier.CODEC);
}
