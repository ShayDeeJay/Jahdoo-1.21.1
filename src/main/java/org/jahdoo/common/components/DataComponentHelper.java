package org.jahdoo.common.components;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.common.items.wand.WandData;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.Map;

import static org.jahdoo.common.registers.ComponentReg.*;

public class DataComponentHelper {

    public static void setAbilityTypeItemStack(ItemStack itemStack, String ability) {
        itemStack.update(WAND_DATA.get(), WandData.DEFAULT, data -> data.setSelectedAbility(ability));
    }

    public static boolean hasWandAbilitiesTag(ItemStack itemStack){
        return itemStack.get(ABILITY_HOLDER.get()) != null;
    }

    public static String getAbilityTypePlayerString(Player player) {
        return player.getData(AttachmentReg.CASTER_DATA).getSelectedAbility();
    }

    public static String getAbilityTypeItemStack(ItemStack itemStack) {
        var wandData = itemStack.get(WAND_DATA.get());
        if(wandData != null) return wandData.selectedAbility();

        return "";
    }

    public static String getKeyFromAugment(ItemStack itemStack){
        var abilityHolder = itemStack.get(ABILITY_HOLDER);
        if(abilityHolder != null){
            return abilityHolder.abilityName();
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

    public static Map<String, AbilityData.AbilityModifiers> getSpecificValue(Player player){
        var wandAbilityHolder = AbilityHolder.getHolderFromWand(player);
        return wandAbilityHolder.data().abilityProperties();
    }


    public static double getSpecificValue(Player player, String modifier){
        var getValue = CastingData.entityHolderWithSelected(player).data().abilityProperties().get(modifier);
        return getValue != null ? getValue.setValue() : 0;
    }

//    public static double getSpecificValue(Player player, ItemStack itemStack, String modifier){
//        var wandAbilityHolder = itemStack.get(ABILITY_HOLDER.get());
//
//        if(wandAbilityHolder != null){
//            var specificValue = wandAbilityHolder.data().abilityProperties().get(modifier);
//            return specificValue.setValue();
//        }
//
//        return 0;
//    }

    public static double getSpecificValue(AbilityHolder abilityHolder, String modifier){
        if(abilityHolder == null) return 0;
        var specificValue = abilityHolder.data().abilityProperties().get(modifier);

        if(specificValue == null) return 0;
        return specificValue.setValue();
    }

    public static ResourceLocation getAbilityTypeWand(Player player) {
        var res = Helpers.res("");
        if(player == null) return res;
        var itemInHand = Helpers.getUsedItem(player);

        if (!(itemInHand.getItem() instanceof WandItem)) return res;
        var abilityName = itemInHand.get(WAND_DATA.get());

        if (abilityName == null) return res;
        return Helpers.res(abilityName.selectedAbility());
    }

    public static ResourceLocation getAbilityTypePlayer(Player player) {
//        return Helpers.res(player.getData(AttachmentReg.CASTER_DATA).getSelectedAbility());
        Helpers.syncAbilities();
        var data = player.getData(AttachmentReg.CASTER_DATA).getSelectedAbility();
        return Helpers.res(data);
    }
}
