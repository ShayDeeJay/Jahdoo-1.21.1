package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

public class OverenchantedBook extends EnchantedBookItem implements JahdooItem {

    public OverenchantedBook() {
        super(
            new Item.Properties()
                .stacksTo(1)
                .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getChampionGold());
    }

    @Override
    public String descriptionId() {
        return "description."+this.getDescriptionId();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        JahdooItem.tooltipWithoutType(tooltipComponents, false, false, context.level(), stack.get(DataComponents.STORED_ENCHANTMENTS));
    }
}
