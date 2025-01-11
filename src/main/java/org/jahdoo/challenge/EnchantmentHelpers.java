package org.jahdoo.challenge;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction.randomEnchantment;

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

    public static EnchantRandomlyFunction.Builder randomApplicableEnchantment(HolderLookup.Provider registries) {
        var enchant = randomEnchantment();
        for (int i = 0; i < 10; i++) enchant.withOneOf(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
        return enchant;
    }
}
