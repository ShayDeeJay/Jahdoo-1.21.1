package org.jahdoo.common.items.shields;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.BaseItem;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class JahdooShieldItem extends BaseItem implements ICurioItem {

    public JahdooShieldItem() {
        super(
            new Properties()
                .stacksTo(1)
                .durability(300)
                .component(ComponentReg.JAHDOO_RARITY, 0)
        );
    }

    @Override
    public Component getName(ItemStack stack) {
        return JahdooHelpers.withStyleComponent(super.getName(stack).getString(), ColourStore.BRONZE_COIN);
    }

    @Override
    public void implicitModifiers(ItemStack stack, List<Component> tooltipComponents) {
        super.implicitModifiers(stack, tooltipComponents);
        var blockChance = stack.get(ComponentReg.SHIELD_BLOCK_CHANCE);
        if(blockChance != null){
            var newValue = org.shaydee.shaydeeapi.Maths.roundNonWholeDouble(org.shaydee.shaydeeapi.Maths.doubleFormattedDouble(blockChance)) + "%";
            var value = JahdooHelpers.withStyleComponent(newValue + " Block Chance", ColourStore.GOLD_COIN);
            tooltipComponents.add(value);
        }
    }
}
