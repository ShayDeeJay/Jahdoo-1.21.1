package org.jahdoo.common.components;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.wand.WandData;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.ascension.utils.Helpers;

import java.util.Map;

import static org.jahdoo.common.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.common.registers.DataComponentRegistry.WAND_DATA;

public class DataComponentHelper {

    public static void setAbilityTypeItemStack(ItemStack itemStack, String ability) {
        itemStack.update(WAND_DATA.get(), WandData.DEFAULT, data -> data.setSelectedAbility(ability));
    }

    public static boolean hasWandAbilitiesTag(ItemStack itemStack){
        return itemStack.get(WAND_ABILITY_HOLDER.get()) != null;
    }

    public static String getAbilityTypeItemStack(ItemStack itemStack) {
        var wandData = itemStack.get(WAND_DATA.get());
        if(wandData != null) return wandData.selectedAbility();

        return "";
    }

    public static String getKeyFromAugment(ItemStack itemStack){
        var abilityHolder = itemStack.get(WAND_ABILITY_HOLDER);
        if(abilityHolder != null){
            var name = abilityHolder.abilityProperties().keySet().stream().findFirst();
            if(name.isPresent()) return name.get();
        }

        return "";
    }

    public static void setAbilityTypeWand(Player player, String ability) {
        if(player == null) return;
        var item = Helpers.getUsedItem(player);
        if(item.getItem() instanceof WandItem){
            item.update(WAND_DATA.get(), WandData.DEFAULT, data -> data.setSelectedAbility(ability));
        }
    }

    public static Map<String, AbilityHolder.AbilityModifiers> getSpecificValue(Player player){
        var abilityName = DataComponentHelper.getAbilityTypeWand(player);
        var wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
        var allModifiers = wandAbilityHolder.abilityProperties().get(abilityName.getPath().intern());

        return allModifiers.abilityProperties();
    }

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

    public static double getSpecificValue(String name, WandAbilityHolder abilityHolder, String modifier){
        if(abilityHolder == null) return 0;
        var allModifiers = abilityHolder.abilityProperties().get(name);

        if(allModifiers == null) return 0;
        var specificValue = allModifiers.abilityProperties().get(modifier);

        if(specificValue == null) return 0;
        return specificValue.setValue();
    }

    public static ResourceLocation getAbilityTypeWand(Player player) {
        var res = Helpers.res("");
        if(player == null) return res;
        var itemInHand = Helpers.getUsedItem(player);

        if (!(itemInHand.getItem() instanceof WandItem)) return res;
        var abilityName = itemInHand.get(WAND_DATA.get()).selectedAbility();

        if (abilityName == null) return res;
        return Helpers.res(abilityName);
    }
}
