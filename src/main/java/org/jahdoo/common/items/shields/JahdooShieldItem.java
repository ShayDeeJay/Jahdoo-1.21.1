package org.jahdoo.common.items.shields;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.BaseItem;
import org.jahdoo.common.registers.ComponentReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
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
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getBronzeCoin());
    }

    @Override
    public void implicitModifiers(ItemStack stack, List<Component> tooltipComponents) {
        super.implicitModifiers(stack, tooltipComponents);
        var blockChance = stack.get(ComponentReg.SHIELD_BLOCK_CHANCE);
        if(blockChance != null){
            var newValue = MathHelpers.roundNonWholeDouble(MathHelpers.doubleFormattedDouble(blockChance)) + "%";
            var value = TextHelpers.withStyleComponent(newValue + " Block Chance", ColourHelpers.getGoldCoin());
            tooltipComponents.add(value);
        }
    }
}
