package org.jahdoo.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.JahdooMod.MOD_ID;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MOD_ID);

    public static final String CREATOR_RECIPE_ID = "creator_block";

    public static final DeferredHolder<RecipeType<?>, RecipeType<CreatorRecipe>> CREATOR_TYPE = RECIPE_TYPES.register(CREATOR_RECIPE_ID, () -> RecipeType.simple(ModHelpers.res(CREATOR_RECIPE_ID)));
    public static final DeferredHolder<RecipeSerializer<?>, CreatorRecipe.Serializer> CREATOR_SERIALIZER = RECIPE_SERIALIZERS.register(CREATOR_RECIPE_ID, CreatorRecipe.Serializer::new);


    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}