package org.jahdoo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {


    public ModItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags,JahdooMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Items.WAND_TAGS)
            .add(ItemsRegister.WAND_ITEM_INFERNO.get())
            .add(ItemsRegister.WAND_ITEM_MYSTIC.get())
            .add(ItemsRegister.WAND_ITEM_FROST.get())
            .add(ItemsRegister.WAND_ITEM_LIGHTNING.get())
            .add(ItemsRegister.WAND_ITEM_VITALITY.get());

        this.tag(ItemTags.SWORDS)
                .add(ItemsRegister.INGMAS_SWORD.get());

        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
            .add(ItemsRegister.WIZARD_HELMET.get())
            .add(ItemsRegister.MAGE_HELMET.get());

        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
            .add(ItemsRegister.WIZARD_CHESTPLATE.get())
            .add(ItemsRegister.MAGE_CHESTPLATE.get());

        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
            .add(ItemsRegister.WIZARD_CHESTPLATE.get())
            .add(ItemsRegister.MAGE_LEGGINGS.get());

        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
            .add(ItemsRegister.WIZARD_BOOTS.get())
            .add(ItemsRegister.MAGE_BOOTS.get());
    }



}
