package org.jahdoo.trial_nexus.trading_post;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.registers.ItemReg;

import javax.annotation.Nullable;

import static net.minecraft.world.item.enchantment.Enchantments.*;
import static org.jahdoo.trial_nexus.loot.RewardLootTables.attachEnchantment;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class ShoppingWeapon {

    public static ItemStack getElementalSword(@Nullable ItemStack stack) {
        var sword = stack == null ? new ItemStack(ItemReg.ELEMENTAL_SWORD) : stack;
        sword.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(Random.nextInt(0,4)));
        return sword;
    }

    public static void enchantSword(ServerLevel serverLevel, ItemStack itemStack, boolean isSpecial) {
        if(org.shaydee.shaydeeapi.Maths.percentageChance(20)) attachEnchantment(itemStack, serverLevel, SWEEPING_EDGE, 1, 4);
        if(org.shaydee.shaydeeapi.Maths.percentageChance(30)) attachEnchantment(itemStack, serverLevel, SHARPNESS, 1, 6);
        if(org.shaydee.shaydeeapi.Maths.percentageChance(30)) attachEnchantment(itemStack, serverLevel, UNBREAKING, 1, 4);
        if(org.shaydee.shaydeeapi.Maths.percentageChance(40)) attachEnchantment(itemStack, serverLevel, LOOTING, 1, 4);
        if(org.shaydee.shaydeeapi.Maths.percentageChance(40)) attachEnchantment(itemStack, serverLevel, MENDING, 0, 1);

        if(!isSpecial) return;
        if(org.shaydee.shaydeeapi.Maths.percentageChance(20)) attachEnchantment(itemStack, serverLevel, SHARPNESS, 6, 11);
        if(org.shaydee.shaydeeapi.Maths.percentageChance(30)) attachEnchantment(itemStack, serverLevel, SWEEPING_EDGE, 4, 8);
        if(org.shaydee.shaydeeapi.Maths.percentageChance(30)) attachEnchantment(itemStack, serverLevel, LOOTING, 4, 8);
        if(org.shaydee.shaydeeapi.Maths.percentageChance(40)) attachEnchantment(itemStack, serverLevel, UNBREAKING, 4, 8);
    }

}
