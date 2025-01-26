package org.jahdoo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jahdoo.recipe.CreatorRecipeBuilder;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.ModTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.jahdoo.registers.ItemsRegister.*;

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
        creatorBlockRecipe(recipeOutput, BlocksRegister.CREATOR.get().asItem());
        tankRecipe(recipeOutput, BlocksRegister.TANK.get().asItem());
        infuser(recipeOutput, BlocksRegister.INFUSER.get().asItem());
        augment(recipeOutput, AUGMENT_ITEM.get());
        chaosCube(recipeOutput, BlocksRegister.MODULAR_CHAOS_CUBE.get().asItem());
        nexite(recipeOutput, BlocksRegister.NEXITE_BLOCK.get().asItem());
        coinCompressor(recipeOutput);
        augmentModificationTable(recipeOutput, BlocksRegister.AUGMENT_MODIFICATION_STATION.get().asItem());
        wands(recipeOutput, WAND_ITEM_FROST.get(), Items.LIGHT_BLUE_DYE, "frost");
        wands(recipeOutput, WAND_ITEM_INFERNO.get(), Items.ORANGE_DYE, "inferno");
        wands(recipeOutput, WAND_ITEM_MYSTIC.get(), Items.PURPLE_DYE, "mystic");
        wands(recipeOutput, WAND_ITEM_LIGHTNING.get(), Items.LIGHT_GRAY_DYE, "lightning");
        wands(recipeOutput, WAND_ITEM_VITALITY.get(), Items.RED_DYE, "vitality");
    }


    //Creator Recipes
    protected void hyperCore(RecipeOutput output, Item result) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 24)
            .requires(ADVANCED_AUGMENT_CORE.get(), 4)
            .requires(BlocksRegister.NEXITE_BLOCK.get(), 4)
            .unlockedBy("augment_hyper_core", has(ADVANCED_AUGMENT_CORE.get()))
            .save(output);
    }

    protected void augment(RecipeOutput output, Item result) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 18)
            .requires(AUGMENT_FRAGMENT.get(), 3)
            .requires(AUGMENT_CORE.get())
            .unlockedBy("augment", has(AUGMENT_ITEM.get()))
            .save(output);
    }

    protected void advancedAugmentCore(RecipeOutput output, Item result) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 12)
            .requires(AUGMENT_CORE.get(), 4)
            .requires(NEXITE_POWDER.get(), 4)
            .unlockedBy("augment_core", has(AUGMENT_CORE.get()))
            .group("advanced_augment_core")
            .save(output);
    }

    //Wands
    protected void wands(RecipeOutput output, Item result, Item unique, String type) {
        CreatorRecipeBuilder.shapeless(RecipeCategory.MISC, result, 12)
            .requires(AUGMENT_CORE.get())
            .requires(Items.DIAMOND)
            .requires(Items.STICK)
            .requires(unique)
            .unlockedBy("augment_core", has(AUGMENT_CORE.get()))
            .group("wand"+type)
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
            .define('X', AUGMENT_ITEM.get())
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

    protected void Gold(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('X', ModTags.Items.WAND_TAGS)
            .define('M', Items.MUD_BRICKS)
            .pattern("MMM")
            .pattern("MXM")
            .pattern("MMM")
            .unlockedBy("wand_item", has(ModTags.Items.WAND_TAGS))
            .save(output);
    }

    protected void augmentModificationTable(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
            .define('X', AUGMENT_ITEM.get())
            .define('M', Items.MUD_BRICKS)
            .pattern(" X ")
            .pattern(" M ")
            .pattern(" M ")
            .unlockedBy("augment_item", has(AUGMENT_ITEM.get()))
            .save(output);
    }

    protected void nexite(RecipeOutput output, Item result) {
        nineBlockStorageRecipes(output, RecipeCategory.MISC, NEXITE_POWDER.get(), RecipeCategory.BUILDING_BLOCKS, BlocksRegister.NEXITE_BLOCK.get());
        oreSmelting(output, List.of(BlocksRegister.RAW_NEXITE_BLOCK.get()),RecipeCategory.BUILDING_BLOCKS, result, 2.0F, 200, "nexite");
    }

    protected void coinCompressor(RecipeOutput output) {
        var misc = RecipeCategory.MISC;
        nineBlockStorageRecipes(output, misc, BRONZE_COIN.get(), misc, SILVER_COIN.get(), "packed_silver", null, "unpacked_bronze", null);
        nineBlockStorageRecipes(output, misc, SILVER_COIN.get(), misc, GOLD_COIN.get(),"packed_gold", null, "unpacked_silver", null);
        nineBlockStorageRecipes(output, misc, GOLD_COIN.get(), misc, PLATINUM_COIN.get(), "packed_platinum", null, "unpacked_gold", null);
    }

}
