package org.jahdoo.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public class EnchantmentHelpers {

    @Nullable
    private static Holder<Enchantment> enchantmentFromKey(RegistryAccess registryAccess, ResourceKey<Enchantment> enchantmentkey) {
        var reg = registryAccess.registry(Registries.ENCHANTMENT).orElse(null);
        if (reg != null) {
            var enchantment = reg.get(enchantmentkey);
            if (enchantment != null) {
                return reg.wrapAsHolder(enchantment);
            }
        }
        return null;
    }

    public static void enchant(ItemStack stack, RegistryAccess access, ResourceKey<Enchantment> enchantmentKey, int level) {
        var enchantment = enchantmentFromKey(access, enchantmentKey);
        if (enchantment != null) stack.enchant(enchantment, level);
    }

}
