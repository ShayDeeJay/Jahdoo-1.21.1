package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.ascension.utils.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.ItemTags.*;
import static org.jahdoo.common.registers.ItemsRegister.*;

public class ModItemTagGenerator extends ItemTagsProvider {


    public ModItemTagGenerator(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> lookupProvider,
        CompletableFuture<TagLookup<Block>> blockTags,
        ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags,JahdooMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Items.WAND_TAGS)
            .add(WAND_ITEM_INFERNO.get())
            .add(WAND_ITEM_MYSTIC.get())
            .add(WAND_ITEM_FROST.get())
            .add(WAND_ITEM_VITALITY.get());

        this.tag(SWORDS)
            .add(INGMAS_SWORD.get());

        this.tag(HEAD_ARMOR_ENCHANTABLE)
            .add(WIZARD_HELMET.get())
            .add(MAGE_HELMET.get());

        this.tag(CHEST_ARMOR_ENCHANTABLE)
            .add(WIZARD_CHESTPLATE.get())
            .add(MAGE_CHESTPLATE.get());

        this.tag(LEG_ARMOR_ENCHANTABLE)
            .add(WIZARD_CHESTPLATE.get())
            .add(MAGE_LEGGINGS.get());

        this.tag(FOOT_ARMOR_ENCHANTABLE)
            .add(WIZARD_BOOTS.get())
            .add(MAGE_BOOTS.get());
    }



}
