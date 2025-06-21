package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.block.creator.recipe.*;
import org.jahdoo.common.block.creator.recipe.recipes.*;

import java.util.List;
import java.util.Optional;

import static net.minecraft.resources.ResourceKey.createRegistryKey;
import static net.neoforged.neoforge.registries.DeferredRegister.create;

public class CreatorRecipeReg {

    public static final ResourceKey<Registry<CreatorRecipes>> RECIPE_CREATOR =
        createRegistryKey(ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID, "recipe_creator"));

    private static final DeferredRegister<CreatorRecipes> ABILITIES =
        create(RECIPE_CREATOR, JahdooMod.MOD_ID);

    public static final Registry<CreatorRecipes> REGISTRY =
        new RegistryBuilder<>(RECIPE_CREATOR).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("CreatorRecipe.RegisterRegistry");
        event.register(REGISTRY);
    }

    public static Optional<CreatorRecipes> getSpellsByTypeId(List<ItemStack> currentRecipe) {
        var x = CreatorRecipeReg.REGISTRY
            .stream()
            .filter(a -> a.canCraft(currentRecipe));
        return x.findFirst();
    }

    public static final DeferredHolder<CreatorRecipes, CreatorRecipes> EXIT_KEY =
        registerSpell(new ExitKey());

    public static final DeferredHolder<CreatorRecipes, CreatorRecipes> SANCTUARY_KEY =
        registerSpell(new SanctuaryKey());

    public static final DeferredHolder<CreatorRecipes, CreatorRecipes> BAZAAR_KEY =
        registerSpell(new BazaarKey());

    public static final DeferredHolder<CreatorRecipes, CreatorRecipes> CRYPT_KEY =
        registerSpell(new CryptKey());

    public static final DeferredHolder<CreatorRecipes, CreatorRecipes> AUGMENT_CORE =
        registerSpell(new AugmentCore());

    private static DeferredHolder<CreatorRecipes, CreatorRecipes> registerSpell(CreatorRecipes recipe) {
        return ABILITIES.register(recipe.recipeId(), () -> recipe);
    }

    public static void register(IEventBus eventBus) {
        ABILITIES.register(eventBus);
    }
}
