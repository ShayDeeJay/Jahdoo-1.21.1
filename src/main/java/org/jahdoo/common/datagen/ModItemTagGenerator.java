package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.utils.ModTags;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.ItemTags.*;
import static org.jahdoo.common.registers.ItemReg.*;

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
        this.tag(ModTags.Items.AUGMENT_CORE)
            .add(RECOVERY_RECEIPT.get());

        this.tag(ModTags.Items.ESSENCE_FRAGMENT)
            .add(RUNE.get());

        this.tag(ModTags.Items.WAND_TAGS)
            .add(STARTER_WAND.get())
            .add(WAND_ITEM_INFERNO.get())
            .add(WAND_ITEM_MYSTIC.get())
            .add(WAND_ITEM_FROST.get())
            .add(WAND_ITEM_VITALITY.get());

        this.tag(SWORDS)
            .add(ELEMENTAL_SWORD.value())
            .add(ANCIENT_GLAIVE.value())
            .add(INGMAS_SWORD.get());

    }



}
