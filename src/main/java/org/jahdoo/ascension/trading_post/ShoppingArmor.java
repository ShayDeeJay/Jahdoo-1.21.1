package org.jahdoo.ascension.trading_post;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static org.jahdoo.ascension.RewardLootTables.attachEnchantment;
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
            RuneHolder.createNewRuneSlots(itemStack, runeSlots, refinementPotential);
        }

        if (Random.nextInt(isSpecial ? 20 : 50) != 0) return;

        attachEnchantment(itemStack, serverLevel, BLAST_PROTECTION, 5, 10, isSpecial);
        attachEnchantment(itemStack, serverLevel, PROJECTILE_PROTECTION, 5, 10, isSpecial);
        attachEnchantment(itemStack, serverLevel, PROTECTION, 5, 10, isSpecial);
        attachEnchantment(itemStack, serverLevel, UNBREAKING, 4, 9, isSpecial);

        if(slot == FEET){
            attachEnchantment(itemStack, serverLevel, SOUL_SPEED, 4, 9, isSpecial);
            attachEnchantment(itemStack, serverLevel, DEPTH_STRIDER, 4, 9, isSpecial);
            attachEnchantment(itemStack, serverLevel, FEATHER_FALLING, 5, 10, isSpecial);
        }
        if(slot == LEGS) attachEnchantment(itemStack, serverLevel, SWIFT_SNEAK, 4, 9, isSpecial);
        if(slot == HEAD) attachEnchantment(itemStack, serverLevel, RESPIRATION, 4, 9, isSpecial);
    }

}
