package org.jahdoo.datagen;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Blocks;
import org.jahdoo.recipe.CreatorRecipe;
import org.jahdoo.recipe.CreatorRecipeBuilder;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        creatorBlockRecipe(recipeOutput, BlocksRegister.CREATOR.get().asItem());
        tankRecipe(recipeOutput, BlocksRegister.TANK.get().asItem());
        infuser(recipeOutput, BlocksRegister.INFUSER.get().asItem());
        smelterRecipe(recipeOutput, BlocksRegister.NEXITE_BLOCK.get().asItem());
        advancedAugmentCore(recipeOutput, ItemsRegister.ADVANCED_AUGMENT_CORE.get());
        hyperCore(recipeOutput, ItemsRegister.AUGMENT_HYPER_CORE.get());
        augment(recipeOutput, ItemsRegister.AUGMENT_ITEM.get());
        tomeOfUnity(recipeOutput, ItemsRegister.TOME_OF_UNITY.get());
        wands(recipeOutput, ItemsRegister.WAND_ITEM_FROST.get(), Items.LIGHT_BLUE_DYE, "frost");
        wands(recipeOutput, ItemsRegister.WAND_ITEM_INFERNO.get(), Items.ORANGE_DYE, "inferno");
        wands(recipeOutput, ItemsRegister.WAND_ITEM_MYSTIC.get(), Items.PURPLE_DYE, "mystic");
        wands(recipeOutput, ItemsRegister.WAND_ITEM_LIGHTNING.get(), Items.LIGHT_GRAY_DYE, "lightning");
        wands(recipeOutput, ItemsRegister.WAND_ITEM_VITALITY.get(), Items.RED_DYE, "vitality");
    }

    //Creator Recipes
    protected void hyperCore(RecipeOutput output, Item result) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 24)
            .requires(ItemsRegister.ADVANCED_AUGMENT_CORE.get(), 4)
            .requires(BlocksRegister.NEXITE_BLOCK.get(), 4)
            .unlockedBy("augment_hyper_core", has(ItemsRegister.ADVANCED_AUGMENT_CORE.get()))
            .save(output);
    }

    protected void augment(RecipeOutput output, Item result) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 18)
            .requires(ItemsRegister.AUGMENT_FRAGMENT.get(), 3)
            .requires(ItemsRegister.AUGMENT_CORE.get())
            .unlockedBy("augment", has(ItemsRegister.AUGMENT_ITEM.get()))
            .save(output);
    }

    protected void tomeOfUnity(RecipeOutput output, Item result) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 32)
            .requires(Items.BOOK)
            .requires(ItemsRegister.AUGMENT_FRAGMENT.get())
            .requires(ItemsRegister.AUGMENT_CORE.get(), 2)
            .unlockedBy("tome_of_unity", has(ItemsRegister.AUGMENT_CORE.get()))
            .save(output);
    }

    protected void advancedAugmentCore(RecipeOutput output, Item result) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 12)
            .requires(ItemsRegister.AUGMENT_CORE.get(), 4)
            .requires(ItemsRegister.NEXITE_POWDER.get(), 4)
            .unlockedBy("augment_core", has(ItemsRegister.AUGMENT_CORE.get()))
            .group("creator_block")
            .save(output);
    }

    //Wands
    protected void wands(RecipeOutput output, Item result, Item unique, String type) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 12)
            .requires(ItemsRegister.AUGMENT_CORE.get())
            .requires(Items.DIAMOND)
            .requires(Items.STICK)
            .requires(unique)
            .unlockedBy("augment_core", has(ItemsRegister.AUGMENT_CORE.get()))
            .group("wand"+type)
            .save(output);
    }

    //Vanilla Smelting
    protected void smelterRecipe(RecipeOutput output, Item result) {
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(BlocksRegister.RAW_NEXITE_BLOCK.get()),
                RecipeCategory.BUILDING_BLOCKS,
                result, 2.0F, 200
            ).unlockedBy("nexite_powder", has(ItemsRegister.NEXITE_POWDER.get()))
            .save(output);
    }

    //Vanilla Crafting
    protected void creatorBlockRecipe(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .define('M', Items.MUD_BRICKS)
                .define('X', Items.REDSTONE)
                .pattern(" M ")
                .pattern("MXM")
                .pattern(" M ")
                .unlockedBy("mud_bricks", has(Items.MUD_BRICKS))
                .save(output);
    }

    protected void infuser(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('M', Items.MUD_BRICKS)
            .define('X', ItemsRegister.AUGMENT_ITEM.get())
            .pattern(" M ")
            .pattern("MXM")
            .pattern(" M ")
            .unlockedBy("mud_bricks", has(Items.MUD_BRICKS))
            .save(output);
    }

    protected void tankRecipe(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('M', Items.GLASS)
            .define('X', Items.DEEPSLATE)
            .pattern("MMM")
            .pattern("MXM")
            .pattern("MMM")
            .unlockedBy("glass", has(Items.GLASS))
            .save(output);
    }

}
