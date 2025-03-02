package org.jahdoo.ascension.trading_post;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.item.enchantment.Enchantments.*;
import static net.minecraft.world.item.enchantment.Enchantments.UNBREAKING;
import static org.jahdoo.ascension.RewardLootTables.attachEnchantment;
import static org.jahdoo.ascension.utils.Helpers.Random;

public class ShoppingWeapon {

    public static ItemStack getElementalSword() {
        var sword = new ItemStack(ItemReg.ELEMENTAL_SWORD);
        sword.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(Random.nextInt(1,5)));
        return sword;
    }

    public static void enchantSword(ServerLevel serverLevel, ItemStack itemStack, boolean isSpecial) {
        if(!isSpecial) if(Random.nextInt(50) != 0) return;
        attachEnchantment(itemStack, serverLevel, SHARPNESS, 6, 11, isSpecial);
        attachEnchantment(itemStack, serverLevel, SWEEPING_EDGE, 4, 8, isSpecial);
        attachEnchantment(itemStack, serverLevel, LOOTING, 4, 8, isSpecial);
        attachEnchantment(itemStack, serverLevel, UNBREAKING, 4, 8, isSpecial);
    }

}
