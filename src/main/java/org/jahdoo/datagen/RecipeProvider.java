package org.jahdoo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;

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
    }

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
