package org.jahdoo.trial_nexus.trading_post;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.armor.battle_mage.BattleMageArmor;
import org.jahdoo.common.items.armor.knight_king.KnightKingArmor;
import org.jahdoo.common.items.armor.mage.MageArmor;
import org.jahdoo.common.items.armor.wizard.WizardArmor;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.RuneReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.*;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.INFINITY;
import static org.jahdoo.common.registers.AttributeReg.replaceOrAddAttribute;
import static org.jahdoo.trial_nexus.loot.RewardLootTables.enchantmentWithChance;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.*;
import static org.jahdoo.trial_nexus.trading_post.ShoppingItems.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.listRandom;

public class ShoppingArmor {

    public static ItemStack getMageArmorPiece() {
        var mageArmor = List.of(
                new ItemStack(ItemReg.MAGE_HELMET),
                new ItemStack(ItemReg.MAGE_CHESTPLATE),
                new ItemStack(ItemReg.MAGE_LEGGINGS),
                new ItemStack(ItemReg.MAGE_BOOTS)
        );
        return listRandom(mageArmor);
    }

    public static ItemStack getWizardArmorPiece() {
        var mageArmor = List.of(
                new ItemStack(ItemReg.WIZARD_HELMET),
                new ItemStack(ItemReg.WIZARD_CHESTPLATE),
                new ItemStack(ItemReg.WIZARD_LEGGINGS),
                new ItemStack(ItemReg.WIZARD_BOOTS)
        );
        return listRandom(mageArmor);
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

    public static List<ItemStack> getAncientGolemAll() {
        return List.of(
            new ItemStack(ItemReg.ANCIENT_GOLEM_HELMET),
            new ItemStack(ItemReg.ANCIENT_GOLEM_CHESTPLATE),
            new ItemStack(ItemReg.ANCIENT_GOLEM_LEGGINGS),
            new ItemStack(ItemReg.ANCIENT_GOLEM_BOOTS)
        );
    }

    public static List<ItemStack> mageWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getMageArmorAll()) {
            attachMageData(jahdooRarity, itemStack);

            withGearData.add(itemStack);
        }

