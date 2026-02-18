package org.jahdoo.trial_nexus.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class EnchantmentHelpers {

    public static void randomApplicableEnchantment(ItemStack itemStack) {
        EnchantmentHelper.selectEnchantment(RandomSource.create(), itemStack, JahdooHelpers.Random.nextInt(0, 5), Stream.<Holder<Enchantment>>builder().build());
    }

    public static void enchant(ItemStack stack, RegistryAccess access, ResourceKey<Enchantment> enchantmentKey, int level) {
        var enchantment = enchantmentFromKey(access, enchantmentKey);
        if (enchantment != null) {
            if(stack.is(ItemReg.OVERENCHANTED_BOOK)){
                var iEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                iEnchantments.set(enchantment, level);
                stack.set(DataComponents.STORED_ENCHANTMENTS, iEnchantments.toImmutable());
            } else {
                stack.enchant(enchantment, level);
            }
        }
    }

    @Nullable
    private static Holder<Enchantment> enchantmentFromKey(RegistryAccess registryAccess, ResourceKey<Enchantment> enchantmentKey) {
        var reg = registryAccess.registry(Registries.ENCHANTMENT).orElse(null);
        if (reg != null) {
            var enchantment = reg.get(enchantmentKey);
            if (enchantment != null) {
                return reg.wrapAsHolder(enchantment);
            }
        }
        return null;
    }

    public static boolean applyBookEnchantsToItem(ItemStack enchantedBook, ItemStack enchantableItem, boolean checkOnly){
        var lookUp = enchantedBook.getComponents().get(DataComponents.STORED_ENCHANTMENTS);
        if(lookUp == null) return false;
        var canApplyAnyEnchants = false;

        for (var enchant : lookUp.entrySet()) {
            if(enchant.getKey().value().canEnchant(enchantableItem)){
                if(!checkOnly) enchantableItem.enchant(enchant.getKey(), enchant.getIntValue());
                canApplyAnyEnchants = true;
            }
        }

        return canApplyAnyEnchants;
    }

}
