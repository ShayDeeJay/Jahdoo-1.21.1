package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jahdoo.ascension.utils.ModTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.common.registers.ItemReg.*;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void generateForEnabledBlockFamilies(RecipeOutput enabledFeatures, FeatureFlagSet p_251836_) {
        super.generateForEnabledBlockFamilies(enabledFeatures, p_251836_);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        tankRecipe(recipeOutput, TANK.get().asItem());
        dissembler(recipeOutput, DISSEMBLER.get().asItem());
        nexite(recipeOutput, NEXITE_BLOCK.get().asItem());
        chaosCube(recipeOutput, MODULAR_CHAOS_CUBE.get().asItem());
        runeManager(recipeOutput, RUNE_TABLE.get().asItem());
        wandManager(recipeOutput, WAND_MANAGER_TABLE.get().asItem());
        oreSmelting(recipeOutput, List.of(Items.ROTTEN_FLESH), RecipeCategory.MISC, Items.LEATHER, 2.0F, 200, "leather");
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, ESSENCE_FRAGMENT.get(), RecipeCategory.MISC, AUGMENT_CORE.get());
        ticket(recipeOutput, CHALLENGER_TICKET.get());
    }

    protected void ticket(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('M', Items.PAPER)
            .define('X', AUGMENT_CORE.get())
            .pattern(" M ")
            .pattern("MXM")
            .pattern(" M ")
            .unlockedBy("paper", has(Items.PAPER))
            .unlockedBy("core", has(AUGMENT_CORE.get()))
            .save(output);
    }

    protected void dissembler(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('M', Items.MUD_BRICKS)
            .define('X', AUGMENT_CORE.get())
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

    protected void chaosCube(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('X', ModTags.Items.WAND_TAGS)
            .define('M', Items.MUD_BRICKS)
            .pattern("MMM")
            .pattern("MXM")
            .pattern("MMM")
            .unlockedBy("wand_item", has(ModTags.Items.WAND_TAGS))
            .save(output);
    }

    protected void wandManager(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('X', ModTags.Items.WAND_TAGS)
            .define('M', Items.MUD_BRICKS)
            .pattern(" M ")
            .pattern(" X ")
            .pattern(" M ")
            .unlockedBy("wand_item", has(ModTags.Items.WAND_TAGS))
            .save(output);
    }

    protected void runeManager(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('X', AUGMENT_CORE.get())
            .define('M', Items.MUD_BRICKS)
            .pattern("MMM")
            .pattern("MXM")
            .pattern("MMM")
            .unlockedBy("wand_item", has(RUNE.get()))
            .save(output);
    }

    protected void nexite(RecipeOutput output, Item result) {
        nineBlockStorageRecipes(output, RecipeCategory.MISC, NEXITE_POWDER.get(), RecipeCategory.BUILDING_BLOCKS, NEXITE_BLOCK.get());

        oreSmelting(output, List.of(RAW_NEXITE_BLOCK.get()),RecipeCategory.BUILDING_BLOCKS, result, 2.0F, 200, "nexite");
    }


}
