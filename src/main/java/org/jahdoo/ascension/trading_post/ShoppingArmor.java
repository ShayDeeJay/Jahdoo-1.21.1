package org.jahdoo.ascension.trading_post;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.RuneReg;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;
import static org.jahdoo.ascension.rarity.JahdooRarity.getRarity;
import static org.jahdoo.ascension.trading_post.RewardLootTables.attachEnchantmentWithChance;
import static org.jahdoo.ascension.utils.Helpers.Random;

public class ShoppingArmor {

    public static ItemStack getMageArmorPiece() {
        var mageArmor = List.of(
                new ItemStack(ItemReg.MAGE_HELMET),
                new ItemStack(ItemReg.MAGE_CHESTPLATE),
                new ItemStack(ItemReg.MAGE_LEGGINGS),
                new ItemStack(ItemReg.MAGE_BOOTS)
        );
        return Helpers.listRandom(mageArmor);
    }

    public static ItemStack getWizardArmorPiece() {
        var mageArmor = List.of(
                new ItemStack(ItemReg.WIZARD_HELMET),
                new ItemStack(ItemReg.WIZARD_CHESTPLATE),
                new ItemStack(ItemReg.WIZARD_LEGGINGS),
                new ItemStack(ItemReg.WIZARD_BOOTS)
        );
        return Helpers.listRandom(mageArmor);
    }

    public static List<ItemStack> getMageArmorAll() {
        return List.of(
            new ItemStack(ItemReg.MAGE_HELMET),
            new ItemStack(ItemReg.MAGE_CHESTPLATE),
            new ItemStack(ItemReg.MAGE_LEGGINGS),
            new ItemStack(ItemReg.MAGE_BOOTS)
        );
    }

    public static List<ItemStack> getWizardArmorAll() {
        return List.of(
            new ItemStack(ItemReg.WIZARD_HELMET),
            new ItemStack(ItemReg.WIZARD_CHESTPLATE),
            new ItemStack(ItemReg.WIZARD_LEGGINGS),
            new ItemStack(ItemReg.WIZARD_BOOTS)
        );
    }

    public static List<ItemStack> getBattleMageAll() {
        return List.of(
            new ItemStack(ItemReg.BATTLEMAGE_HELMET),
            new ItemStack(ItemReg.BATTLEMAGE_CHESTPLATE),
            new ItemStack(ItemReg.BATTLEMAGE_LEGGINGS),
            new ItemStack(ItemReg.BATTLEMAGE_BOOTS)
        );
    }


    public static List<ItemStack> getKnightKingAll() {
        return  List.of(
            new ItemStack(ItemReg.KNIGHT_KING_HELMET),
            new ItemStack(ItemReg.KNIGHT_KING_CHESTPLATE),
            new ItemStack(ItemReg.KNIGHT_KING_LEGGINGS),
            new ItemStack(ItemReg.KNIGHT_KING_BOOTS)
        );
    }

    public static List<ItemStack> mageWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getMageArmorAll()) {
            ShoppingItems.attachSharedProperties(itemStack, Maths.percentageChance(30) ? 1 : 0, jahdooRarity, -1, -15, -65);
            if(itemStack.getItem() instanceof ArmorItem armorItem){
                if(Maths.percentageChance(30)){
                    var rarity = getRarity(List.of(Pair.of(COMMON, 1), Pair.of(RARE, 5000)));
                    var tier = getRarity(List.of(Pair.of(COMMON, 1), Pair.of(RARE, 5000)));
                    ShoppingItems.addAttribute(itemStack, armorItem.getEquipmentSlot(), rarity, tier);
                }
            }
            withGearData.add(itemStack);
        }

        return withGearData;
    }

    public static List<ItemStack> wizardWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getWizardArmorAll()) {
            ShoppingItems.attachSharedProperties(itemStack, 1, jahdooRarity, -1, 0, -50);
            if(itemStack.getItem() instanceof ArmorItem armorItem){
                ShoppingItems.addAttribute(itemStack, armorItem.getEquipmentSlot(), null, jahdooRarity);
                ShoppingItems.addAttribute(itemStack, armorItem.getEquipmentSlot(), null, jahdooRarity);
            }
            withGearData.add(itemStack);
        }

        return withGearData;
    }

    public static List<ItemStack> battleMageWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getBattleMageAll()) {
            ShoppingItems.attachSharedProperties(itemStack, 1, jahdooRarity, -1, -20, -30);
            if(itemStack.getItem() instanceof ArmorItem armorItem){
                var rarity = getRarity(List.of(Pair.of(COMMON, 1), Pair.of(RARE, 1000), Pair.of(EPIC, 5000)));
                ShoppingItems.addAttribute(itemStack, armorItem.getEquipmentSlot(), rarity, jahdooRarity);

                if(Maths.percentageChance(40)){
                    var rarity1 = getRarity(List.of(Pair.of(COMMON, 1), Pair.of(RARE, 1000), Pair.of(EPIC, 5000)));
                    ShoppingItems.addAttribute(itemStack, armorItem.getEquipmentSlot(), rarity1, jahdooRarity);
                }
            }
            withGearData.add(itemStack);
        }

        return withGearData;
    }

    public static List<ItemStack> knightKingWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getKnightKingAll()) {
            ShoppingItems.attachSharedProperties(itemStack, 2, jahdooRarity, -1, -50, 0);

            if(itemStack.getItem() instanceof ArmorItem armorItem){
                if(Maths.percentageChance(30)){
                    ShoppingItems.addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), jahdooRarity, RuneReg.RESILIENCE.get());
                }
            }

            withGearData.add(itemStack);
        }

        return withGearData;
    }


    public static void enchantArmorItem(
        ServerLevel serverLevel,
        ItemStack itemStack,
        ArmorItem armorItem,
        boolean isSpecial
    ) {
        var runeSlots = Random.nextInt(3);
        var slot = armorItem.getEquipmentSlot();

        if(runeSlots > 0){
            var refinementPotential = Random.nextInt(80, 320);
            RuneHolder.createNewRuneSlots(itemStack, runeSlots, JahdooRarity.getRarity().getId() + 1, refinementPotential);
        }

        if (Random.nextInt(isSpecial ? 20 : 50) != 0) return;

        attachEnchantmentWithChance(itemStack, serverLevel, BLAST_PROTECTION, 5, 10, isSpecial);
        attachEnchantmentWithChance(itemStack, serverLevel, PROJECTILE_PROTECTION, 5, 10, isSpecial);
        attachEnchantmentWithChance(itemStack, serverLevel, PROTECTION, 5, 10, isSpecial);
        attachEnchantmentWithChance(itemStack, serverLevel, UNBREAKING, 4, 9, isSpecial);

        if(slot == FEET){
            attachEnchantmentWithChance(itemStack, serverLevel, SOUL_SPEED, 4, 9, isSpecial);
            attachEnchantmentWithChance(itemStack, serverLevel, DEPTH_STRIDER, 4, 9, isSpecial);
            attachEnchantmentWithChance(itemStack, serverLevel, FEATHER_FALLING, 5, 10, isSpecial);
        }

        if(slot == LEGS) attachEnchantmentWithChance(itemStack, serverLevel, SWIFT_SNEAK, 4, 9, isSpecial);
        if(slot == HEAD) attachEnchantmentWithChance(itemStack, serverLevel, RESPIRATION, 4, 9, isSpecial);
    }

}
