package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.utils.ModTags;

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

        this.tag(HEAD_ARMOR)
            .add(MAGE_HELMET.get())
            .add(BATTLEMAGE_HELMET.get())
            .add(KNIGHT_KING_HELMET.get())
            .add(WIZARD_HELMET.get())
            .add(ANCIENT_GOLEM_HELMET.get());

        this.tag(CHEST_ARMOR)
            .add(MAGE_CHESTPLATE.get())
            .add(BATTLEMAGE_CHESTPLATE.get())
            .add(KNIGHT_KING_CHESTPLATE.get())
            .add(WIZARD_CHESTPLATE.get())
            .add(ANCIENT_GOLEM_CHESTPLATE.get());

        this.tag(LEG_ARMOR)
            .add(MAGE_LEGGINGS.get())
            .add(BATTLEMAGE_LEGGINGS.get())
            .add(KNIGHT_KING_LEGGINGS.get())
            .add(WIZARD_LEGGINGS.get())
            .add(ANCIENT_GOLEM_LEGGINGS.get());

        this.tag(FOOT_ARMOR)
            .add(MAGE_BOOTS.get())
            .add(BATTLEMAGE_BOOTS.get())
            .add(KNIGHT_KING_BOOTS.get())
            .add(WIZARD_BOOTS.get())
            .add(ANCIENT_GOLEM_BOOTS.get());

        this.tag(DURABILITY_ENCHANTABLE)
            .add(BASIC_SHIELD.get())
            .add(UNDEAD_PROTECTOR_SHIELD.get())
            .add(TOME_OF_UNITY.get())
            .add(BATTLEMAGE_GAUNTLET.get())
            .add(MAGNET.get());

    }



}
