package org.jahdoo.components;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.items.wand.WandData;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.ModHelpers;

import java.util.Map;

import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class DataComponentHelper {

    public static double getSpecificValue(Player player, ItemStack itemStack, String modifier){
        var abilityName = DataComponentHelper.getAbilityTypeWand(player);
        var wandAbilityHolder = itemStack.get(WAND_ABILITY_HOLDER.get());
        var allModifiers = wandAbilityHolder.abilityProperties().get(abilityName.getPath().intern());
        if(allModifiers.abilityProperties().containsKey(modifier)){
            var specificValue = allModifiers.abilityProperties().get(modifier);
            return specificValue.setValue();
        }
        return 0;
    }

    public static double getSpecificValue(String name, WandAbilityHolder wandAbilityHolder, String modifier){
        if(wandAbilityHolder != null){
            var allModifiers = wandAbilityHolder.abilityProperties().get(name);
            if(allModifiers != null){
                var specificValue = allModifiers.abilityProperties().get(modifier);
                if(specificValue != null){
                    return specificValue.setValue();
                }
            }
        }
        return 0;
    }

    public static Map<String, AbilityHolder.AbilityModifiers> getSpecificValue(Player player){
        var abilityName = DataComponentHelper.getAbilityTypeWand(player);
        var wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
        var allModifiers = wandAbilityHolder.abilityProperties().get(abilityName.getPath().intern());
        return allModifiers.abilityProperties();
    }

    public static boolean hasWandAbilitiesTag(ItemStack itemStack){
        return itemStack.get(WAND_ABILITY_HOLDER.get()) != null;
    }

    public static ResourceLocation getAbilityTypeWand(Player player) {
        if(player != null) {
            var itemInHand = ModHelpers.getUsedItem(player);
            if (itemInHand.getItem() instanceof WandItem) {
                String abilityName = itemInHand.get(WAND_DATA.get()).selectedAbility();
                if (abilityName != null) {
                    return ModHelpers.res(abilityName);
                }
            }
        }
        return ModHelpers.res("");
    }

    public static String getKeyFromAugment(ItemStack itemStack){
        if(itemStack.has(WAND_ABILITY_HOLDER)){
            var name = itemStack.get(WAND_ABILITY_HOLDER).abilityProperties().keySet().stream().findFirst();
            if(name.isPresent()) return name.get();
        }
        return "";
    }

    public static void setAbilityTypeWand(Player player, String ability) {
        if(player == null) return;
        var item = ModHelpers.getUsedItem(player);
        if(item.getItem() instanceof WandItem){
            item.update(WAND_DATA.get(), WandData.DEFAULT, data -> data.setSelectedAbility(ability));
        }
    }

    public static void setAbilityTypeItemStack(ItemStack itemStack, String ability) {
        itemStack.update(WAND_DATA.get(), WandData.DEFAULT, data -> data.setSelectedAbility(ability));
    }

    public static String getAbilityTypeItemStack(ItemStack itemStack) {
        if(itemStack.has(WAND_DATA.get())){
            return itemStack.get(WAND_DATA.get()).selectedAbility();
        }
        return "";
    }
}
