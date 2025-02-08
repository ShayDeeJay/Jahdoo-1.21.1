package org.jahdoo.challenge.trading_post;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.world.item.enchantment.Enchantments.*;
import static net.minecraft.world.item.enchantment.Enchantments.UNBREAKING;
import static org.jahdoo.challenge.RewardLootTables.attachEnchantment;
import static org.jahdoo.utils.ModHelpers.Random;

public class ShoppingWeapon {

    public static @NotNull ItemStack getElementalSword() {
        var sword = new ItemStack(ItemsRegister.ELEMENTAL_SWORD);
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
