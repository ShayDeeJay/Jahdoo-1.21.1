package org.jahdoo.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.armor.WizardArmor;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.TomeOfUnity;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.ItemsRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.jahdoo.ability.rarity.JahdooRarity.*;

public class CreatorRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final int craftingCost;

    public CreatorRecipe(NonNullList<Ingredient> inputItems, ItemStack output, int craftingCost) {
        this.inputItems = inputItems;
        this.output = output;
        this.craftingCost = craftingCost;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        var recipeItems = new ArrayList<String>();
        var inventoryItem = new ArrayList<String>();

        for (Ingredient inputItem : inputItems) {
            List<ItemStack> small = List.of(inputItem.getItems());
            for (ItemStack itemStack : small) {
                recipeItems.add(itemStack.getDescriptionId());
            }
        }

        for(int i = 0; i < pInput.size(); i++){
            if(!pInput.getItem(i).isEmpty()){
                inventoryItem.add((pInput.getItem(i)).getDescriptionId());
            }
        }

        Collections.sort(recipeItems);
        Collections.sort(inventoryItem);
        return recipeItems.equals(inventoryItem);
    }

    public int getCraftingCost(){
        return this.craftingCost;
    }

    private ItemStack randomAugment(){
        var stack = new ItemStack(ItemsRegister.AUGMENT_ITEM);
        return stack;
    }

    public ItemStack getItemWithData(){
        var asItem = this.output.getItem();
        return switch (asItem) {
            case Augment ignored -> randomAugment();
            case WandItem ignored -> setGeneratedWand(JahdooRarity.getRarity(), asItem);
            case TomeOfUnity ignored -> setGeneratedTome(COMMON, asItem);
            default -> this.output.copy();
        };
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pRegistries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return getItemWithData();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CREATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CREATOR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CreatorRecipe> {
        public static final MapCodec<CreatorRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Ingredient.CODEC_NONEMPTY
                    .listOf()
                    .fieldOf("ingredients")
                    .xmap(
                        ingredients -> {
                            Ingredient[] aingredient = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                            return NonNullList.of(Ingredient.EMPTY, aingredient);
                        },
                        Function.identity()
                    )
                    .forGetter(creatorRecipe -> creatorRecipe.inputItems),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.INT.fieldOf("cost").forGetter(creatorRecipe -> creatorRecipe.craftingCost)

            ).apply(instance, CreatorRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CreatorRecipe> STREAM_CODEC = StreamCodec.of(
            Serializer::toNetwork,  // Function for writing to the network buffer
            Serializer::fromNetwork  // Function for reading from the network buffer
        );

        private static CreatorRecipe fromNetwork(RegistryFriendlyByteBuf byteBuf) {
            int i = byteBuf.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(byteBuf));
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(byteBuf);
            int j = byteBuf.readVarInt();

            return new CreatorRecipe(nonnulllist, itemstack, j);
        }

        private static void toNetwork(RegistryFriendlyByteBuf byteBuf, CreatorRecipe creatorRecipe) {
            byteBuf.writeVarInt(creatorRecipe.inputItems.size());
            for (Ingredient ingredient : creatorRecipe.inputItems) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(byteBuf, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(byteBuf, creatorRecipe.output);
            byteBuf.writeVarInt(creatorRecipe.craftingCost);
        }

        @Override
        public MapCodec<CreatorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CreatorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}