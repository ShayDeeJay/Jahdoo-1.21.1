package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jahdoo.trial_nexus.utils.ModTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS;
import static net.minecraft.data.recipes.RecipeCategory.MISC;
import static net.minecraft.world.item.Items.*;
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
        diamond(recipeOutput, DIAMOND_NUGGET.get());
        netheriteIngot(recipeOutput, NETHERITE_NUGGET.get());
        chaosCube(recipeOutput, MODULAR_CHAOS_CUBE.get().asItem());
        runeManager(recipeOutput, RUNE_TABLE.get().asItem());
        oreSmelting(recipeOutput, List.of(ROTTEN_FLESH), MISC, LEATHER, 2.0F, 200, "leather");
        packedMudClayBlock(recipeOutput, PACKED_MUD_CLAY.get().asItem());
        ticketBureau(recipeOutput, TICKET_BUREAU.get().asItem());
        creator(recipeOutput, CREATOR_BLOCK.get().asItem());
        ticket(recipeOutput, TRIAL_TICKET.get());
        enchantedDiamond(recipeOutput);
        xpOrbs(recipeOutput);
        roseQuartz(recipeOutput);
    }

    protected void ticket(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(MISC, result, 1)
            .define('M', NEXITE_POWDER.get())
            .define('X', DIAMOND)
            .define('Y', PAPER)
            .pattern(" Y ")
            .pattern("MXM")
            .pattern(" Y ")
            .unlockedBy("diamond", has(DIAMOND))
            .save(output);
    }

    protected void packedMudClayBlock(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(MISC, result, 4)
            .define('M', DIRT)
            .define('X', CLAY)
            .pattern(" M ")
            .pattern("MXM")
            .pattern(" M ")
            .unlockedBy("packed_mud_clay", has(PACKED_MUD))
            .save(output);
    }

    protected void dissembler(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(MISC, result)
            .define('M', PACKED_MUD_CLAY.get())
            .define('X', AUGMENT_CORE.get())
            .pattern(" M ")
            .pattern("MXM")
            .pattern(" M ")
            .unlockedBy("mud_bricks", has(MUD_BRICKS))
            .save(output);
    }

    protected void tankRecipe(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(MISC, result)
            .define('M', GLASS)
            .define('X', DEEPSLATE)
            .define('Z', PACKED_MUD_CLAY.get())
            .pattern("XZX")
            .pattern("MMM")
            .pattern("XZX")
            .unlockedBy("glass", has(GLASS))
            .save(output);
    }

    protected void chaosCube(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(MISC, result)
            .define('X', ModTags.Items.WAND_TAGS)
            .define('M', PACKED_MUD_CLAY.get())
            .pattern("MMM")
            .pattern("MXM")
            .pattern("MMM")
            .unlockedBy("wand_item", has(ModTags.Items.WAND_TAGS))
            .save(output);
    }

    protected void runeManager(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(MISC, result)
            .define('X', AUGMENT_CORE.get())
            .define('M', PACKED_MUD_CLAY.get())
            .pattern("MMM")
            .pattern("MXM")
            .pattern("MMM")
            .unlockedBy("wand_item", has(RUNE.get()))
            .save(output);
    }

    protected void ticketBureau(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(MISC, result)
            .define('X', TRIAL_TICKET.get())
            .define('M', Tags.Items.STRIPPED_LOGS)
            .pattern("MMM")
            .pattern("MXM")
            .pattern("MMM")
            .unlockedBy("wand_item", has(RUNE.get()))
            .save(output);
    }

    protected void creator(RecipeOutput output, Item result) {
        ShapedRecipeBuilder.shaped(MISC, result)
            .define('M', PACKED_MUD_CLAY.get())
            .define('X', NEXITE_BLOCK.get())
            .pattern("MMM")
            .pattern(" X ")
            .pattern(" M ")
            .unlockedBy("mud_bricks", has(MUD_BRICKS))
            .save(output);
    }

    protected void nexite(RecipeOutput output, Item result) {
        nineBlockStorageRecipes(output, MISC, NEXITE_POWDER.get(), BUILDING_BLOCKS, NEXITE_BLOCK.get());
        oreSmelting(output, List.of(RAW_NEXITE_BLOCK.get()), BUILDING_BLOCKS, result, 2.0F, 200, "nexite");
    }

    protected void enchantedDiamond(RecipeOutput output) {
        nineBlockStorageRecipes(output, MISC, ENCHANTED_DIAMOND.get(), BUILDING_BLOCKS, ENCHANTED_DIAMOND_BLOCK.get());
    }

    protected void roseQuartz(RecipeOutput output) {
        nineBlockStorageRecipes(output, MISC, ROSE_QUARTZ.get(), BUILDING_BLOCKS, ROSE_QUARTZ_BLOCK.get());
    }

    protected void xpOrbs(RecipeOutput output) {
        var a = DIM_EXPERIENCE_ORB.get();
        var b = GLOWING_EXPERIENCE_ORB.get();
        var c = RADIANT_EXPERIENCE_ORB.get();
        var item = "experience_orb";
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
            output, RecipeCategory.MISC, b, RecipeCategory.BUILDING_BLOCKS, c, item + "_from_diamond_block", item
        );
        nineBlockStorageRecipesWithCustomPacking(
            output, RecipeCategory.MISC, a, RecipeCategory.MISC, b, item + "_from_nuggets", item
        );
    }

    protected void diamond(RecipeOutput output, Item result) {
        compressedBlocks(output, DIAMOND_NUGGET.get(), DIAMOND, DIAMOND_BLOCK, "diamond");
        List<ItemLike> ingredients = List.of(DIAMOND_SWORD, DIAMOND_AXE, DIAMOND_HOE, DIAMOND_PICKAXE, DIAMOND_SHOVEL, DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS);
        oreSmelting(output, ingredients, MISC, result, 2.0F, 200, "diamond");
    }

    protected void netheriteIngot(RecipeOutput output, Item result) {
        compressedBlocks(output, NETHERITE_NUGGET.get(), NETHERITE_INGOT, NETHERITE_BLOCK, "netherite_ingot");
        List<ItemLike> ingredients = List.of(NETHERITE_SWORD, NETHERITE_AXE, NETHERITE_HOE, NETHERITE_PICKAXE, NETHERITE_SHOVEL, NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS);
        oreSmelting(output, ingredients, MISC, result, 2.0F, 200, "netherite");
    }

    private void compressedBlocks(RecipeOutput output, Item a, Item b, Item c, String item){
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
            output, RecipeCategory.MISC, b, RecipeCategory.BUILDING_BLOCKS, c, item + "_from_diamond_block", item
        );
        nineBlockStorageRecipesWithCustomPacking(
            output, RecipeCategory.MISC, a, RecipeCategory.MISC, b, item + "_from_nuggets", item
        );
    }

}
