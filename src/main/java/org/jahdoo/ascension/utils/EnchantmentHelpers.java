package org.jahdoo.ascension.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.item.enchantment.EnchantmentHelper.getAvailableEnchantmentResults;
import static net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction.randomEnchantment;

public class EnchantmentHelpers {

    public static void enchant(ItemStack stack, RegistryAccess access, ResourceKey<Enchantment> enchantmentKey, int level) {
        var enchantment = enchantmentFromKey(access, enchantmentKey);
        if (enchantment != null) stack.enchant(enchantment, level);
    }

    public static EnchantRandomlyFunction.Builder randomApplicableEnchantment(HolderLookup.Provider registries) {
        var enchant = randomEnchantment();
        for (int i = 0; i < 10; i++) enchant.withOneOf(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
        return enchant;
    }

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

    public static List<Enchantments> getItemsValidEnchantments(ItemStack itemStack, ServerLevel serverLevel){
        //Find valid enchantment for item
        var optional = serverLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).entrySet();
        var availableEnchantmentResults = getAvailableEnchantmentResults(50, itemStack, optional.stream().map(s -> Holder.direct(s.getValue())));
        var newA = availableEnchantmentResults.stream().filter(s -> !s.enchantment.value().description().getString().contains("Mending")).toList();
        for (var availableEnchantmentResult : newA) {
            System.out.println(availableEnchantmentResult.enchantment.value());
        }
        return new ArrayList<>();
    }

}
