package org.jahdoo.common.items.shields;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.ComponentReg;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;
import static org.jahdoo.ascension.utils.Maths.roundNonWholeDouble;

public class JahdooShieldItem extends Item implements JahdooItem, ICurioItem {

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
        return Helpers.withStyleComponent(super.getName(stack).getString(), ColourStore.BRONZE_COIN);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        this.appendItemToolTips(stack, context, tooltipComponents, false);
        var blockChance = stack.get(ComponentReg.SHIELD_BLOCK_CHANCE);
        if(blockChance != null){
            var newValue = roundNonWholeDouble(doubleFormattedDouble(blockChance)) + "%";
            var value = Helpers.withStyleComponent(newValue + " Block Chance", ColourStore.GOLD_COIN);
            var prefix = Helpers.withStyleComponent("Implicit Modifiers", ColourStore.SUB_HEADER_COLOUR);

            tooltipComponents.add(Component.literal(" "));
            tooltipComponents.add(prefix);
            tooltipComponents.add(value);
            runeSpacer(stack, tooltipComponents);
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
