package org.jahdoo.common.components;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.wand.WandData;

import static org.jahdoo.common.registers.ComponentReg.ABILITY_HOLDER;
import static org.jahdoo.common.registers.ComponentReg.WAND_DATA;

public class DataComponentHelper {

    public static void setAbilityTypeItemStack(ItemStack itemStack, String ability) {
        itemStack.update(WAND_DATA.get(), WandData.DEFAULT, data -> data.setSelectedAbility(ability));
    }

    public static boolean hasWandAbilitiesTag(ItemStack itemStack){
        return itemStack.get(ABILITY_HOLDER.get()) != null;
    }

    public static String getAbilityTypeItemStack(ItemStack itemStack) {
        var wandData = itemStack.get(WAND_DATA.get());
        if(wandData != null) return wandData.selectedAbility();

        return "";
    }

}