        return withGearData;
    }

    public static List<ItemStack> wizardWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getWizardArmorAll()) {
            attachWizardData(jahdooRarity, itemStack);
            withGearData.add(itemStack);
        }
        return withGearData;
    }

    public static List<ItemStack> battleMageWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getBattleMageAll()) {
            attachBattleMageData(jahdooRarity, itemStack);
            withGearData.add(itemStack);
        }

        return withGearData;
    }

    public static List<ItemStack> knightKingWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getKnightKingAll()) {

            attachKnightKingData(jahdooRarity, itemStack);
            withGearData.add(itemStack);
        }

        return withGearData;
    }

    public static List<ItemStack> ancientGolemWithData(JahdooRarity jahdooRarity){
        var withGearData = new ArrayList<ItemStack>();

        for (var itemStack : getAncientGolemAll()) {

            attachAncientGolemData(jahdooRarity, itemStack);
            withGearData.add(itemStack);
        }

        return withGearData;
    }

    private static void attachWizardData(JahdooRarity jahdooRarity, ItemStack itemStack) {
        if(itemStack.getItem() instanceof ArmorItem armorItem){
            var getAllAllowed = RuneReg.runesWithoutCategoryAndRarity(PERK, RESILIENCE, INFINITY);

            addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), jahdooRarity, listRandom(getAllAllowed));
            addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), jahdooRarity, listRandom(getAllAllowed));
        }

        sharedArmorData(jahdooRarity, itemStack, 1, 0, -50);
    }

    private static void attachMageData(JahdooRarity jahdooRarity, ItemStack itemStack) {
        var i = jahdooRarity.getId() + 1;
        if(itemStack.getItem() instanceof ArmorItem armorItem){
            if(org.shaydee.shaydeeapi.Maths.percentageChance(10 * i)){
                var tier = getRarity(List.of(Pair.of(COMMON, 1), Pair.of(RARE, 5000)));
                var getAllAllowed = RuneReg.runesWithoutCategoryAndRarity(PERK, RESILIENCE, INFINITY, COSMIC);
                if(!getAllAllowed.isEmpty()){
                    addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), tier, listRandom(getAllAllowed));
                }
            }
        }

        var attachRuneSlots = org.shaydee.shaydeeapi.Maths.percentageChance(10 * i) ? 1 : 0;
        sharedArmorData(jahdooRarity, itemStack, attachRuneSlots, -15, -50);
    }

    private static void attachBattleMageData(JahdooRarity jahdooRarity, ItemStack itemStack) {
        if(itemStack.getItem() instanceof ArmorItem armorItem){
            var getAllAllowed = RuneReg.runesWithoutCategoryAndRarity(INFINITY, COSMIC);
            addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), jahdooRarity, listRandom(getAllAllowed));

            if(org.shaydee.shaydeeapi.Maths.percentageChance(5 * (jahdooRarity.getId()+1))){
                addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), jahdooRarity, listRandom(getAllAllowed));
            }
        }

        sharedArmorData(jahdooRarity, itemStack, 1, -20, -30);
    }

    private static void attachKnightKingData(JahdooRarity jahdooRarity, ItemStack itemStack) {
        if(itemStack.getItem() instanceof ArmorItem armorItem){
            if(org.shaydee.shaydeeapi.Maths.percentageChance(5 * (jahdooRarity.getId()+1))){
                addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), jahdooRarity, RuneReg.RESILIENCE.get());
            }
        }

        sharedArmorData(jahdooRarity, itemStack, 2, -50, 0);
    }

    private static void attachAncientGolemData(JahdooRarity jahdooRarity, ItemStack itemStack) {
        if(itemStack.getItem() instanceof ArmorItem armorItem){
            addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), jahdooRarity, RuneReg.RESILIENCE.get());
            addSpecificAttribute(itemStack, armorItem.getEquipmentSlot(), jahdooRarity, RuneReg.STRIKER.get());
            var movementSpeed = Attributes.MOVEMENT_SPEED;
            replaceOrAddAttribute(itemStack, movementSpeed.getRegisteredName(), movementSpeed, -0.01, armorItem.getEquipmentSlot(), true, "bonus");
        }

        sharedArmorData(jahdooRarity, itemStack, 2, -50, 0);
    }

    private static void sharedArmorData(
        JahdooRarity jahdooRarity,
        ItemStack itemStack,
        int runeSlots,
        int adjustPotential,
        int adjustDurability
    ) {
        var isUnique = jahdooRarity == UNIQUE;
        var getRuneSlots = isUnique ? runeSlots + 1 : runeSlots;
        var repairSlots = isUnique ? Random.nextInt(4, 7) : -1;
        attachSharedProperties(itemStack, getRuneSlots, jahdooRarity, repairSlots, adjustPotential, adjustDurability);
        if(isUnique) preInsertRunes(itemStack);
    }

    public static void enchantArmorItem(
        ServerLevel serverLevel,
        ItemStack itemStack,
        ArmorItem armorItem,
        JahdooRarity jahdooRarity
    ) {
        var slot = armorItem.getEquipmentSlot();
        var isSpecial = jahdooRarity == UNIQUE;

        if (org.shaydee.shaydeeapi.Maths.percentageChance(isSpecial ? 60 : 10)) return;

        enchantmentWithChance(itemStack, serverLevel, BLAST_PROTECTION, 5, 10, isSpecial);
        enchantmentWithChance(itemStack, serverLevel, PROJECTILE_PROTECTION, 5, 10, isSpecial);
        enchantmentWithChance(itemStack, serverLevel, PROTECTION, 5, 10, isSpecial);
        enchantmentWithChance(itemStack, serverLevel, UNBREAKING, 4, 10, isSpecial);

        if(slot == FEET){
            enchantmentWithChance(itemStack, serverLevel, SOUL_SPEED, 4, 9, isSpecial);
            enchantmentWithChance(itemStack, serverLevel, DEPTH_STRIDER, 4, 9, isSpecial);
            enchantmentWithChance(itemStack, serverLevel, FEATHER_FALLING, 5, 10, isSpecial);
        }

        if(slot == LEGS) enchantmentWithChance(itemStack, serverLevel, SWIFT_SNEAK, 4, 9, isSpecial);
        if(slot == HEAD) enchantmentWithChance(itemStack, serverLevel, RESPIRATION, 4, 9, isSpecial);
    }

    public static void attachCustomArmorData(ServerLevel serverLevel, ItemStack itemStack, JahdooRarity jahdooRarity) {
        switch (itemStack.getItem()){
            case KnightKingArmor ignored -> attachKnightKingData(jahdooRarity, itemStack);
            case MageArmor ignored -> attachMageData(jahdooRarity, itemStack);
            case BattleMageArmor ignored -> attachBattleMageData(jahdooRarity, itemStack);
            case WizardArmor ignored -> attachWizardData(jahdooRarity, itemStack);
            default -> {/* IGNORED */}
        }

        if(!(itemStack.getItem() instanceof ArmorItem armorItem)) return;
        enchantArmorItem(serverLevel, itemStack, armorItem, jahdooRarity);
    }

}
